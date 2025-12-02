package com.example.healthcareproject.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.DoctorHomeFragmentAdapter;
import com.example.healthcareproject.adapters.ScheduleHomeFragmentAdapter;
import com.example.healthcareproject.models.Appointment;
import com.example.healthcareproject.models.Doctor;
import com.example.healthcareproject.models.Hospital;
import com.example.healthcareproject.models.ScheduleItemHomeFrag;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ImageView ivProfile;
    private TextView tvUserName, tvWelcome, tvSeeMore, tvEmptySchedule, tvEmptyDoctors;
    private EditText etSearchDoctor;
    private LinearLayout layoutEmergency, layoutHospital, layoutBlood, layoutPrescription;
    private LinearLayout layoutDoctor, layoutCheckUp;
    private RecyclerView rvScheduleList, rvDoctorRecommendations;

    private ArrayList<ScheduleItemHomeFrag> scheduleList = new ArrayList<>();
    private ScheduleHomeFragmentAdapter scheduleAdapter;

    private ArrayList<Doctor> doctorList = new ArrayList<>();
    private ArrayList<Doctor> filteredDoctorList = new ArrayList<>();
    private DoctorHomeFragmentAdapter doctorAdapter;

    DatabaseReference dbRef;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            dbRef = FirebaseDatabase.getInstance().getReference();
            sessionManager = new SessionManager(getContext());

            String userId = sessionManager.getUserId();
            Log.d("HomeFragment", "Logged in as: " + userId);

            if (userId == null) {
                Toast.makeText(getContext(), "Please login again", Toast.LENGTH_LONG).show();
                return;
            }

            initViews(view);
            setupClickListeners();
            setupRecyclerViews();
            setupSearchFunctionality();
            loadData();
        } catch (Exception e) {
            Log.e("HomeFragment", "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading home: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        tvUserName.setText(sessionManager.getUserName().toString().trim());
    }

    private void initViews(View view) {
        ivProfile = view.findViewById(R.id.ivProfile);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        etSearchDoctor = view.findViewById(R.id.etSearchDoctor);

        layoutEmergency = view.findViewById(R.id.layoutEmergency);
        layoutHospital = view.findViewById(R.id.layoutHospital);
        layoutBlood = view.findViewById(R.id.layoutBlood);
        layoutPrescription = view.findViewById(R.id.layoutPrescription);
        layoutDoctor = view.findViewById(R.id.layoutDoctor);
        layoutCheckUp = view.findViewById(R.id.layoutCheckUp);


        rvScheduleList = view.findViewById(R.id.rvScheduleList);
        tvEmptySchedule = view.findViewById(R.id.tvEmptySchedule);
        rvDoctorRecommendations = view.findViewById(R.id.rvDoctorRecommendations);
        tvEmptyDoctors = view.findViewById(R.id.tvEmptyDoctors);
        tvSeeMore = view.findViewById(R.id.tvSeeMore);
    }

    private void setupClickListeners() {
        ivProfile.setOnClickListener(v ->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.HomeLayout, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();
        });



        layoutEmergency.setOnClickListener(v -> showEmergencyDialog());


        layoutHospital.setOnClickListener(v ->{
                Toast.makeText(getContext(), "All Hospitals", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.HomeLayout, new AllHospitalsFragment())
                        .addToBackStack(null)
                        .commit();
                });


        layoutBlood.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.HomeLayout, new BloodFragment())
                    .addToBackStack("blood_fragment")
                    .commit();
        });

        layoutPrescription.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.HomeLayout, new PrescriptionFragment())
                    .addToBackStack("prescription_fragment")
                    .commit();
        });

        layoutDoctor.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Find Doctors", Toast.LENGTH_SHORT).show();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.HomeLayout, new DoctorsFragment())
                    .addToBackStack(null)
                    .commit();
        });



        layoutCheckUp.setOnClickListener(v ->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.HomeLayout, new ScheduleFragment())
                    .addToBackStack(null)
                    .commit();
        });


        tvSeeMore.setOnClickListener(v ->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.HomeLayout, new DoctorsFragment())
                    .addToBackStack(null)
                    .commit();
        });

    }

    private void setupRecyclerViews() {
        rvScheduleList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvScheduleList.setNestedScrollingEnabled(false);
        scheduleAdapter = new ScheduleHomeFragmentAdapter(scheduleList);
        rvScheduleList.setAdapter(scheduleAdapter);

        LinearLayoutManager horizontalLayout = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDoctorRecommendations.setLayoutManager(horizontalLayout);

        int spacing = (int) (8 * getResources().getDisplayMetrics().density);
        rvDoctorRecommendations.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = spacing;
            }
        });

        doctorAdapter = new DoctorHomeFragmentAdapter(filteredDoctorList);
        rvDoctorRecommendations.setAdapter(doctorAdapter);
    }

    private void setupSearchFunctionality() {
        etSearchDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void showEmergencyDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(getContext());

        builder.setTitle("Emergency Options")
                .setItems(new CharSequence[]{
                        "Call 1122",
                        "Open Nearest Hospital",
                        "Show All Hospitals on Map"
                }, (dialog, which) -> {

                    switch (which) {
                        case 0:
                            call1122();
                            break;
                        case 1:
                            openNearestHospital();
                            break;
                        case 2:
                            showAllHospitalsOnMap();
                            break;
                    }

                });

        builder.show();
    }

    private void call1122() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:1122"));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to make call", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAllHospitalsOnMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=hospital"));
        startActivity(intent);
    }

    private void openNearestHospital() {
        dbRef.child("hospitals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "No hospitals found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Hospital h = ds.getValue(Hospital.class);
                    if (h == null) continue;

                    double lat = h.getLatitude();
                    double lng = h.getLongitude();
                    String name = h.getName();

                    String uri = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + name + ")";
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(mapIntent);

                    return; // stop after first
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }




    private void filterDoctors(String searchText) {
        filteredDoctorList.clear();

        if (searchText.isEmpty()) {
            filteredDoctorList.addAll(doctorList);
        } else {
            String query = searchText.toLowerCase().trim();
            for (Doctor doctor : doctorList) {
                boolean matchesName = doctor.getName() != null &&
                        doctor.getName().toLowerCase().contains(query);
                boolean matchesSpec = doctor.getSpecialization() != null &&
                        doctor.getSpecialization().toLowerCase().contains(query);

                if (matchesName || matchesSpec) {
                    filteredDoctorList.add(doctor);
                }
            }
        }

        doctorAdapter.notifyDataSetChanged();
        tvEmptyDoctors.setVisibility(
                filteredDoctorList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadData() {
        loadUserName();
        loadSchedules();
        loadDoctors();
    }

    private void loadUserName() {
        String userName = sessionManager.getUserName();
        if (userName != null) {
            tvUserName.setText(userName);
        }
    }

    private void loadSchedules() {
        String userId = sessionManager.getUserId();
        if (userId == null) return;

        scheduleList.clear();
        tvEmptySchedule.setVisibility(View.GONE);

        dbRef.child("appointments")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            tvEmptySchedule.setVisibility(View.VISIBLE);
                            return;
                        }

                        ArrayList<Appointment> userAppointments = new ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Appointment appointment = ds.getValue(Appointment.class);
                            if (appointment != null &&
                                    userId.equals(appointment.getPatientId()) &&
                                    ("pending".equals(appointment.getStatus()) || "confirmed".equals(appointment.getStatus()))) {

                                userAppointments.add(appointment);
                            }
                        }

                        if (userAppointments.isEmpty()) {
                            tvEmptySchedule.setVisibility(View.VISIBLE);
                        } else {
                            for (Appointment appointment : userAppointments) {
                                loadAppointmentDetails(appointment);
                            }
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

    private void loadAppointmentDetails(Appointment appointment) {
        for (ScheduleItemHomeFrag item : scheduleList) {
            if (item.getAppointmentId().equals(appointment.getAppointmentId())) return;
        }

        if (appointment.getDoctorId() == null || appointment.getHospitalId() == null) return;

        dbRef.child("users").child(appointment.getDoctorId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot doctorSnapshot) {
                        if (!doctorSnapshot.exists()) return;

                        Doctor doctor = doctorSnapshot.getValue(Doctor.class);
                        if (doctor == null) return;

                        dbRef.child("hospitals").child(appointment.getHospitalId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot hospitalSnapshot) {
                                        String hospitalName = "Unknown Hospital";
                                        if (hospitalSnapshot.exists()) {
                                            Hospital hospital = hospitalSnapshot.getValue(Hospital.class);
                                            if (hospital != null && hospital.getName() != null) {
                                                hospitalName = hospital.getName();
                                            }
                                        }

                                        ScheduleItemHomeFrag item = new ScheduleItemHomeFrag(
                                                appointment.getAppointmentId(),
                                                appointment.getDoctorId(),
                                                doctor.getName() != null ? doctor.getName() : "Unknown Doctor",
                                                doctor.getProfileImage(),
                                                appointment.getHospitalId(),
                                                hospitalName,
                                                doctor.getSpecialization() != null ? doctor.getSpecialization() : "General",
                                                appointment.getDate(),
                                                appointment.getTime(),
                                                appointment.getStatus()
                                        );

                                        scheduleList.add(item);
                                        scheduleAdapter.notifyDataSetChanged();

                                        tvEmptySchedule.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadDoctors() {
        dbRef.child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        doctorList.clear();

                        if (!snapshot.exists()) {
                            tvEmptyDoctors.setVisibility(View.VISIBLE);
                            return;
                        }

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                String userType = ds.child("userType").getValue(String.class);

                                if (userType != null && userType.equals("DOCTOR")) {
                                    Doctor doctor = ds.getValue(Doctor.class);
                                    if (doctor != null) {
                                        doctorList.add(doctor);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        filteredDoctorList.clear();
                        filteredDoctorList.addAll(doctorList);
                        doctorAdapter.notifyDataSetChanged();

                        tvEmptyDoctors.setVisibility(
                                filteredDoctorList.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}