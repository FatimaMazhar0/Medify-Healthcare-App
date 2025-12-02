package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.ScheduleAdapter;
import com.example.healthcareproject.models.Appointment;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleFragment extends Fragment {

    private EditText searchEditText;
    private TextView filterAll, filterUpcoming, filterCompleted, filterCancel;
    private RecyclerView scheduleRecyclerView;

    private ScheduleAdapter scheduleAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private List<Appointment> filteredList = new ArrayList<>();
    private String currentFilter = "All";
    private String currentUserId;

    private DatabaseReference appointmentsRef;
    private SessionManager sessionManager;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(getActivity());
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        currentUserId = sessionManager.getUserId();

        initViews(view);
        setupFilterTabs();
        setupRecyclerView();
        setupSearch();
        loadSchedules();
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);

        filterAll = view.findViewById(R.id.filterAll);
        filterUpcoming = view.findViewById(R.id.filterUpcoming);
        filterCompleted = view.findViewById(R.id.filterCompleted);
        filterCancel = view.findViewById(R.id.filterCancel);

        scheduleRecyclerView = view.findViewById(R.id.scheduleRecyclerView);
    }

    private void setupFilterTabs() {
        filterAll.setOnClickListener(v -> {
            updateFilter("All");
            updateFilterUI(filterAll);
        });

        filterUpcoming.setOnClickListener(v -> {
            updateFilter("Upcoming");
            updateFilterUI(filterUpcoming);
        });

        filterCompleted.setOnClickListener(v -> {
            updateFilter("Completed");
            updateFilterUI(filterCompleted);
        });

        filterCancel.setOnClickListener(v -> {
            updateFilter("Cancel");
            updateFilterUI(filterCancel);
        });

        updateFilterUI(filterAll);
    }

    private void updateFilter(String filter) {
        currentFilter = filter;
        applyFilterAndSearch();
    }

    private void updateFilterUI(TextView selectedFilter) {
        resetFilterStyle(filterAll);
        resetFilterStyle(filterUpcoming);
        resetFilterStyle(filterCompleted);
        resetFilterStyle(filterCancel);

        selectedFilter.setBackground(ContextCompat.getDrawable(requireContext(),
                R.drawable.filter_selected_bg_schedulepage));
        selectedFilter.setTextColor(ContextCompat.getColor(requireContext(),
                android.R.color.white));
    }

    private void resetFilterStyle(TextView filter) {
        filter.setBackground(null);
        filter.setTextColor(ContextCompat.getColor(requireContext(),
                R.color.filter_unselected_text));
    }

    private void setupRecyclerView() {
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        scheduleAdapter = new ScheduleAdapter(getContext(), appointmentList, currentUserId);
        scheduleRecyclerView.setAdapter(scheduleAdapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadSchedules() {
        if (currentUserId == null) {
            return;
        }

        appointmentsRef.orderByChild("patientId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Appointment appointment = dataSnapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                appointmentList.add(appointment);
                            }
                        }
                        applyFilterAndSearch();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private boolean isDatePassed(String appointmentDate) {
        try {
            Date appointment = dateFormat.parse(appointmentDate);
            Date currentDate = new Date();

            return appointment != null && appointment.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void applyFilterAndSearch() {
        filteredList.clear();
        String searchQuery = searchEditText.getText().toString().toLowerCase();

        for (Appointment appointment : appointmentList) {
            boolean statusMatch = false;
            boolean isPassed = isDatePassed(appointment.getDate());

            switch (currentFilter) {
                case "All":
                    statusMatch = true;
                    break;
                case "Upcoming":
                    statusMatch = !isPassed && !"cancelled".equals(appointment.getStatus());
                    break;
                case "Completed":
                    statusMatch = isPassed || "cancelled".equals(appointment.getStatus());
                    break;
                case "Cancel":
                    statusMatch = "cancelled".equals(appointment.getStatus());
                    break;
            }

            if (statusMatch) {
                if (searchQuery.isEmpty()) {
                    filteredList.add(appointment);
                } else {
                    filteredList.add(appointment);
                }
            }
        }

        scheduleAdapter.updateData(filteredList, searchQuery);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSchedules();
    }
}