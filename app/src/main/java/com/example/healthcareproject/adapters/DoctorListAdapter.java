package com.example.healthcareproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcareproject.R;
import com.example.healthcareproject.fragments.DoctorDetailsFragment;
import com.example.healthcareproject.models.Doctor;

import java.util.ArrayList;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {

    private ArrayList<Doctor> list;
    private Context context;

    public DoctorListAdapter(ArrayList<Doctor> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctors_list_doctor_frag, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = list.get(position);

        holder.tvName.setText(doctor.getName() != null ? doctor.getName() : "Unknown Doctor");

        holder.tvSpecialty.setText(doctor.getSpecialization() != null ?
                doctor.getSpecialization() : "General");

        if (doctor.getExperienceYears() > 0) {
            holder.tvExperience.setText(doctor.getExperienceYears() + " years experience");
        } else {
            holder.tvExperience.setText("Experience not specified");
        }

        if (doctor.getRating() > 0) {
            holder.tvRating.setText(String.format("%.1f", doctor.getRating()));
        } else {
            holder.tvRating.setText("N/A");
        }

        if (doctor.getFee() != null && !doctor.getFee().isEmpty()) {
            holder.tvFee.setText("Rs. " + doctor.getFee());
        } else {
            holder.tvFee.setText("Fee not specified");
        }

        if (doctor.getProfileImage() != null && !doctor.getProfileImage().isEmpty()) {
            Glide.with(context)
                    .load(doctor.getProfileImage())
                    .placeholder(R.drawable.ic_doctor)
                    .into(holder.ivDoctor);
        } else {
            holder.ivDoctor.setImageResource(R.drawable.ic_doctor);
        }

        holder.itemView.setOnClickListener(v -> {
            String doctorId = doctor.getUid();

            if (doctorId != null) {
                DoctorDetailsFragment fragment = DoctorDetailsFragment.newInstance(doctorId);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.HomeLayout, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDoctor;
        TextView tvName, tvSpecialty, tvExperience, tvRating, tvFee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDoctor = itemView.findViewById(R.id.ivDoctor);
            tvName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvFee = itemView.findViewById(R.id.tvFee);
        }
    }
}