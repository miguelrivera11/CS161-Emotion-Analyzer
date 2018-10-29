package com.example.smart.emotionanalyzer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;

import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;


public class AccountFragment extends Fragment {
    private UserManager userManager;
    private ActivityManager activityManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, null);
        userManager = new UserManager();
        activityManager = new ActivityManager(getActivity());

        Button logout = view.findViewById(R.id.logout);
        Button editAccount = view.findViewById(R.id.edit_account);
        Button topicButton = view.findViewById(R.id.view_created_button);

        TextView nameDisplay = view.findViewById(R.id.display_name);
        TextView emailDisplay = view.findViewById(R.id.display_email);
        nameDisplay.setText("Name: " + userManager.getName());
        emailDisplay.setText("Email: " + userManager.getEmail());
        ImageView profilePicture = view.findViewById(R.id.profile_pic);
        Bitmap bitmap;
            userManager.displayProfilePicture(profilePicture, getActivity(), userManager.getUserID());

        editAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activityManager.changeActivty(LoginActivity.class, getActivity().getIntent().getExtras());
            }
        });

        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.logout();
                activityManager.changeActivty(LoginActivity.class, null);
            }
        });

        topicButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getActivity().getIntent().getExtras();
                bundle.putString("fragment", "account");
                activityManager.changeActivty(CreatedTopicsActivity.class, bundle);
            }
        });

        return view;
    }


    private void sendToLoginToReauthenticate() {
        Bundle bundle = getActivity().getIntent().getExtras();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().finish();
    }



}
