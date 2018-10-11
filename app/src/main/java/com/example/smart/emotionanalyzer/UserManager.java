package com.example.smart.emotionanalyzer;


import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserManager {

    private FirebaseUser user;
    private DatabaseReference userRef;


    public UserManager() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
    }

    public void addUserInfo() {
        String name = getName();
        User newUser = new User(name);
        userRef.child(getUserID()).setValue(newUser);
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    public String getName() {
        return user.getDisplayName();
    }

    public String getUserID() {
        return user.getUid();
    }

    public String getEmail() {
        return user.getEmail();
    }
}
