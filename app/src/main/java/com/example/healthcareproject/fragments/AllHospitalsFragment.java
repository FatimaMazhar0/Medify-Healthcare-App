package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.AllHospitalsAdapter;
import com.example.healthcareproject.models.Hospital;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllHospitalsFragment extends Fragment {

    private ImageButton btnBack;
    private EditText etSearchHospital;
    private RecyclerView rvHospitals;

    private ArrayList<Hospital> allHospitals = new ArrayList<>();
    private ArrayList<Hospital> filteredHospitals = new ArrayList<>();
    private AllHospitalsAdapter adapter;
    private DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_hospitals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbRef = FirebaseDatabase.getInstance().getReference();

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupClickListeners();
        loadHospitals();
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        etSearchHospital = view.findViewById(R.id.etSearchHospital);
        rvHospitals = view.findViewById(R.id.rvHospitals);
    }

    private void setupRecyclerView() {
        adapter = new AllHospitalsAdapter(filteredHospitals, getContext(), hospitalId -> {
            Toast.makeText(getContext(), "Hospital clicked: " + hospitalId, Toast.LENGTH_SHORT).show();
            // TODO: Navigate to HospitalDetailFragment
             HospitalProfileFragment fragment = HospitalProfileFragment.newInstance(hospitalId);
             getParentFragmentManager().beginTransaction()
                 .replace(R.id.HomeLayout, fragment)
                 .addToBackStack(null)
                 .commit();
        });

        rvHospitals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHospitals.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearchHospital.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterHospitals(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });

    }

    private void loadHospitals() {
        dbRef.child("hospitals")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allHospitals.clear();

                        if (!snapshot.exists()) {
                            Toast.makeText(getContext(), "No hospitals found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                Hospital hospital = ds.getValue(Hospital.class);
                                if (hospital != null) {
                                    hospital.setHospitalId(ds.getKey());
                                    allHospitals.add(hospital);
                                }
                            } catch (Exception e) {
                                Log.e("AllHospitalsFragment", "Error parsing hospital: " + e.getMessage());
                            }
                        }

                        filteredHospitals.clear();
                        filteredHospitals.addAll(allHospitals);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Error loading hospitals: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterHospitals(String searchQuery) {
        filteredHospitals.clear();

        if (searchQuery.isEmpty()) {
            filteredHospitals.addAll(allHospitals);
        } else {
            String query = searchQuery.toLowerCase().trim();
            for (Hospital hospital : allHospitals) {
                boolean matchesName = hospital.getName() != null &&
                        hospital.getName().toLowerCase().contains(query);
                boolean matchesAddress = hospital.getAddress() != null &&
                        hospital.getAddress().toLowerCase().contains(query);
                boolean matchesCity = hospital.getCity() != null &&
                        hospital.getCity().toLowerCase().contains(query);

                if (matchesName || matchesAddress || matchesCity) {
                    filteredHospitals.add(hospital);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}