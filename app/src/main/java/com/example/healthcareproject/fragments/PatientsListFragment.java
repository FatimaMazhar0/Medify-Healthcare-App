package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.PatientsAdapter;
import com.example.healthcareproject.models.Patient;
import com.example.healthcareproject.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PatientsListFragment extends Fragment {

    private RecyclerView recyclerViewPatients;
    private PatientsAdapter adapter;
    private List<Patient> patientList = new ArrayList<>();
    private DatabaseReference usersRef;
    private LinearLayout emptyState;
    private TextView tvTotalCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patients_list, container, false);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        initViews(view);
        setupRecyclerView();
        loadPatients();

        return view;
    }

    private void initViews(View view) {
        recyclerViewPatients = view.findViewById(R.id.recyclerViewPatients);
        emptyState = view.findViewById(R.id.emptyStatePatients);
        tvTotalCount = view.findViewById(R.id.tvTotalPatientsCount);
    }

    private void setupRecyclerView() {
        recyclerViewPatients.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PatientsAdapter(getContext(), patientList);
        recyclerViewPatients.setAdapter(adapter);
    }

    private void loadPatients() {
        usersRef.orderByChild("userType").equalTo("PATIENT")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        patientList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Patient patient = dataSnapshot.getValue(Patient.class);
                            if (patient != null && patient.getUserType() == User.UserType.PATIENT) {
                                patientList.add(patient);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        tvTotalCount.setText("Total Patients: " + patientList.size());

                        if (patientList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recyclerViewPatients.setVisibility(View.GONE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            recyclerViewPatients.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Failed to load patients: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}