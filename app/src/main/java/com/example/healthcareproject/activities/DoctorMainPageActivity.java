package com.example.healthcareproject.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.DoctorPagerAdapter;
import com.example.healthcareproject.fragments.DoctorAppointmentsFragment;
import com.example.healthcareproject.fragments.DoctorProfileFragment;
import com.example.healthcareproject.models.Doctor;
import com.example.healthcareproject.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorMainPageActivity extends AppCompatActivity {

    private ImageView imgDoctorProfile;
    private TextView tvDoctorName, tvDoctorSpecialty;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private SessionManager sessionManager;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main_page);

        sessionManager = new SessionManager(this);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        initViews();

        if (tabLayout == null || viewPager == null) {
            Toast.makeText(this, "Error initializing UI components", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViewPager();
        setupTabLayout();
        loadDoctorHeaderData();
    }

    private void initViews() {
        imgDoctorProfile = findViewById(R.id.imgDoctorProfile);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvDoctorSpecialty = findViewById(R.id.tvDoctorSpecialty);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setupViewPager() {
        DoctorPagerAdapter pagerAdapter = new DoctorPagerAdapter(this);
        pagerAdapter.addFragment(new DoctorProfileFragment(), "Profile");
        pagerAdapter.addFragment(new DoctorAppointmentsFragment(), "Appointments");
        viewPager.setAdapter(pagerAdapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Profile" : "Appointments")
        ).attach();
    }

    private void loadDoctorHeaderData() {
        String doctorId = sessionManager.getUserId();

        if (doctorId == null || doctorId.isEmpty()) {
            Toast.makeText(this, "Doctor not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Doctor doctor = snapshot.getValue(Doctor.class);
                    if (doctor != null) {
                        updateHeaderUI(doctor);
                    }
                } else {
                    Toast.makeText(DoctorMainPageActivity.this, "Doctor data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorMainPageActivity.this, "Failed to load doctor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateHeaderUI(Doctor doctor) {
        tvDoctorName.setText(doctor.getName());
        tvDoctorSpecialty.setText(doctor.getSpecialization());

    }


}