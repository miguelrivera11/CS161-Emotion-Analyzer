package com.example.smart.emotionanalyzer;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class UserManager{

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;
    private StorageReference profilePictureRef;


    public UserManager() {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users/" + firebaseUser.getUid());
    }

    public void addUserInfo(String name) {
        User newUser = new User(name);
        userRef.setValue(newUser);
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        firebaseUser.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    public void addCreatedTopic(final String topicID) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userObject = dataSnapshot.getValue(User.class);
                userObject.addCreatedTopic(topicID);
                userRef.setValue(userObject);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String getName() {
        return firebaseUser.getDisplayName();
    }

    public String getUserID() {
        return firebaseUser.getUid();
    }

    public String getEmail() {
        return firebaseUser.getEmail();
    }

    public void logout() {
        mAuth.signOut();
    }

    public DatabaseReference getUserRef() {
        return userRef;
    }

    public void updateEmail(String email) {
        firebaseUser.updateEmail(email);
    }

    public void updatePassword(String password) {
        firebaseUser.updatePassword(password);
    }

    public void updateProfilePicture(Uri photo) {
        profilePictureRef.putFile(photo)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    public void displayProfilePicture(final ImageView profilePic, final Activity context, final String userID) {
        try {
            final File localFile = File.createTempFile("images", "jpg");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            profilePictureRef = storageReference.child(userID + "/profilePicture");
            profilePictureRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Uri photoUri = Uri.fromFile(localFile);
                            profilePic.setImageURI(photoUri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (context != null) {
                        Drawable d = context.getResources().getDrawable(android.R.drawable.ic_menu_gallery, null);
                        profilePic.setImageDrawable(d);
                    }
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateName(final String name) {
        userRef.child("name").setValue(name);
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        firebaseUser.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    public void deleteCreatedTopic(final String topicID) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ArrayList<String> createdTopics = user.getCreatedTopics();
                createdTopics.remove(topicID);
                userRef.setValue(user);
                updateOtherUsersForDelete(topicID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateOtherUsersForDelete(final String topicID) {
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    String userID = child.getKey().toString();
                    ArrayList<String> commentedTopics = user.getCommentedTopics();
                    if(user.getCommentedTopics().contains(topicID)) {
                        commentedTopics.remove(topicID);
                        usersRef.child(userID).setValue(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addCommentedTopic(final String topicID) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (!user.getCommentedTopics().contains(topicID)) {
                    user.addCommentedTopic(topicID);
                    userRef.setValue(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



}
