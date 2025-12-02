package com.example.healthcareproject.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.EditHospitalAdapter;
import com.example.healthcareproject.adapters.TimeSlotAdapter;
import com.example.healthcareproject.models.Doctor;
import com.example.healthcareproject.models.Hospital;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditDoctorProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etSpecialization, etExperience, etFee, etAbout;
    private Button btnSaveChanges, btnCancel, btnManageAvailability, btnManageHospitals;
    private RecyclerView rvSelectedHospitals;

    private SessionManager sessionManager;
    private DatabaseReference usersRef, hospitalsRef;
    private ProgressDialog progressDialog;

    private Doctor currentDoctor;
    private List<Hospital> selectedHospitals = new ArrayList<>();
    private List<Hospital> allHospitals = new ArrayList<>();
    private EditHospitalAdapter hospitalAdapter;

    private List<String> availableDays = new ArrayList<>();
    private List<String> availableTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor_profile);

        sessionManager = new SessionManager(this);
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");

        initViews();
        setupProgressDialog();
        setupRecyclerView();
        loadDoctorData();
        loadAllHospitals();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etSpecialization = findViewById(R.id.etSpecialization);
        etExperience = findViewById(R.id.etExperience);
        etFee = findViewById(R.id.etFee);
        etAbout = findViewById(R.id.etAbout);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
        btnManageAvailability = findViewById(R.id.btnManageAvailability);
        btnManageHospitals = findViewById(R.id.btnManageHospitals);

        rvSelectedHospitals = findViewById(R.id.rvSelectedHospitals);
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving changes...");
        progressDialog.setCancelable(false);
    }

    private void setupRecyclerView() {
        hospitalAdapter = new EditHospitalAdapter(selectedHospitals, this, hospital -> {
            // Remove hospital
            removeHospital(hospital);
        });
        rvSelectedHospitals.setLayoutManager(new LinearLayoutManager(this));
        rvSelectedHospitals.setAdapter(hospitalAdapter);
    }

    private void setupListeners() {
        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> finish());
        btnManageAvailability.setOnClickListener(v -> showAvailabilityDialog());
        btnManageHospitals.setOnClickListener(v -> showHospitalSelectionDialog());
    }

    private void loadDoctorData() {
        String doctorId = sessionManager.getUserId();
        if (doctorId == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressDialog.show();
        usersRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    currentDoctor = snapshot.getValue(Doctor.class);
                    if (currentDoctor != null) {
                        populateFields();
                        loadSelectedHospitals();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(EditDoctorProfileActivity.this,
                        "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields() {
        etName.setText(currentDoctor.getName());
        etPhone.setText(currentDoctor.getPhone());
        etSpecialization.setText(currentDoctor.getSpecialization());
        etExperience.setText(String.valueOf(currentDoctor.getExperienceYears()));
        etFee.setText(currentDoctor.getFee());
        etAbout.setText(currentDoctor.getAbout());

        availableDays = currentDoctor.getAvailableDates() != null ?
                new ArrayList<>(currentDoctor.getAvailableDates()) : new ArrayList<>();
        availableTimes = currentDoctor.getAvailableTimes() != null ?
                new ArrayList<>(currentDoctor.getAvailableTimes()) : new ArrayList<>();
    }

    private void loadSelectedHospitals() {
        if (currentDoctor.getHospitalIds() == null || currentDoctor.getHospitalIds().isEmpty()) {
            return;
        }

        selectedHospitals.clear();
        for (String hospitalId : currentDoctor.getHospitalIds()) {
            hospitalsRef.child(hospitalId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Hospital hospital = snapshot.getValue(Hospital.class);
                        if (hospital != null) {
                            selectedHospitals.add(hospital);
                            hospitalAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void loadAllHospitals() {
        hospitalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allHospitals.clear();
                for (DataSnapshot hospitalSnapshot : snapshot.getChildren()) {
                    Hospital hospital = hospitalSnapshot.getValue(Hospital.class);
                    if (hospital != null) {
                        allHospitals.add(hospital);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showAvailabilityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_availability, null);
        builder.setView(dialogView);

        LinearLayout llDays = dialogView.findViewById(R.id.llDays);

        // Days checkboxes
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        List<CheckBox> dayCheckBoxes = new ArrayList<>();

        for (String day : days) {
            CheckBox cb = new CheckBox(this);
            cb.setText(day);
            cb.setChecked(availableDays.contains(day));
            dayCheckBoxes.add(cb);
            llDays.addView(cb);
        }

        // Time slots
        EditText etTimeSlot = dialogView.findViewById(R.id.etTimeSlot);
        Button btnAddTime = dialogView.findViewById(R.id.btnAddTime);
        RecyclerView rvTimes = dialogView.findViewById(R.id.rvTimes);

        // Setup times RecyclerView
        List<String> tempTimes = new ArrayList<>(availableTimes);
        TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(tempTimes, this, position -> {
            tempTimes.remove(position);
            rvTimes.getAdapter().notifyItemRemoved(position);
        });
        rvTimes.setLayoutManager(new LinearLayoutManager(this));
        rvTimes.setAdapter(timeSlotAdapter);

        btnAddTime.setOnClickListener(v -> {
            String time = etTimeSlot.getText().toString().trim();
            if (!time.isEmpty() && !tempTimes.contains(time)) {
                tempTimes.add(time);
                timeSlotAdapter.notifyItemInserted(tempTimes.size() - 1);
                etTimeSlot.setText("");
            } else if (time.isEmpty()) {
                Toast.makeText(this, "Please enter a time slot", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Time slot already exists", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Save", (dialog, which) -> {
            availableDays.clear();
            for (CheckBox cb : dayCheckBoxes) {
                if (cb.isChecked()) {
                    availableDays.add(cb.getText().toString());
                }
            }
            availableTimes = new ArrayList<>(tempTimes);
            Toast.makeText(this, "Availability updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void showHospitalSelectionDialog() {
        if (allHospitals.isEmpty()) {
            Toast.makeText(this, "No hospitals available", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] hospitalNames = new String[allHospitals.size()];
        boolean[] checkedHospitals = new boolean[allHospitals.size()];

        List<String> currentHospitalIds = new ArrayList<>();
        for (Hospital h : selectedHospitals) {
            currentHospitalIds.add(h.getHospitalId());
        }

        for (int i = 0; i < allHospitals.size(); i++) {
            hospitalNames[i] = allHospitals.get(i).getName();
            checkedHospitals[i] = currentHospitalIds.contains(allHospitals.get(i).getHospitalId());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Hospitals");
        builder.setMultiChoiceItems(hospitalNames, checkedHospitals,
                (dialog, which, isChecked) -> {
                    // Handle checkbox state
                });

        builder.setPositiveButton("Save", (dialog, which) -> {
            selectedHospitals.clear();
            for (int i = 0; i < checkedHospitals.length; i++) {
                if (checkedHospitals[i]) {
                    selectedHospitals.add(allHospitals.get(i));
                }
            }
            hospitalAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Hospitals updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void removeHospital(Hospital hospital) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Hospital")
                .setMessage("Remove " + hospital.getName() + " from your profile?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    selectedHospitals.remove(hospital);
                    hospitalAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Hospital removed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveChanges() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();
        String experienceStr = etExperience.getText().toString().trim();
        String fee = etFee.getText().toString().trim();
        String about = etAbout.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || specialization.isEmpty() ||
                experienceStr.isEmpty() || fee.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int experience;
        try {
            experience = Integer.parseInt(experienceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid experience value", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("specialization", specialization);
        updates.put("experienceYears", experience);
        updates.put("fee", fee);
        updates.put("about", about);
        updates.put("availableDates", availableDays);
        updates.put("availableTimes", availableTimes);

        List<String> hospitalIds = new ArrayList<>();
        for (Hospital hospital : selectedHospitals) {
            hospitalIds.add(hospital.getHospitalId());
        }
        updates.put("hospitalIds", hospitalIds);

        String doctorId = sessionManager.getUserId();
        usersRef.child(doctorId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();

                    // Update session
                    sessionManager.saveUserSession(doctorId, name,
                            currentDoctor.getEmail(), phone, "DOCTOR");

                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to update profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}