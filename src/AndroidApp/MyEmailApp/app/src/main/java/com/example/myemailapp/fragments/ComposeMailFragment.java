package com.example.myemailapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myemailapp.R;
import com.example.myemailapp.viewmodel.ComposeMailViewModel;



public class ComposeMailFragment extends Fragment {

    private EditText editTextTo, editTextSubject, editTextBody;
    private ComposeMailViewModel viewModel;

    private String draftId = null;

    public ComposeMailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_mail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextTo = view.findViewById(R.id.editTextTo);
        editTextSubject = view.findViewById(R.id.editTextSubject);
        editTextBody = view.findViewById(R.id.editTextBody);
        ImageButton buttonSend = view.findViewById(R.id.buttonSend);
        ImageButton buttonBack = view.findViewById(R.id.buttonBack);
        ImageButton buttonExit = view.findViewById(R.id.buttonExit);

        viewModel = new ViewModelProvider(this).get(ComposeMailViewModel.class);

        Bundle args = getArguments();
        if (args != null) {
            String to = args.getString("to");
            String subject = args.getString("subject");
            String body = args.getString("body");
            draftId = args.getString("draftId");

            if (!TextUtils.isEmpty(to)) {
                editTextTo.setText(to);
            }
            if (!TextUtils.isEmpty(subject)) {
                editTextSubject.setText(subject);
            }
            if (!TextUtils.isEmpty(body)) {
                editTextBody.setText(body);
            }
        }


        buttonSend.setOnClickListener(v -> {
            String[] to = editTextTo.getText().toString().trim().split("\\s*,\\s*");
            String subject = editTextSubject.getText().toString().trim();
            String body = editTextBody.getText().toString().trim();

            if (to.length == 0 || TextUtils.isEmpty(to[0])) {
                Toast.makeText(getContext(), "Recipient is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (draftId != null) {
                viewModel.sendDraftAsMail(to, subject, body, draftId);
                Toast.makeText(getContext(), "Draft sent!", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.sendMail(to, subject, body);
                Toast.makeText(getContext(), "Mail sent!", Toast.LENGTH_SHORT).show();
            }
            requireActivity().onBackPressed();
        });

        buttonBack.setOnClickListener(v -> {
            String[] to = editTextTo.getText().toString().trim().split("\\s*,\\s*");
            String subject = editTextSubject.getText().toString().trim();
            String body = editTextBody.getText().toString().trim();

            if (to.length == 0 || TextUtils.isEmpty(to[0])) {
                Toast.makeText(getContext(), "Recipient is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (draftId != null) {
                viewModel.updateDraft(to, subject, body, draftId);
                Toast.makeText(getContext(), "Draft updated!", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.createDraft(to, subject, body);
                Toast.makeText(getContext(), "Draft created!", Toast.LENGTH_SHORT).show();
            }
            requireActivity().onBackPressed();
        });

        buttonExit.setOnClickListener(v -> requireActivity().onBackPressed());
    }
}
