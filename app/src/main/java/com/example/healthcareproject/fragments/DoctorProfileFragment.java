package com.example.healthcareproject.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.activities.EditDoctorProfileActivity;
import com.example.healthcareproject.activities.LoginActivity;
import com.example.healthcareproject.adapters.HospitalListAdapter;
import com.example.healthcareproject.models.Doctor;
import com.example.healthcareproject.models.Hospital;
import com.example.healthcareproject.utils.PreferenceManager;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorProfileFragment extends Fragment {

    private TextView tvDoctorName, tvDoctorSpecialty, tvExperience, tvPatients, tvRating, tvFee;
    private TextView tvAbout, tvEmail, tvPhone, tvAvailableDays, tvAvailableTimes, tvNoHospitals;
    private Button btnLogout, btnEditProfile;
    private RecyclerView rvHospitals;
    private HospitalListAdapter hospitalAdapter;

    private PreferenceManager preferenceManager;
    private SessionManager sessionManager;
    private DatabaseReference usersRef, hospitalsRef;

    private List<Hospital> hospitalList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        preferenceManager = new PreferenceManager(requireContext());
        sessionManager = new SessionManager(getActivity());
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");

        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadDoctorData();

        return view;
    }

    private void initViews(View view) {
        tvDoctorName = view.findViewById(R.id.tvDoctorName);
        tvDoctorSpecialty = view.findViewById(R.id.tvDoctorSpecialty);
        tvExperience = view.findViewById(R.id.tvExperience);
        tvPatients = view.findViewById(R.id.tvPatients);
        tvRating = view.findViewById(R.id.tvRating);
        tvFee = view.findViewById(R.id.tvFee);
        tvAbout = view.findViewById(R.id.tvAbout);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAvailableDays = view.findViewById(R.id.tvAvailableDays);
        tvAvailableTimes = view.findViewById(R.id.tvAvailableTimes);
        tvNoHospitals = view.findViewById(R.id.tvNoHospitals);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        rvHospitals = view.findViewById(R.id.rvHospitals);
    }

    private void setupRecyclerView() {
        hospitalAdapter = new HospitalListAdapter(hospitalList, getContext());
        rvHospitals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHospitals.setAdapter(hospitalAdapter);
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> logout());
        btnEditProfile.setOnClickListener(v -> editProfile());

        tvPhone.setOnClickListener(v -> initiatePhoneCall());
    }

    private void initiatePhoneCall() {
        String phoneNumber = tvPhone.getText().toString().trim();

        if (phoneNumber.isEmpty() || phoneNumber.equals("Not specified")) {
            Toast.makeText(requireContext(), "Phone number not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String cleanNumber = phoneNumber.replaceAll("[^0-9+]", "");

        if (cleanNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + cleanNumber));

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "No phone app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDoctorData() {
        String doctorId = sessionManager.getUserId();

        if (doctorId == null || doctorId.isEmpty()) {
            Toast.makeText(getActivity(), "Doctor not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.child(doctorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Doctor doctor = snapshot.getValue(Doctor.class);
                    if (doctor != null) {
                        populateDoctorData(doctor);
                        loadHospitalsData(doctor.getHospitalIds());
                    }
                } else {
                    Toast.makeText(getActivity(), "Doctor data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load doctor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateDoctorData(Doctor doctor) {
        tvDoctorName.setText(doctor.getName());
        tvDoctorSpecialty.setText(doctor.getSpecialization());
        tvExperience.setText(doctor.getExperienceYears() + " years");
        tvPatients.setText(String.valueOf(doctor.getTotalPatients()));
        tvRating.setText(String.format("%.1f", doctor.getRating()));
        tvFee.setText("$" + doctor.getFee());
        tvAbout.setText(doctor.getAbout());
        tvEmail.setText(doctor.getEmail());
        tvPhone.setText(doctor.getPhone());

        if (doctor.getAvailableDates() != null && !doctor.getAvailableDates().isEmpty()) {
            tvAvailableDays.setText(formatList(doctor.getAvailableDates()));
        } else {
            tvAvailableDays.setText("Not specified");
        }

        // Format and display available times
        if (doctor.getAvailableTimes() != null && !doctor.getAvailableTimes().isEmpty()) {
            tvAvailableTimes.setText(formatList(doctor.getAvailableTimes()));
        } else {
            tvAvailableTimes.setText("Not specified");
        }
    }

    private void loadHospitalsData(List<String> hospitalIds) {
        if (hospitalIds == null || hospitalIds.isEmpty()) {
            tvNoHospitals.setVisibility(View.VISIBLE);
            rvHospitals.setVisibility(View.GONE);
            Log.d("DoctorProfile", "No hospital IDs to load");
            return;
        }

        hospitalList.clear();
        tvNoHospitals.setVisibility(View.GONE);
        rvHospitals.setVisibility(View.VISIBLE);


        hospitalsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hospitalList.clear(); // Clear list before adding new data

                if (!snapshot.exists()) {
                    tvNoHospitals.setVisibility(View.VISIBLE);
                    rvHospitals.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "No hospitals found in database", Toast.LENGTH_SHORT).show();
                    return;
                }

                int loadedCount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        String hospitalId = ds.getKey(); // Get the Firebase key

                        // Check if this hospital is in the doctor's hospitalIds list
                        if (hospitalIds.contains(hospitalId)) {
                            Hospital hospital = ds.getValue(Hospital.class);
                            if (hospital != null) {
                                hospital.setHospitalId(hospitalId); // Set the ID
                                hospitalList.add(hospital);
                                loadedCount++;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("DoctorProfile", "Error parsing hospital: " + e.getMessage());
                    }
                }

                if (hospitalList.isEmpty()) {
                    tvNoHospitals.setVisibility(View.VISIBLE);
                    rvHospitals.setVisibility(View.GONE);
                } else {
                    hospitalAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),
                        "Error loading hospitals: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                tvNoHospitals.setVisibility(View.VISIBLE);
                rvHospitals.setVisibility(View.GONE);
            }
        });
    }

    private String formatList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "Not specified";
        }
        return String.join(", ", list);
    }

    private void logout() {
        preferenceManager.clearAll();
        sessionManager.logout();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void editProfile() {
        Intent intent = new Intent(requireContext(), EditDoctorProfileActivity.class);
        startActivity(intent);
    }
}