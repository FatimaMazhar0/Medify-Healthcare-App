package com.example.healthcareproject.adapters;

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

public class DoctorHomeFragmentAdapter extends RecyclerView.Adapter<DoctorHomeFragmentAdapter.ViewHolder> {

    ArrayList<Doctor> list;

    public DoctorHomeFragmentAdapter(ArrayList<Doctor> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_item_layout_homescreen, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor d = list.get(position);

        if (d.getName() != null) {
            holder.tvName.setText(d.getName());
        } else {
            holder.tvName.setText("Unknown Doctor");
        }

        if (d.getSpecialization() != null) {
            holder.tvSpec.setText(d.getSpecialization());
        } else {
            holder.tvSpec.setText("General");
        }

        if (d.getProfileImage() != null && !d.getProfileImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(d.getProfileImage())
                    .placeholder(R.drawable.ic_doctor)
                    .into(holder.ivDoctor);
        } else {
            holder.ivDoctor.setImageResource(R.drawable.ic_doctor);
        }

        holder.itemView.setOnClickListener(v -> {
            String doctorId = d.getUid();

            if (doctorId != null) {
                DoctorDetailsFragment fragment = DoctorDetailsFragment.newInstance(doctorId);

                FragmentManager fragmentManager = ((AppCompatActivity) holder.itemView.getContext())
                        .getSupportFragmentManager();
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
        TextView tvName, tvSpec;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDoctor = itemView.findViewById(R.id.ivDoctorImage);
            tvName = itemView.findViewById(R.id.tvDoctorName);
            tvSpec = itemView.findViewById(R.id.tvDoctorSpecialty);
        }
    }
}