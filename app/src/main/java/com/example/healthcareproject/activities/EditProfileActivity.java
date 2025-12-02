package com.example.healthcareproject.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcareproject.R;
import com.example.healthcareproject.models.User;
import com.example.healthcareproject.utils.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private DatabaseReference databaseReference;
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etDob;
    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnSave;
    private ImageView ivBack;

    private String userUid;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        preferenceManager = new PreferenceManager(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userUid = getIntent().getStringExtra("user_uid");
        if (userUid == null || userUid.isEmpty()) {
            userUid = preferenceManager.getString("user_uid", "");
        }

        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSave = findViewById(R.id.btnSave);
        ivBack = findViewById(R.id.ivBack);
    }

    private void loadUserData() {
        if (userUid.isEmpty()) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference.child("users").child(userUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentUser = snapshot.getValue(User.class);
                            if (currentUser != null) {
                                populateFields(currentUser);
                            }
                        } else {
                            Toast.makeText(EditProfileActivity.this,
                                    "User data not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditProfileActivity.this,
                                "Failed to load user data: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateFields(User user) {
        etName.setText(user.getName() != null ? user.getName() : "");
        etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        etDob.setText(user.getDob() != null ? user.getDob() : "");
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveUserData());

        etDob.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDob.setText(date);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void saveUserData() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        boolean isChangingPassword = !currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty();

        if (isChangingPassword) {
            if (currentPassword.isEmpty()) {
                etCurrentPassword.setError("Current password is required");
                etCurrentPassword.requestFocus();
                return;
            }

            if (newPassword.isEmpty()) {
                etNewPassword.setError("New password is required");
                etNewPassword.requestFocus();
                return;
            }

            if (newPassword.length() < 6) {
                etNewPassword.setError("Password must be at least 6 characters");
                etNewPassword.requestFocus();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                return;
            }

            if (currentUser != null && currentUser.getPassword() != null) {
                if (!currentPassword.equals(currentUser.getPassword())) {
                    etCurrentPassword.setError("Current password is incorrect");
                    etCurrentPassword.requestFocus();
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "Unable to verify current password", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("phone", phone);
        updates.put("dob", dob);

        if (isChangingPassword) {
            updates.put("password", newPassword);
        }

        databaseReference.child("users").child(userUid)
                .updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    if (isChangingPassword) {
                        Toast.makeText(EditProfileActivity.this,
                                "Profile and password updated successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this,
                            "Failed to update profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}