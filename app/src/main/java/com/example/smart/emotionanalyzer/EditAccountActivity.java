package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditAccountActivity extends AppCompatActivity {
    private FirebaseUser user;
    private boolean changingPassword = false;
    private UserManager userManager;
    private TopicDatabaseManager topicManager;
    private ActivityManager activityManager;
    private ImageView profilePic;
    private Uri profilePictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        topicManager = new TopicDatabaseManager(this);
        userManager = new UserManager();
        activityManager = new ActivityManager(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        final String oldEmail = user.getEmail();
        final String oldName = user.getDisplayName();
        final EditText nameField = findViewById(R.id.name_edit);
        final EditText emailField = findViewById(R.id.email_edit);
        final CheckBox changePassword = findViewById(R.id.change_password);
        final TextView newPasswordText = findViewById(R.id.new_password_text);
        final EditText newPasswordField = findViewById(R.id.new_password_edit);
        final TextView confirmPasswordText = findViewById(R.id.confirm_password_text);
        final EditText confirmPasswordField = findViewById(R.id.confirm_password_edit);
        Button cancel = findViewById(R.id.cancel);
        Button confirmChange = findViewById(R.id.confirm_changes);
        TextView selectPic = findViewById(R.id.select_pic);
        profilePic = findViewById(R.id.profile_pic);

        newPasswordField.setVisibility(View.INVISIBLE);
        newPasswordText.setVisibility(View.INVISIBLE);
        confirmPasswordField.setVisibility(View.INVISIBLE);
        confirmPasswordText.setVisibility(View.INVISIBLE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("fragment", "MainFeedFragment");
                Intent intent = new Intent(EditAccountActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        userManager.displayProfilePicture(profilePic, this, userManager.getUserID());
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
                final String name = nameField.getText().toString();
                final String email = emailField.getText().toString();
                final boolean nameChange = !userManager.getName().equals(name);
                final boolean emailChange = !userManager.getEmail().equals(email);
                boolean success = true;
                if (changingPassword) {
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(EditAccountActivity.this, "Passwords do not match",
                                Toast.LENGTH_SHORT).show();
                        success = false;
                    }
                    else if (isPasswordValid(password)) {
                        userManager.updatePassword(password);
                        success = true;
                    }
                    else {
                        success = false;
                    }
                }
                if(emailChange && success) {
                    if(isEmailValid(email)) {
                        userManager.updateEmail(email);
                        success = success && true;
                    }
                    else {
                        success = false;
                    }
                }
                if(nameChange && success) {
                    userManager.updateName(name);
                    topicManager.updateDatabaseforNameChange(name);
                }
                if (profilePictureUri != null) {
                    userManager.updateProfilePicture(profilePictureUri);
                }
                if(success) {
                    Bundle bundle = getIntent().getExtras();
                    bundle.putString("fragment", "account");
                    activityManager.changeActivityWithDelay(MainActivity.class, bundle, 3000);
                }

            }
        });

        selectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityManager.getPictureFromGallery();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            profilePictureUri = data.getData();
            profilePic.setImageURI(profilePictureUri);
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
}
