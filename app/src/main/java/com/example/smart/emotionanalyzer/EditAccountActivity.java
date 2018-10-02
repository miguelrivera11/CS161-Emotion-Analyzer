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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditAccountActivity extends AppCompatActivity {
    FirebaseUser user;
    boolean changingPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

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
                final String name = nameField.getText().toString();
                final String email = emailField.getText().toString();
                final boolean nameChange = !user.getDisplayName().equals(name);
                final boolean emailChange = !user.getEmail().equals(email);
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
                if(emailChange && success) {
                    if(isEmailValid(email)) {
                        user.updateEmail(email);
                        success = success && true;
                    }
                    else {
                        success = false;
                    }
                }
                if(nameChange && success) {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    user.updateProfile(profileChangeRequest)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful() && nameChange) {
                                        updateDatabase(name);
                                    }
                                }
                            });
                }
                if(success) {
                    sendToAccount();
                }

            }
        });

    }

    private void updateDatabase(final String name) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users/" + user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ArrayList<String> commentedTopics = user.getCommentedTopics();
                updateTopicsAndComments(database, commentedTopics, name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateTopicsAndComments(FirebaseDatabase database, final ArrayList<String> commentedTopics, final String name) {
        final DatabaseReference topicsRef = database.getReference("Topics");
        final String userID = user.getUid();
        topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Topic topic = child.getValue(Topic.class);
                    boolean changed = false;
                    if (topic.getCreatorID().equals(userID)) {
                        topic.setCreator(name);
                        changed = true;
                    }
                    if (commentedTopics.contains(topic.getTopic())) {
                        for (Comment comment: topic.getComments()) {
                            if (comment.getCreatorID().equals(userID)) {
                                comment.setCreator(name);
                            }
                            for (Comment reply: comment.getReplies()) {
                                if (reply.getCreatorID().equals(userID)) {
                                    reply.setCreator(name);
                                }
                            }
                        }
                        changed = true;
                    }
                    if (changed) {
                        topicsRef.child(child.getKey()).setValue(topic);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        h.postDelayed(r, 3000); // will be delayed for 3 seconds to give time for databse to update
    }

}
