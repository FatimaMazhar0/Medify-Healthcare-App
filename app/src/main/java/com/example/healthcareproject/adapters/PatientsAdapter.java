package com.example.healthcareproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthcareproject.R;
import com.example.healthcareproject.models.Patient;
import java.util.List;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.PatientViewHolder> {

    private Context context;
    private List<Patient> patientList;

    public PatientsAdapter(Context context, List<Patient> patientList) {
        this.context = context;
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_admin, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);

        holder.tvPatientName.setText(patient.getName());
        holder.tvPatientEmail.setText(patient.getEmail());
        holder.tvPatientPhone.setText("Phone: " + patient.getPhone());
        holder.tvPatientDob.setText("DOB: " + patient.getDob());

        String bloodGroup = patient.getBloodGroup();
        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            holder.tvPatientBloodGroup.setText("Blood Group: " + bloodGroup);
            holder.tvPatientBloodGroup.setVisibility(View.VISIBLE);
        } else {
            holder.tvPatientBloodGroup.setVisibility(View.GONE);
        }

        holder.tvPatientDonor.setText(patient.isBloodDonor() ? "✓ Blood Donor" : "Not a Donor");
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvPatientEmail, tvPatientPhone, tvPatientDob,
                tvPatientBloodGroup, tvPatientDonor;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvPatientEmail = itemView.findViewById(R.id.tvPatientEmail);
            tvPatientPhone = itemView.findViewById(R.id.tvPatientPhone);
            tvPatientDob = itemView.findViewById(R.id.tvPatientDob);
            tvPatientBloodGroup = itemView.findViewById(R.id.tvPatientBloodGroup);
            tvPatientDonor = itemView.findViewById(R.id.tvPatientDonor);
        }
    }
}