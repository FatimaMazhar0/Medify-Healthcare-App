package com.example.healthcareproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcareproject.R;
import com.example.healthcareproject.models.BloodDonor;
import com.example.healthcareproject.models.DoctorRequest;
import com.example.healthcareproject.utils.FirebaseClient;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etPhone, etDob;
    RadioGroup rgUserType;
    Button btnRegister;
    TextView tvGoToLogin;
    LinearLayout layoutBloodDonor;
    Spinner spinnerBloodGroup;
    CheckBox cbBloodDonor;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDOB);
        rgUserType = findViewById(R.id.rgUserType);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        layoutBloodDonor = findViewById(R.id.layoutBloodDonor);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        cbBloodDonor = findViewById(R.id.cbBloodDonor);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);


        rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPatient) {
                layoutBloodDonor.setVisibility(View.VISIBLE);
            } else {
                layoutBloodDonor.setVisibility(View.GONE);
            }
        });

        btnRegister.setOnClickListener(v -> registerUser());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedType = rgUserType.getCheckedRadioButtonId();
        if (selectedType == -1) {
            Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rb = findViewById(selectedType);
        String type = rb.getText().toString().toUpperCase();


        if (type.equals("PATIENT")) {
            String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();
            if (bloodGroup.equals("Select Blood Group") || bloodGroup.equals("All")) {
                Toast.makeText(this, "Please select your blood group", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        btnRegister.setEnabled(false);
        btnRegister.setText("Registering...");


        String uid = FirebaseClient.getInstance().getRealtimeDB()
                .child("users")
                .push()
                .getKey();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("password", password);
        userMap.put("phone", phone);
        userMap.put("dob", dob);
        userMap.put("userType", type);


        if (type.equals("PATIENT")) {
            String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();
            boolean isBloodDonor = cbBloodDonor.isChecked();

            userMap.put("bloodGroup", bloodGroup);
            userMap.put("isBloodDonor", isBloodDonor);

            if (isBloodDonor) {
                addBloodDonor(uid, name, bloodGroup, phone, email);
            }

            registerUserDirectly(uid, userMap, type);
        }
        else if (type.equals("DOCTOR")) {
            createDoctorRequest(uid, name, email, password, phone, dob);
        }
    }

    private void registerUserDirectly(String uid, Map<String, Object> userMap, String userType) {
        FirebaseClient.getInstance().getRealtimeDB()
                .child("users")
                .child(uid)
                .setValue(userMap)
                .addOnSuccessListener(a -> {
                    sessionManager.saveUserSession(uid,
                            (String) userMap.get("name"),
                            (String) userMap.get("email"),
                            (String) userMap.get("phone"),
                            userType);

                    Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show();

                    redirectToAppropriatePage(userType);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    resetRegisterButton();
                });
    }

    private void createDoctorRequest(String doctorId, String name, String email, String password,
                                     String phone, String dob) {
        DatabaseReference requestsRef = FirebaseClient.getInstance().getRealtimeDB()
                .child("doctor_requests");

        String requestId = requestsRef.push().getKey();

        DoctorRequest request = new DoctorRequest();
        request.setRequestId(requestId);
        request.setDoctorId(doctorId);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setPhone(phone);
        request.setDob(dob);
        request.setSpecialization("General");
        request.setExperienceYears(0);
        request.setTotalPatients(0);
        request.setRating(0.0f);
        request.setFee("0 PKR");
        request.setAbout("New doctor awaiting approval");
        request.setAvailableDates(new ArrayList<>());
        request.setAvailableTimes(new ArrayList<>());

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestId", request.getRequestId());
        requestMap.put("doctorId", request.getDoctorId());
        requestMap.put("name", request.getName());
        requestMap.put("email", request.getEmail());
        requestMap.put("password", request.getPassword());
        requestMap.put("phone", request.getPhone());
        requestMap.put("dob", request.getDob());
        requestMap.put("specialization", request.getSpecialization());
        requestMap.put("experienceYears", request.getExperienceYears());
        requestMap.put("totalPatients", request.getTotalPatients());
        requestMap.put("rating", request.getRating());
        requestMap.put("fee", request.getFee());
        requestMap.put("about", request.getAbout());
        requestMap.put("availableDates", request.getAvailableDates());
        requestMap.put("availableTimes", request.getAvailableTimes());
        requestMap.put("createdAt", request.getCreatedAt());
        requestMap.put("status", request.getStatus());

        requestsRef.child(requestId).setValue(requestMap)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this,
                            "Doctor registration request sent to admin for approval. You will be notified once approved.",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit request: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    resetRegisterButton();
                });
    }

    private void addBloodDonor(String patientId, String name, String bloodGroup, String phone, String email) {
        String donorId = FirebaseClient.getInstance().getRealtimeDB()
                .child("bloodDonors")
                .push()
                .getKey();

        BloodDonor donor = new BloodDonor(donorId, patientId, name, bloodGroup, phone, email);

        Map<String, Object> donorMap = new HashMap<>();
        donorMap.put("donorId", donor.getDonorId());
        donorMap.put("patientId", donor.getPatientId());
        donorMap.put("name", donor.getName());
        donorMap.put("bloodGroup", donor.getBloodGroup());
        donorMap.put("phone", donor.getPhone());
        donorMap.put("email", donor.getEmail());
        donorMap.put("available", donor.isAvailable());
        donorMap.put("lastDonationDate", donor.getLastDonationDate());

        FirebaseClient.getInstance().getRealtimeDB()
                .child("bloodDonors")
                .child(donorId)
                .setValue(donorMap)
                .addOnFailureListener(e -> {
                    System.err.println("Failed to add blood donor: " + e.getMessage());
                });
    }

    private void redirectToAppropriatePage(String userType) {
        Intent intent;

        if ("DOCTOR".equals(userType)) {
            intent = new Intent(RegisterActivity.this, DoctorMainPageActivity.class);
        } else if ("ADMIN".equals(userType)) {
            intent = new Intent(RegisterActivity.this, AdminMainPageActivity.class);
        } else {
            intent = new Intent(RegisterActivity.this, MainPageActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void resetRegisterButton() {
        btnRegister.setEnabled(true);
        btnRegister.setText("Register");
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetRegisterButton();
    }
}