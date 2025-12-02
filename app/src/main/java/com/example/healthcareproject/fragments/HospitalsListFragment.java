package com.example.healthcareproject.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.HospitalsAdapter;
import com.example.healthcareproject.models.Hospital;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HospitalsListFragment extends Fragment {

    private RecyclerView recyclerViewHospitals;
    private HospitalsAdapter adapter;
    private List<Hospital> hospitalList = new ArrayList<>();
    private DatabaseReference hospitalsRef;
    private LinearLayout emptyState;
    private TextView tvTotalCount;
    private FloatingActionButton fabAddHospital;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospitals_list, container, false);

        hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadHospitals();

        return view;
    }

    private void initViews(View view) {
        recyclerViewHospitals = view.findViewById(R.id.recyclerViewHospitals);
        emptyState = view.findViewById(R.id.emptyStateHospitals);
        tvTotalCount = view.findViewById(R.id.tvTotalHospitalsCount);
        fabAddHospital = view.findViewById(R.id.fabAddHospital);
    }

    private void setupRecyclerView() {
        recyclerViewHospitals.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HospitalsAdapter(getContext(), hospitalList);
        recyclerViewHospitals.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabAddHospital.setOnClickListener(v -> showAddHospitalDialog());
    }

    private void loadHospitals() {
        hospitalsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                hospitalList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Hospital hospital = dataSnapshot.getValue(Hospital.class);
                    if (hospital != null) {
                        hospitalList.add(hospital);
                    }
                }
                adapter.notifyDataSetChanged();
                tvTotalCount.setText("Total Hospitals: " + hospitalList.size());

                if (hospitalList.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerViewHospitals.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerViewHospitals.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(),
                        "Failed to load hospitals: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddHospitalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_hospital, null);
        builder.setView(dialogView);

        EditText etHospitalName = dialogView.findViewById(R.id.etHospitalName);
        EditText etHospitalAddress = dialogView.findViewById(R.id.etHospitalAddress);
        EditText etHospitalCity = dialogView.findViewById(R.id.etHospitalCity);
        EditText etHospitalContact = dialogView.findViewById(R.id.etHospitalContact);
        EditText etHospitalLatitude = dialogView.findViewById(R.id.etHospitalLatitude);
        EditText etHospitalLongitude = dialogView.findViewById(R.id.etHospitalLongitude);

        builder.setTitle("Add New Hospital")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etHospitalName.getText().toString().trim();
            String address = etHospitalAddress.getText().toString().trim();
            String city = etHospitalCity.getText().toString().trim();
            String contact = etHospitalContact.getText().toString().trim();
            String latStr = etHospitalLatitude.getText().toString().trim();
            String lngStr = etHospitalLongitude.getText().toString().trim();

            if (name.isEmpty()) {
                etHospitalName.setError("Required");
                return;
            }
            if (address.isEmpty()) {
                etHospitalAddress.setError("Required");
                return;
            }
            if (city.isEmpty()) {
                etHospitalCity.setError("Required");
                return;
            }
            if (contact.isEmpty()) {
                etHospitalContact.setError("Required");
                return;
            }

            double latitude = 0.0;
            double longitude = 0.0;

            if (!latStr.isEmpty()) {
                try {
                    latitude = Double.parseDouble(latStr);
                } catch (NumberFormatException e) {
                    etHospitalLatitude.setError("Invalid latitude");
                    return;
                }
            }

            if (!lngStr.isEmpty()) {
                try {
                    longitude = Double.parseDouble(lngStr);
                } catch (NumberFormatException e) {
                    etHospitalLongitude.setError("Invalid longitude");
                    return;
                }
            }

            addHospitalToFirebase(name, address, city, contact, latitude, longitude);
            dialog.dismiss();
        });
    }

    private void addHospitalToFirebase(String name, String address, String city, String contact, double latitude, double longitude) {
        String hospitalId = hospitalsRef.push().getKey();

        if (hospitalId == null) {
            Toast.makeText(getContext(), "Failed to generate hospital ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Hospital hospital = new Hospital();
        hospital.setHospitalId(hospitalId);
        hospital.setName(name);
        hospital.setAddress(address);
        hospital.setCity(city);
        hospital.setContactNumber(contact);
        hospital.setLatitude(latitude);
        hospital.setLongitude(longitude);
        hospital.setOpeningHours("24/7");
        hospital.setImageUrl("");
        hospital.setDepartmentIds(new ArrayList<>());
        hospital.setDoctorIds(new ArrayList<>());

        hospitalsRef.child(hospitalId).setValue(hospital)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Hospital added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add hospital: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}