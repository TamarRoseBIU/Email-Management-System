package com.example.myemailapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myemailapp.BuildConfig;
import com.example.myemailapp.R;

public class ProfileFragment extends Fragment {

    private static final String BASE_URL = BuildConfig.BASE_URL_NO_API; // Replace with actual base URL

    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView phoneTextView;
    private TextView birthDateTextView;
    private TextView genderTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = view.findViewById(R.id.profile_image);
        nameTextView = view.findViewById(R.id.name_text);
        usernameTextView = view.findViewById(R.id.username_text);
        phoneTextView = view.findViewById(R.id.phone_text);
        birthDateTextView = view.findViewById(R.id.birthdate_text);
        genderTextView = view.findViewById(R.id.gender_text);

        loadUserData();

        return view;
    }

//    private void loadUserData() {
//        SharedPreferences prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
//
//        String profilePic = prefs.getString("profilePic", "");
//        String username = prefs.getString("username", "");
//        String firstName = prefs.getString("firstName", "");
//        String lastName = prefs.getString("lastName", "");
//        String phone = prefs.getString("phoneNumber", "");
//        String birthDate = prefs.getString("birthDate", "");
//        String gender = prefs.getString("gender", "");
//
//        if (!profilePic.isEmpty()) {
//            String fullUrl = BASE_URL + profilePic;
//            Glide.with(this)
//                    .load(fullUrl)
//                    .placeholder(R.mipmap.ic_launcher_round)
//                    .error(R.mipmap.ic_launcher_round)
//                    .circleCrop()
//                    .into(profileImageView);
//        }
//
//        nameTextView.setText(String.format("%s %s", firstName, lastName));
//        usernameTextView.setText("@" + username);
//        phoneTextView.setText(phone);
//        birthDateTextView.setText(birthDate);
//        genderTextView.setText(gender);
//    }
private void loadUserData() {
    SharedPreferences prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);

    String profilePic = prefs.getString("profilePic", "");
    String username = prefs.getString("username", "");
    String firstName = prefs.getString("firstName", "");
    String lastName = prefs.getString("lastName", "");
    String phone = prefs.getString("phoneNumber", "");
    String birthDate = prefs.getString("birthDate", "");
    if (!birthDate.isEmpty()) {
        // Extract just the date part (2000-01-01)
        String formattedDate = birthDate.substring(0, 10);
        // Or convert to a more readable format
        String[] parts = formattedDate.split("-");
        String readableDate = parts[2] + "/" + parts[1] + "/" + parts[0]; // DD/MM/YYYY
        birthDate = readableDate;
    }

    String gender = prefs.getString("gender", "");

    if (!profilePic.isEmpty()) {
        String fullUrl = BASE_URL + profilePic;
        Glide.with(this)
                .load(fullUrl)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .circleCrop()
                .into(profileImageView);
    }

    nameTextView.setText(getString(R.string.name_format, firstName, lastName));
    usernameTextView.setText(getString(R.string.username_format, username));
    phoneTextView.setText(getString(R.string.phone_format, phone));
    birthDateTextView.setText(getString(R.string.birthdate_format, birthDate));
    genderTextView.setText(getString(R.string.gender_format, gender));
}

}
