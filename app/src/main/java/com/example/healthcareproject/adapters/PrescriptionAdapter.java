package com.example.healthcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.models.Prescription;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    private List<Prescription> prescriptionList;
    private DatabaseReference dbRef;

    public PrescriptionAdapter(List<Prescription> prescriptionList) {
        this.prescriptionList = prescriptionList;
        this.dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        Prescription prescription = prescriptionList.get(position);

        holder.tvPrescriptionId.setText("Prescription #" + prescription.getId());
        holder.tvNotes.setText(prescription.getNotes() != null ? prescription.getNotes() : "No additional notes");

        loadDoctorName(prescription.getDoctorId(), holder.tvDoctorName);

        loadAppointmentDate(prescription.getAppointmentId(), holder.tvAppointmentDate);

        loadMedicines(prescription.getMedicineIds(), holder.tvMedicines);
    }

    private void loadDoctorName(String doctorId, TextView tvDoctorName) {
        dbRef.child("users").child(doctorId).child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String doctorName = snapshot.getValue(String.class);
                            tvDoctorName.setText("Dr. " + doctorName);
                        } else {
                            tvDoctorName.setText("Unknown Doctor");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvDoctorName.setText("Unknown Doctor");
                    }
                });
    }

    private void loadAppointmentDate(String appointmentId, TextView tvAppointmentDate) {
        dbRef.child("appointments").child(appointmentId).child("date")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String date = snapshot.getValue(String.class);
                            tvAppointmentDate.setText("Date: " + date);
                        } else {
                            tvAppointmentDate.setText("Date: Unknown");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvAppointmentDate.setText("Date: Unknown");
                    }
                });
    }

    private void loadMedicines(List<String> medicineIds, TextView tvMedicines) {
        if (medicineIds == null || medicineIds.isEmpty()) {
            tvMedicines.setText("Medicines: None prescribed");
            return;
        }

        StringBuilder medicinesBuilder = new StringBuilder("Medicines: ");
        final int[] loadedCount = {0};

        for (String medicineId : medicineIds) {
            dbRef.child("medicines").child(medicineId).child("name")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String medicineName = snapshot.getValue(String.class);
                                if (loadedCount[0] > 0) {
                                    medicinesBuilder.append(", ");
                                }
                                medicinesBuilder.append(medicineName);
                            }

                            loadedCount[0]++;
                            if (loadedCount[0] == medicineIds.size()) {
                                tvMedicines.setText(medicinesBuilder.toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadedCount[0]++;
                            if (loadedCount[0] == medicineIds.size()) {
                                tvMedicines.setText(medicinesBuilder.toString());
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public static class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrescriptionId, tvDoctorName, tvAppointmentDate, tvMedicines, tvNotes;

        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrescriptionId = itemView.findViewById(R.id.tvPrescriptionId);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvAppointmentDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvMedicines = itemView.findViewById(R.id.tvMedicines);
            tvNotes = itemView.findViewById(R.id.tvNotes);
        }
    }
}