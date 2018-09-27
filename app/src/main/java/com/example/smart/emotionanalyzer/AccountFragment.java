package com.example.smart.emotionanalyzer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.view.View.OnClickListener;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.ProgressDialog;
import android.widget.Toast;

import java.util.List;


public class AccountFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, null);
        Button logout = view.findViewById(R.id.logout);
        Button editAccount = view.findViewById(R.id.edit_account);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        TextView nameDisplay = view.findViewById(R.id.display_name);
        TextView emailDisplay = view.findViewById(R.id.display_email);
        //TODO: Fix null object reference when going back from create a topic
        String userName = user.getDisplayName();
        String userEmail = user.getEmail();
        nameDisplay.setText("Name: " + userName);
        emailDisplay.setText("Email: " + userEmail);

        editAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLoginToReauthenticate();;
            }
        });

        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                sendToLogin();
            }
        });

        return view;
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
        getActivity().finish();
    }

    private void sendToLoginToReauthenticate() {
        Bundle bundle = getActivity().getIntent().getExtras();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().finish();
    }



}
