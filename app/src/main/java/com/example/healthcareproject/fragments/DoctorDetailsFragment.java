package com.example.healthcareproject.fragments;

import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.DateDoctorDetailAdapter;
import com.example.healthcareproject.adapters.TimeSlotDoctorDetailAdapter;
import com.example.healthcareproject.models.Appointment;
import com.example.healthcareproject.models.Doctor;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorDetailsFragment extends Fragment {

    private ImageButton btnBack;
    private ImageView imgDoctorProfile;
    private TextView tvDoctorName;
    private TextView tvDoctorSpecialty;
    private TextView tvCharactersValue;
    private TextView tvPatientsValue;
    private TextView tvExperienceValue;
    private TextView tvAboutDoctorDescription;
    private RecyclerView recyclerViewDates;
    private RecyclerView recyclerViewTimeSlots;
    private Button btnConfirmSchedule;

    private DateDoctorDetailAdapter dateAdapter;
    private TimeSlotDoctorDetailAdapter timeSlotAdapter;

    private Doctor doctor;
    private String doctorId;
    private String selectedDate;
    private String selectedTimeSlot;

    private DatabaseReference usersRef;
    private DatabaseReference appointmentsRef;
    private SessionManager sessionManager;

    public static DoctorDetailsFragment newInstance(String doctorId) {
        DoctorDetailsFragment fragment = new DoctorDetailsFragment();
        Bundle args = new Bundle();
        args.putString("doctorId", doctorId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doctorId = getArguments().getString("doctorId");
        }

        sessionManager = new SessionManager(getActivity());
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_details, container, false);

        initViews(view);
        setupRecyclerViews();
        setupListeners();

        if (doctorId != null) {
            fetchDoctorData();
        }

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        imgDoctorProfile = view.findViewById(R.id.imgDoctorProfile);
        tvDoctorName = view.findViewById(R.id.tvDoctorName);
        tvDoctorSpecialty = view.findViewById(R.id.tvDoctorSpecialty);
        tvCharactersValue = view.findViewById(R.id.tvCharactersValue);
        tvPatientsValue = view.findViewById(R.id.tvPatientsValue);
        tvExperienceValue = view.findViewById(R.id.tvExperienceValue);
        tvAboutDoctorDescription = view.findViewById(R.id.tvAboutDoctorDescription);
        recyclerViewDates = view.findViewById(R.id.recyclerViewDates);
        recyclerViewTimeSlots = view.findViewById(R.id.recyclerViewTimeSlots);
        btnConfirmSchedule = view.findViewById(R.id.btnConfirmSchedule);
    }

    private void setupRecyclerViews() {
        LinearLayoutManager dateLayoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerViewDates.setLayoutManager(dateLayoutManager);

        GridLayoutManager timeLayoutManager = new GridLayoutManager(getContext(), 4);
        recyclerViewTimeSlots.setLayoutManager(timeLayoutManager);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnConfirmSchedule.setOnClickListener(v -> confirmBooking());
    }

    private void fetchDoctorData() {
        showLoading(true);

        usersRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showLoading(false);

                if (snapshot.exists()) {
                    doctor = snapshot.getValue(Doctor.class);

                    if (doctor != null) {
                        populateDoctorData();
                    } else {
                        showError("Failed to load doctor data");
                    }
                } else {
                    showError("Doctor not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                showError("Error: " + error.getMessage());
            }
        });
    }

    private void populateDoctorData() {
        tvDoctorName.setText(doctor.getName());
        tvDoctorSpecialty.setText(doctor.getSpecialization());
        tvAboutDoctorDescription.setText(doctor.getAbout());

        tvCharactersValue.setText(String.valueOf(doctor.getExperienceYears()));
        tvPatientsValue.setText(String.valueOf(doctor.getTotalPatients()));
        tvExperienceValue.setText(String.format("%.1f", doctor.getRating()));

        setupDatesList();
        setupTimeSlotsList();
    }

    private void setupDatesList() {
        if (doctor.getAvailableDates() != null && !doctor.getAvailableDates().isEmpty()) {
            selectedDate = doctor.getAvailableDates().get(0);

            dateAdapter = new DateDoctorDetailAdapter(doctor.getAvailableDates(), (date, position) -> {
                selectedDate = date;
            });

            recyclerViewDates.setAdapter(dateAdapter);
        }
    }

    private void setupTimeSlotsList() {
        if (doctor.getAvailableTimes() != null && !doctor.getAvailableTimes().isEmpty()) {
            timeSlotAdapter = new TimeSlotDoctorDetailAdapter(doctor.getAvailableTimes(), (timeSlot, position) -> {
                selectedTimeSlot = timeSlot;
            });

            recyclerViewTimeSlots.setAdapter(timeSlotAdapter);
        }
    }

    private void confirmBooking() {
        if (selectedDate == null) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTimeSlot == null) {
            Toast.makeText(getContext(), "Please select a time slot", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID from SessionManager
        String patientId = sessionManager.getUserId();

        if (patientId == null || patientId.isEmpty()) {
            Toast.makeText(getContext(), "User not logged in. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user is a patient
        String userType = sessionManager.getUserType();
        if (!"PATIENT".equals(userType)) {
            Toast.makeText(getContext(), "Only patients can book appointments", Toast.LENGTH_SHORT).show();
            return;
        }

        saveAppointmentToFirebase(patientId);
    }

    private void saveAppointmentToFirebase(String patientId) {
        String appointmentId = appointmentsRef.push().getKey();

        Appointment appointment = new Appointment(
                appointmentId,
                doctorId,
                patientId,
                "default_hospital",
                selectedDate,
                selectedTimeSlot,
                "pending",
                doctor.getFee()
        );

        appointmentsRef.child(appointmentId).setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Appointment request sent to doctor", Toast.LENGTH_LONG).show();
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to book appointment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void showLoading(boolean show) {
        if (show) {
            btnConfirmSchedule.setText("Loading...");
            btnConfirmSchedule.setEnabled(false);
        } else {
            btnConfirmSchedule.setText("Confirm Schedule");
            btnConfirmSchedule.setEnabled(true);
        }
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}