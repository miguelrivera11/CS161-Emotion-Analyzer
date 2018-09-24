package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private String email;
    private String name;
    private String password;
    private String confirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        final EditText emailView = (EditText) findViewById(R.id.email);
        final EditText nameView = (EditText) findViewById(R.id.name);
        final EditText passwordView = (EditText) findViewById(R.id.password);
        final EditText confirmPasswordView = (EditText) findViewById(R.id.confirm_password);
        database = FirebaseDatabase.getInstance();

        Button registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailView.getText().toString();
                password = passwordView.getText().toString();
                confirmPassword = confirmPasswordView.getText().toString();
                name = nameView.getText().toString();
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match",
                            Toast.LENGTH_SHORT).show();
                }
                else if (isEmailValid(email) && isPasswordValid(password)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                        user.updateProfile(profileChangeRequest)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                    }
                                                });
                                        User newUser = new User(name);
                                        DatabaseReference usersReference = database.getReference("Users");
                                        usersReference.child(user.getUid()).setValue(newUser);


                                        sendToMain();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        Bundle userBundle = new Bundle();
        userBundle.putString("name", name);
        mainIntent.putExtras(userBundle);
        startActivity(mainIntent);
        finish();
    }

    private boolean isEmailValid(String email) {
        if (email.length() >= 7 && email.contains("@") && email.contains(".")) {
            return true;
        }
        else {
            String invalidMessage = getString(R.string.error_invalid_email);
            Toast.makeText(RegisterActivity.this, invalidMessage,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        if (password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*")) {
            return true;
        }
        else {
            String invalidMessage = "";
            if (password.length() < 8) {
                invalidMessage = "Password is too short";
            }
            else if (!password.matches(".*[A-Z].*")) {
                invalidMessage = "Password must have a capital letter";
            }
            else {
                invalidMessage = "Password must have a numeral";
            }
            Toast.makeText(RegisterActivity.this, invalidMessage,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
