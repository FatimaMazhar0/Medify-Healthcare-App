package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.BloodRequestAdapter;
import com.example.healthcareproject.models.BloodRequest;
import com.example.healthcareproject.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BloodRequestsFragment extends Fragment {

    private SessionManager sessionManager;
    private DatabaseReference dbRef;

    private RecyclerView rvBloodRequests;
    private ProgressBar progressBar;
    private TextView tvNoRequests;

    private List<BloodRequest> bloodRequests = new ArrayList<>();
    private BloodRequestAdapter requestAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_requests, container, false);

        sessionManager = new SessionManager(getContext());
        dbRef = FirebaseDatabase.getInstance().getReference();

        initViews(view);
        setupRecyclerView();
        loadBloodRequests();

        return view;
    }

    private void initViews(View view) {
        rvBloodRequests = view.findViewById(R.id.rvBloodRequests);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoRequests = view.findViewById(R.id.tvNoRequests);
    }

    private void setupRecyclerView() {
        requestAdapter = new BloodRequestAdapter(bloodRequests, new BloodRequestAdapter.OnRequestActionListener() {
            @Override
            public void onApprove(BloodRequest request) {
                updateRequestStatus(request, "approved");
            }

            @Override
            public void onReject(BloodRequest request) {
                updateRequestStatus(request, "rejected");
            }
        });

        rvBloodRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBloodRequests.setAdapter(requestAdapter);
    }

    private void loadBloodRequests() {
        progressBar.setVisibility(View.VISIBLE);
        String currentUserId = sessionManager.getUserId();

        dbRef.child("bloodRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bloodRequests.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        BloodRequest request = ds.getValue(BloodRequest.class);
                        if (request != null) {
                            if (request.getDonorId().equals(currentUserId) ||
                                    request.getPatientId().equals(currentUserId)) {
                                bloodRequests.add(request);
                            }
                        }
                    }
                }

                requestAdapter.notifyDataSetChanged();
                tvNoRequests.setVisibility(bloodRequests.isEmpty() ? View.VISIBLE : View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading requests: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRequestStatus(BloodRequest request, String status) {
        dbRef.child("bloodRequests").child(request.getRequestId()).child("status").setValue(status)
                .addOnSuccessListener(a -> {
                    Toast.makeText(getContext(), "Request " + status, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update request", Toast.LENGTH_SHORT).show();
                });
    }
}