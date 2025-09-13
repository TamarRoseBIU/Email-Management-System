package com.example.myemailapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myemailapp.R;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Get username from SharedPreferences
        String username = getString(R.string.username_default);
        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences("auth", MODE_PRIVATE);
            username = prefs.getString("username", username);
        }

        // Set greeting text
        TextView welcomeText = view.findViewById(R.id.home_welcome_text);
        welcomeText.setText(getString(R.string.home_greeting, username));

        // Set description
        TextView descText = view.findViewById(R.id.home_description);

        // Optional: You can set or adjust image dynamically if needed
        ImageView imageView = view.findViewById(R.id.home_image);
        imageView.setImageResource(R.drawable.welcome1);

        return view;
    }
}
