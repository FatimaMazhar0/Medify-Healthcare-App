package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.DoctorAppointmentAdapter;
import com.example.healthcareproject.models.Appointment;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentsFragment extends Fragment {

    private RecyclerView recyclerViewAppointments;
    private DoctorAppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private String doctorId;
    private DatabaseReference appointmentsRef;
    private SessionManager sessionManager;

    private TextView tvPendingCount, tvConfirmedCount, tvTotalCount;
    private LinearLayout emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_appointments, container, false);

        sessionManager = new SessionManager(getActivity());
        doctorId = sessionManager.getUserId();
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        initViews(view);
        setupRecyclerView();
        loadAppointments();

        return view;
    }

    private void initViews(View view) {
        recyclerViewAppointments = view.findViewById(R.id.recyclerViewAppointments);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvConfirmedCount = view.findViewById(R.id.tvConfirmedCount);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        emptyState = view.findViewById(R.id.emptyState);
    }

    private void setupRecyclerView() {
        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DoctorAppointmentAdapter(getActivity(), appointmentList);
        recyclerViewAppointments.setAdapter(adapter);
    }



    private void loadAppointments() {
        if (doctorId == null) {
            Toast.makeText(getActivity(), "Doctor not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        appointmentsRef.orderByChild("doctorId").equalTo(doctorId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentList.clear();
                        int pendingCount = 0;
                        int confirmedCount = 0;
                        int totalCount = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Appointment appointment = dataSnapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                totalCount++;

                                if ("pending".equals(appointment.getStatus())) {
                                    pendingCount++;
                                    appointmentList.add(appointment);
                                } else if ("reschedule_requested".equals(appointment.getStatus())) {
                                    pendingCount++;
                                    appointmentList.add(appointment);
                                } else if ("confirmed".equals(appointment.getStatus())) {
                                    confirmedCount++;
                                }
                            }
                        }

                        tvPendingCount.setText(String.valueOf(pendingCount));
                        tvConfirmedCount.setText(String.valueOf(confirmedCount));
                        tvTotalCount.setText(String.valueOf(totalCount));

                        if (appointmentList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recyclerViewAppointments.setVisibility(View.GONE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            recyclerViewAppointments.setVisibility(View.VISIBLE);
                        }

                        adapter.updateData(appointmentList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Failed to load appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}