package com.example.healthcareproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.healthcareproject.R;
import com.example.healthcareproject.activities.EditProfileActivity;
import com.example.healthcareproject.activities.LoginActivity;
import com.example.healthcareproject.activities.NotificationsActivity;
import com.example.healthcareproject.activities.PrivacyActivity;
import com.example.healthcareproject.models.User;
import com.example.healthcareproject.utils.PreferenceManager;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {

    private PreferenceManager preferenceManager;
    private DatabaseReference databaseReference;

    private TextView tvUserName;
    private TextView tvUserEmail;
    private ImageView ivProfile;

    private LinearLayout settingProfile;
    private LinearLayout settingNotifications;
    private LinearLayout settingPrivacy;
    private LinearLayout settingHelp;
    private LinearLayout settingAbout;
    private LinearLayout settingLogout;

    SessionManager sessionManager;

    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferenceManager = new PreferenceManager(requireContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sessionManager = new SessionManager(requireContext());

        initializeViews(view);
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        ivProfile = view.findViewById(R.id.ivProfile);

        settingProfile = view.findViewById(R.id.settingProfile);
        settingNotifications = view.findViewById(R.id.settingNotifications);
        settingPrivacy = view.findViewById(R.id.settingPrivacy);
        settingHelp = view.findViewById(R.id.settingHelp);
        settingAbout = view.findViewById(R.id.settingAbout);
        settingLogout = view.findViewById(R.id.settingLogout);
    }

    private void loadUserData() {
        String uid = sessionManager.getUserId();

        if (uid != null && !uid.isEmpty()) {
            databaseReference.child("users").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                currentUser = snapshot.getValue(User.class);
                                if (currentUser != null) {
                                    updateUI(currentUser);
                                }
                            } else {
                                Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireContext(), "Failed to load user data: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(User user) {
        if (user != null) {
            tvUserName.setText(user.getName() != null ? user.getName() : "User");
            tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "");

        }
    }

    private void setupClickListeners() {
        settingProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            if (currentUser != null) {
                intent.putExtra("user_uid", currentUser.getUid());
            }
            startActivity(intent);
        });

        settingNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationsActivity.class);
            startActivity(intent);
        });

        settingPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PrivacyActivity.class);
            startActivity(intent);
        });

        settingHelp.setOnClickListener(v -> showHelpDialog());

        settingAbout.setOnClickListener(v -> showAboutDialog());

        settingLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Help & Support")
                .setMessage("Need help?\n\n" +
                        "• Email: support@healthcareapp.com\n" +
                        "• Phone: +1 (555) 123-4567\n" +
                        "• Website: www.healthcareapp.com/support\n\n" +
                        "We're here to help you 24/7!")
                .setPositiveButton("Contact Support", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("About")
                .setMessage("HealthCare App v1.0.0\n\n" +
                        "A modern healthcare management application designed to connect patients with healthcare providers.\n\n" +
                        "© 2024 HealthCare Inc.\n" +
                        "All rights reserved.\n\n" +
                        "Privacy Policy | Terms of Service")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        preferenceManager.clearAll();
        sessionManager.logout();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }
}