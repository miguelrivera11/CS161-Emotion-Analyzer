package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditAccountActivity extends AppCompatActivity {
    FirebaseUser user;
    boolean changingPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        user = FirebaseAuth.getInstance().getCurrentUser();
        final EditText nameField = findViewById(R.id.name_edit);
        final EditText emailField = findViewById(R.id.email_edit);
        final CheckBox changePassword = findViewById(R.id.change_password);
        final TextView newPasswordText = findViewById(R.id.new_password_text);
        final EditText newPasswordField = findViewById(R.id.new_password_edit);
        final TextView confirmPasswordText = findViewById(R.id.confirm_password_text);
        final EditText confirmPasswordField = findViewById(R.id.confirm_password_edit);
        Button confirmChange = findViewById(R.id.confirm_changes);

        nameField.setText(user.getDisplayName());
        emailField.setText(user.getEmail());
        changePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changingPassword = true;
                    newPasswordField.setVisibility(View.VISIBLE);
                    newPasswordText.setVisibility(View.VISIBLE);
                    confirmPasswordField.setVisibility(View.VISIBLE);
                    confirmPasswordText.setVisibility(View.VISIBLE);
                }
                else {
                    changingPassword = false;
                    newPasswordField.setVisibility(View.INVISIBLE);
                    newPasswordText.setVisibility(View.INVISIBLE);
                    confirmPasswordField.setVisibility(View.INVISIBLE);
                    confirmPasswordText.setVisibility(View.INVISIBLE);
                }
            }
        });

        confirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = newPasswordField.getText().toString();
                String confirmPassword = confirmPasswordField.getText().toString();
                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                boolean success = true;
                if (changingPassword) {
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(EditAccountActivity.this, "Passwords do not match",
                                Toast.LENGTH_SHORT).show();
                        success = false;
                    }
                    else if (isPasswordValid(password)) {
                        user.updatePassword(password);
                        success = true;
                    }
                    else {
                        success = false;
                    }
                }
                String oldEmail = user.getEmail();
                if(!email.equals(user.getEmail()) && success) {
                    if(isEmailValid(email)) {
                        user.updateEmail(email);
                        success = success && true;
                    }
                    else {
                        success = false;
                    }
                }
                if(!name.equals(user.getDisplayName())&& success) {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    user.updateProfile(profileChangeRequest)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                }
                if(success) {
                    sendToAccount();
                }

            }
        });

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
            Toast.makeText(EditAccountActivity.this, invalidMessage,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isEmailValid(String email) {
        if (email.length() >= 7 && email.contains("@") && email.contains(".")) {
            return true;
        }
        else {
            String invalidMessage = getString(R.string.error_invalid_email);
            Toast.makeText(EditAccountActivity.this, invalidMessage,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void sendToAccount() {
        final Bundle bundle = getIntent().getExtras();
        bundle.putString("fragment", "account");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(EditAccountActivity.this, MainActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 1000); // will be delayed for 1 second to give time for databse to update
    }

}
