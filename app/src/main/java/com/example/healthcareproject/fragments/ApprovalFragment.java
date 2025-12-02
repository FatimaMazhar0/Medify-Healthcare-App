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
import com.example.healthcareproject.adapters.AdminRequestAdapter;
import com.example.healthcareproject.models.DoctorRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ApprovalFragment extends Fragment {

    private RecyclerView recyclerViewRequests;
    private AdminRequestAdapter adapter;
    private List<DoctorRequest> requestList = new ArrayList<>();
    private DatabaseReference requestsRef;
    private DatabaseReference usersRef;
    private TextView tvPendingCount, tvApprovedCount, tvRejectedCount;
    private LinearLayout emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_approval, container, false);

        requestsRef = FirebaseDatabase.getInstance().getReference("doctor_requests");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        initViews(view);
        setupRecyclerView();
        loadDoctorRequests();
        loadStats();

        return view;
    }

    private void initViews(View view) {
        recyclerViewRequests = view.findViewById(R.id.recyclerViewRequests);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvApprovedCount = view.findViewById(R.id.tvApprovedCount);
        tvRejectedCount = view.findViewById(R.id.tvRejectedCount);
        emptyState = view.findViewById(R.id.emptyState);
    }

    private void setupRecyclerView() {
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminRequestAdapter(getContext(), requestList, requestsRef, usersRef);
        recyclerViewRequests.setAdapter(adapter);
    }

    private void loadDoctorRequests() {
        requestsRef.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        requestList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            DoctorRequest request = dataSnapshot.getValue(DoctorRequest.class);
                            if (request != null && "pending".equals(request.getStatus())) {
                                requestList.add(request);
                            }
                        }
                        adapter.updateData(requestList);

                        if (requestList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recyclerViewRequests.setVisibility(View.GONE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            recyclerViewRequests.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Failed to load requests: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadStats() {
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int pendingCount = 0;
                int approvedCount = 0;
                int rejectedCount = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DoctorRequest request = dataSnapshot.getValue(DoctorRequest.class);
                    if (request != null) {
                        switch (request.getStatus()) {
                            case "pending":
                                pendingCount++;
                                break;
                            case "approved":
                                approvedCount++;
                                break;
                            case "rejected":
                                rejectedCount++;
                                break;
                        }
                    }
                }

                tvPendingCount.setText(String.valueOf(pendingCount));
                tvApprovedCount.setText(String.valueOf(approvedCount));
                tvRejectedCount.setText(String.valueOf(rejectedCount));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(),
                        "Failed to load stats: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}