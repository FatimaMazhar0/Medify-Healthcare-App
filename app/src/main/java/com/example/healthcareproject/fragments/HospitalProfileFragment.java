package com.example.healthcareproject.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.DoctorListAdapter;
import com.example.healthcareproject.models.Doctor;
import com.example.healthcareproject.models.Hospital;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HospitalProfileFragment extends Fragment {

    private ImageButton btnBack;

    private ImageView ivHospitalImage;
    private TextView tvHospitalName, tvRating, tvDoctorCount, tvOpeningHours;
    private TabLayout tabLayout;
    private View aboutSection, doctorsSection;
    private TextView tvAboutContent;
    private Button btnCallHelpline, btnGetDirections;
    private RecyclerView rvHospitalDoctors;

    private String hospitalId;
    private Hospital hospital;
    private ArrayList<Doctor> hospitalDoctors = new ArrayList<>();
    private DoctorListAdapter doctorAdapter;
    private DatabaseReference dbRef;

    public static HospitalProfileFragment newInstance(String hospitalId) {
        HospitalProfileFragment fragment = new HospitalProfileFragment();
        Bundle args = new Bundle();
        args.putString("hospitalId", hospitalId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hospital_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbRef = FirebaseDatabase.getInstance().getReference();

        // Get hospitalId from arguments
        if (getArguments() != null) {
            hospitalId = getArguments().getString("hospitalId");
        }

        initViews(view);
        setupTabs();
        setupRecyclerView();
        setupClickListeners();

        if (hospitalId != null) {
            loadHospitalData();
            loadHospitalDoctors();
        } else {
            Toast.makeText(getContext(), "Hospital ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        ivHospitalImage = view.findViewById(R.id.ivHospitalImage);
        tvHospitalName = view.findViewById(R.id.tvHospitalName);
        tvRating = view.findViewById(R.id.tvRating);
        tvDoctorCount = view.findViewById(R.id.tvDoctorCount);
        tvOpeningHours = view.findViewById(R.id.tvOpeningHours);
        tabLayout = view.findViewById(R.id.tabLayout);
        aboutSection = view.findViewById(R.id.aboutSection);
        doctorsSection = view.findViewById(R.id.doctorsSection);
        tvAboutContent = view.findViewById(R.id.tvAboutContent);
        btnCallHelpline = view.findViewById(R.id.btnCallHelpline);
        btnGetDirections = view.findViewById(R.id.btnGetDirections);
        rvHospitalDoctors = view.findViewById(R.id.rvHospitalDoctors);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("About"));
        tabLayout.addTab(tabLayout.newTab().setText("Hospital Doctor's"));

        showAboutSection();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showAboutSection();
                } else {
                    showDoctorsSection();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupRecyclerView() {
        // Initialize the adapter before setting it
        doctorAdapter = new DoctorListAdapter(hospitalDoctors, getContext());
        rvHospitalDoctors.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHospitalDoctors.setAdapter(doctorAdapter);
        rvHospitalDoctors.setNestedScrollingEnabled(false);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });


        btnCallHelpline.setOnClickListener(v -> {
            if (hospital != null && hospital.getContactNumber() != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + hospital.getContactNumber()));
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Contact number not available", Toast.LENGTH_SHORT).show();
            }
        });

        btnGetDirections.setOnClickListener(v -> {
            if (hospital != null && hospital.getLatitude() != 0 && hospital.getLongitude() != 0) {
                String url = "https://www.google.com/maps/dir/?api=1&destination=" +
                        hospital.getLatitude() + "," + hospital.getLongitude() +
                        "&destination_name=" + hospital.getName();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // Create a chooser to let user select which app to use
                Intent chooser = Intent.createChooser(intent, "Open with");

                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    Toast.makeText(getContext(), "No map application found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Location not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAboutSection() {
        aboutSection.setVisibility(View.VISIBLE);
        doctorsSection.setVisibility(View.GONE);
    }

    private void showDoctorsSection() {
        aboutSection.setVisibility(View.GONE);
        doctorsSection.setVisibility(View.VISIBLE);
    }

    private void loadHospitalData() {
        dbRef.child("hospitals").child(hospitalId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(getContext(), "Hospital not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        hospital = snapshot.getValue(Hospital.class);
                        if (hospital != null) {
                            hospital.setHospitalId(snapshot.getKey());
                            displayHospitalInfo();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayHospitalInfo() {
        tvHospitalName.setText(hospital.getName() != null ? hospital.getName() : "Unknown Hospital");


        tvRating.setText("4.5 15/5"); // Placeholder

        if (hospital.getDoctorIds() != null) {
            tvDoctorCount.setText(hospital.getDoctorIds().size() + " Doctors");
        } else {
            tvDoctorCount.setText("0 Doctors");
        }

        tvOpeningHours.setText(hospital.getOpeningHours() != null ?
                hospital.getOpeningHours() : "24 Hours");

        String aboutText = hospital.getName() + " is located at " +
                (hospital.getAddress() != null ? hospital.getAddress() : "Address not available") +
                ". Contact: " +
                (hospital.getContactNumber() != null ? hospital.getContactNumber() : "Not available");
        tvAboutContent.setText(aboutText);

        // Load hospital image
        if (hospital.getImageUrl() != null && !hospital.getImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(hospital.getImageUrl())
                    .placeholder(R.drawable.ic_hospital)
                    .into(ivHospitalImage);
        }
    }

    private void loadHospitalDoctors() {
        dbRef.child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        hospitalDoctors.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                String userType = ds.child("userType").getValue(String.class);

                                if ("DOCTOR".equals(userType)) {
                                    Doctor doctor = ds.getValue(Doctor.class);
                                    if (doctor != null) {
                                        doctor.setUid(ds.getKey());

                                        // Check if doctor works at this hospital
                                        if (doctor.getHospitalIds() != null &&
                                                doctor.getHospitalIds().contains(hospitalId)) {
                                            hospitalDoctors.add(doctor);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("HospitalProfile", "Error: " + e.getMessage());
                            }
                        }

                        doctorAdapter.notifyDataSetChanged();

                        // Update doctor count
                        tvDoctorCount.setText(hospitalDoctors.size() + " Doctors");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Error loading doctors: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}