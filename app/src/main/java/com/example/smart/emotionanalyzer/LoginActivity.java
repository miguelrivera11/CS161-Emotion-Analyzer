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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Intent mainIntent;
    private String userEmail;
    private String userPassword;
    private String userName;
    private boolean reauthenticating;
    private ActivityManager activityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        activityManager = new ActivityManager(this);
        setContentView(R.layout.activity_login);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            reauthenticating = false;
        }
        else {
            reauthenticating = true;
        }
        // Set up the login form.
        final EditText mEmailView = (EditText) findViewById(R.id.email);

        final EditText mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                userPassword = mPasswordView.getText().toString();
                userEmail = mEmailView.getText().toString();
                if (userEmail.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter an email",
                            Toast.LENGTH_SHORT).show();
                }
                else if (userPassword.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter a password",
                            Toast.LENGTH_SHORT).show();
                }
                else if(reauthenticating) {
                    reauthenticate(userEmail, userPassword);
                }
                else{
                    attemptLogin(userEmail, userPassword);
                }
            }
        });

        Button registerButton = (Button) findViewById(R.id.register);
        if (reauthenticating) {
            registerButton.setVisibility(View.INVISIBLE);
        }
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activityManager.changeActivty(RegisterActivity.class, null);
            }
        });

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
        if (user !=null && !reauthenticating) {
            Bundle bundle = new Bundle();
            bundle.putString("fragment", "MainFeedFragment");
            activityManager.changeActivty(MainActivity.class, bundle);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Bundle bundle = new Bundle();
                            bundle.putString("fragment", "MainFeedFragment");
                            activityManager.changeActivty(MainActivity.class, bundle);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void reauthenticate(String email, String userPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, userPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    activityManager.changeActivty(EditAccountActivity.class, getIntent().getExtras());
                }
                else {
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

