package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.PrescriptionAdapter;
import com.example.healthcareproject.models.Prescription;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionFragment extends Fragment {

    private SessionManager sessionManager;
    private DatabaseReference dbRef;

    private ImageView ivBackArrow;
    private RecyclerView rvPrescriptions;
    private ProgressBar progressBar;
    private TextView tvNoPrescriptions;

    private List<Prescription> prescriptionList = new ArrayList<>();
    private PrescriptionAdapter prescriptionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prescription, container, false);

        sessionManager = new SessionManager(getContext());
        dbRef = FirebaseDatabase.getInstance().getReference();

        initViews(view);
        setupClickListeners();
        setupRecyclerView();
        loadPrescriptions();

        return view;
    }

    private void initViews(View view) {
        ivBackArrow = view.findViewById(R.id.ivBackArrow);
        rvPrescriptions = view.findViewById(R.id.rvPrescriptions);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoPrescriptions = view.findViewById(R.id.tvNoPrescriptions);
    }

    private void setupClickListeners() {
        ivBackArrow.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.HomeLayout, new HomeFragment())
                        .commit();
            }
        });
    }

    private void setupRecyclerView() {
        prescriptionAdapter = new PrescriptionAdapter(prescriptionList);
        rvPrescriptions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPrescriptions.setAdapter(prescriptionAdapter);
    }

    private void loadPrescriptions() {
        progressBar.setVisibility(View.VISIBLE);
        String patientId = sessionManager.getUserId();

        if (patientId == null) {
            Toast.makeText(getContext(), "Please login again", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        dbRef.child("prescriptions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                prescriptionList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Prescription prescription = ds.getValue(Prescription.class);
                        if (prescription != null && patientId.equals(prescription.getPatientId())) {
                            prescriptionList.add(prescription);
                        }
                    }
                }

                prescriptionAdapter.notifyDataSetChanged();
                tvNoPrescriptions.setVisibility(prescriptionList.isEmpty() ? View.VISIBLE : View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading prescriptions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}