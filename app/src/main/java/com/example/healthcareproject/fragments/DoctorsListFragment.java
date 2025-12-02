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
import com.example.healthcareproject.adapters.DoctorsAdapter;
import com.example.healthcareproject.models.Doctor;
import com.example.healthcareproject.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DoctorsListFragment extends Fragment {

    private RecyclerView recyclerViewDoctors;
    private DoctorsAdapter adapter;
    private List<Doctor> doctorList = new ArrayList<>();
    private DatabaseReference usersRef;
    private LinearLayout emptyState;
    private TextView tvTotalCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctors_list, container, false);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        initViews(view);
        setupRecyclerView();
        loadDoctors();

        return view;
    }

    private void initViews(View view) {
        recyclerViewDoctors = view.findViewById(R.id.recyclerViewDoctors);
        emptyState = view.findViewById(R.id.emptyStateDoctors);
        tvTotalCount = view.findViewById(R.id.tvTotalDoctorsCount);
    }

    private void setupRecyclerView() {
        recyclerViewDoctors.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DoctorsAdapter(getContext(), doctorList);
        recyclerViewDoctors.setAdapter(adapter);
    }

    private void loadDoctors() {
        usersRef.orderByChild("userType").equalTo("DOCTOR")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        doctorList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Doctor doctor = dataSnapshot.getValue(Doctor.class);
                            if (doctor != null && doctor.getUserType() == User.UserType.DOCTOR) {
                                doctorList.add(doctor);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        tvTotalCount.setText("Total Doctors: " + doctorList.size());

                        if (doctorList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recyclerViewDoctors.setVisibility(View.GONE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            recyclerViewDoctors.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Failed to load doctors: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}