package com.example.healthcareproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthcareproject.R;
import com.example.healthcareproject.models.Doctor;
import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {

    private Context context;
    private List<Doctor> doctorList;

    public DoctorsAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_admin, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        holder.tvDoctorName.setText("Dr. " + doctor.getName());
        holder.tvDoctorEmail.setText(doctor.getEmail());
        holder.tvDoctorSpecialty.setText(doctor.getSpecialization());
        holder.tvDoctorExperience.setText(doctor.getExperienceYears() + " years");
        holder.tvDoctorPhone.setText("Phone: " + doctor.getPhone());
        holder.tvDoctorFee.setText("Fee: " + doctor.getFee());
        holder.tvDoctorPatients.setText("Patients: " + doctor.getTotalPatients());
        holder.ratingBar.setRating(doctor.getRating());
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDoctorEmail, tvDoctorSpecialty, tvDoctorExperience,
                tvDoctorPhone, tvDoctorFee, tvDoctorPatients;
        RatingBar ratingBar;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorEmail = itemView.findViewById(R.id.tvDoctorEmail);
            tvDoctorSpecialty = itemView.findViewById(R.id.tvDoctorSpecialty);
            tvDoctorExperience = itemView.findViewById(R.id.tvDoctorExperience);
            tvDoctorPhone = itemView.findViewById(R.id.tvDoctorPhone);
            tvDoctorFee = itemView.findViewById(R.id.tvDoctorFee);
            tvDoctorPatients = itemView.findViewById(R.id.tvDoctorPatients);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}