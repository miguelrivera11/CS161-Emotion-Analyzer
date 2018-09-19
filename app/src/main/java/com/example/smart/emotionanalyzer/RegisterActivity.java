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

public class RegisterActivity extends AppCompatActivity {
    String email;
    String name;
    String password;
    String confirmPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        final EditText emailView = (EditText) findViewById(R.id.email);
        EditText nameView = (EditText) findViewById(R.id.name);
        final EditText passwordView = (EditText) findViewById(R.id.password);
        final EditText confirmPasswordView = (EditText) findViewById(R.id.confirm_password);
        Button registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailView.getText().toString();
                do {
                    password = passwordView.getText().toString();
                    confirmPassword = confirmPasswordView.getText().toString();
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                while (!password.equals(confirmPassword));
                if (isEmailValid(email) && isPasswordValid(password)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
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
            String invalidMessage = getString(R.string.error_invalid_password);
            Toast.makeText(RegisterActivity.this, invalidMessage,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}