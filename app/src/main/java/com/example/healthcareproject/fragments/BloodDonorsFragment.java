package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.BloodDonorAdapter;
import com.example.healthcareproject.models.BloodDonor;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BloodDonorsFragment extends Fragment {

    private SessionManager sessionManager;
    private DatabaseReference dbRef;

    private RecyclerView rvBloodDonors;
    private ProgressBar progressBar;
    private TextView tvNoDonors;
    private Spinner spinnerFilterBloodGroup;

    private List<BloodDonor> bloodDonors = new ArrayList<>();
    private List<BloodDonor> filteredDonors = new ArrayList<>();
    private BloodDonorAdapter donorAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_donors, container, false);

        sessionManager = new SessionManager(getContext());
        dbRef = FirebaseDatabase.getInstance().getReference();

        initViews(view);
        setupRecyclerView();
        setupBloodGroupFilter();
        loadBloodDonors();

        return view;
    }

    private void initViews(View view) {
        rvBloodDonors = view.findViewById(R.id.rvBloodDonors);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoDonors = view.findViewById(R.id.tvNoDonors);
        spinnerFilterBloodGroup = view.findViewById(R.id.spinnerFilterBloodGroup);
    }

    private void setupRecyclerView() {
        donorAdapter = new BloodDonorAdapter(filteredDonors, new BloodDonorAdapter.OnRequestClickListener() {
            @Override
            public void onRequestClick(BloodDonor donor) {
                showBloodRequestDialog(donor);
            }
        });

        rvBloodDonors.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBloodDonors.setAdapter(donorAdapter);
    }

    private void setupBloodGroupFilter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blood_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterBloodGroup.setAdapter(adapter);

        spinnerFilterBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBloodGroup = parent.getItemAtPosition(position).toString();
                filterDonorsByBloodGroup(selectedBloodGroup);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadBloodDonors() {
        progressBar.setVisibility(View.VISIBLE);

        dbRef.child("bloodDonors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bloodDonors.clear();
                filteredDonors.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        BloodDonor donor = ds.getValue(BloodDonor.class);
                        if (donor != null && donor.isAvailable() &&
                                !donor.getPatientId().equals(sessionManager.getUserId())) {
                            bloodDonors.add(donor);
                        }
                    }

                    String currentFilter = spinnerFilterBloodGroup.getSelectedItem().toString();
                    filterDonorsByBloodGroup(currentFilter);
                } else {
                    tvNoDonors.setVisibility(View.VISIBLE);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading donors: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterDonorsByBloodGroup(String bloodGroup) {
        filteredDonors.clear();

        if (bloodGroup.equals("All")) {
            filteredDonors.addAll(bloodDonors);
        } else {
            for (BloodDonor donor : bloodDonors) {
                if (donor.getBloodGroup().equals(bloodGroup)) {
                    filteredDonors.add(donor);
                }
            }
        }

        donorAdapter.notifyDataSetChanged();
        tvNoDonors.setVisibility(filteredDonors.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showBloodRequestDialog(BloodDonor donor) {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(getContext());

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_blood_request, null);
        builder.setView(dialogView);

        android.widget.EditText etHospital = dialogView.findViewById(R.id.etHospital);
        android.widget.EditText etMessage = dialogView.findViewById(R.id.etMessage);
        Spinner spinnerUrgency = dialogView.findViewById(R.id.spinnerUrgency);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.urgency_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUrgency.setAdapter(adapter);

        builder.setTitle("Request Blood from " + donor.getName())
                .setPositiveButton("Send Request", (dialog, which) -> {
                    String hospital = etHospital.getText().toString().trim();
                    String message = etMessage.getText().toString().trim();
                    String urgency = spinnerUrgency.getSelectedItem().toString();

                    if (hospital.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter hospital name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sendBloodRequest(donor, hospital, message, urgency);
                })
                .setNegativeButton("Cancel", null);

        builder.show();
    }

    private void sendBloodRequest(BloodDonor donor, String hospital, String message, String urgency) {
        String requestId = dbRef.child("bloodRequests").push().getKey();
        String patientId = sessionManager.getUserId();
        String patientName = sessionManager.getUserName();

        com.example.healthcareproject.models.BloodRequest request =
                new com.example.healthcareproject.models.BloodRequest(
                        requestId, patientId, patientName, donor.getPatientId(),
                        donor.getName(), donor.getBloodGroup(), hospital, urgency
                );

        request.setMessage(message);
        request.setContactNumber(sessionManager.getUserEmail());

        dbRef.child("bloodRequests").child(requestId).setValue(request)
                .addOnSuccessListener(a -> {
                    Toast.makeText(getContext(), "Blood request sent successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to send request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}