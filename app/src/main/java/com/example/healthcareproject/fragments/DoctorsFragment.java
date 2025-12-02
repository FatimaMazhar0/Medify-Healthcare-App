package com.example.healthcareproject.fragments;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.DoctorListAdapter;
import com.example.healthcareproject.models.Doctor;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DoctorsFragment extends Fragment {

    private EditText etSearch;
    private ChipGroup chipGroupSpecialties;
    private RecyclerView rvDoctors;
    private TextView tvEmptyState;
    private ProgressBar progressBar;

    private ArrayList<Doctor> allDoctors = new ArrayList<>();
    private ArrayList<Doctor> filteredDoctors = new ArrayList<>();
    private DoctorListAdapter adapter;
    private DatabaseReference dbRef;

    private String selectedSpecialty = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbRef = FirebaseDatabase.getInstance().getReference();

        initViews(view);
        setupRecyclerView();
        setupSearch();
        loadDoctors();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        chipGroupSpecialties = view.findViewById(R.id.chipGroupSpecialties);
        rvDoctors = view.findViewById(R.id.rvDoctors);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        adapter = new DoctorListAdapter(filteredDoctors, getContext());
        rvDoctors.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDoctors.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctors();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadDoctors() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        dbRef.child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allDoctors.clear();
                        Set<String> specialties = new HashSet<>();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                String userType = ds.child("userType").getValue(String.class);

                                if ("DOCTOR".equals(userType)) {
                                    Doctor doctor = ds.getValue(Doctor.class);
                                    if (doctor != null) {
                                        allDoctors.add(doctor);

                                        if (doctor.getSpecialization() != null &&
                                                !doctor.getSpecialization().isEmpty()) {
                                            specialties.add(doctor.getSpecialization());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("DoctorsFragment", "Error: " + e.getMessage());
                            }
                        }

                        progressBar.setVisibility(View.GONE);

                        createSpecialtyChips(specialties);

                        filterDoctors();

                        if (allDoctors.isEmpty()) {
                            tvEmptyState.setVisibility(View.VISIBLE);
                            tvEmptyState.setText("No doctors available");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),
                                "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createSpecialtyChips(Set<String> specialties) {
        chipGroupSpecialties.removeAllViews();

        Chip allChip = new Chip(getContext());
        allChip.setText("All");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setOnClickListener(v -> {
            selectedSpecialty = "All";
            filterDoctors();
        });
        chipGroupSpecialties.addView(allChip);

        for (String specialty : specialties) {
            Chip chip = new Chip(getContext());
            chip.setText(specialty);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                selectedSpecialty = specialty;
                filterDoctors();
            });
            chipGroupSpecialties.addView(chip);
        }
    }

    private void filterDoctors() {
        filteredDoctors.clear();
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();

        for (Doctor doctor : allDoctors) {
            boolean matchesSearch = true;
            boolean matchesSpecialty = true;

            if (!searchQuery.isEmpty()) {
                String name = doctor.getName() != null ? doctor.getName().toLowerCase() : "";
                String spec = doctor.getSpecialization() != null ? doctor.getSpecialization().toLowerCase() : "";
                matchesSearch = name.contains(searchQuery) || spec.contains(searchQuery);
            }

            if (!"All".equals(selectedSpecialty)) {
                matchesSpecialty = selectedSpecialty.equals(doctor.getSpecialization());
            }

            if (matchesSearch && matchesSpecialty) {
                filteredDoctors.add(doctor);
            }
        }

        adapter.notifyDataSetChanged();

        if (filteredDoctors.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            tvEmptyState.setText("No doctors found");
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }
}