package com.example.smart.emotionanalyzer;


import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserManager{

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;


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

}
