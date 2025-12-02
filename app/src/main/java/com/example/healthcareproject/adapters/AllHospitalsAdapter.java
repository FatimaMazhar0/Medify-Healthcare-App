package com.example.healthcareproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcareproject.R;
import com.example.healthcareproject.fragments.HospitalProfileFragment;
import com.example.healthcareproject.models.Hospital;

import java.util.ArrayList;

public class AllHospitalsAdapter extends RecyclerView.Adapter<AllHospitalsAdapter.ViewHolder> {

    private ArrayList<Hospital> list;
    private Context context;
    private OnHospitalClickListener listener;

    public interface OnHospitalClickListener {
        void onHospitalClick(String hospitalId);

    }

    public AllHospitalsAdapter(ArrayList<Hospital> list, Context context, OnHospitalClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hospital, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hospital hospital = list.get(position);


        holder.tvHospitalName.setText(hospital.getName() != null ?
                hospital.getName() : "Unknown Hospital");

        holder.tvAddress.setText(hospital.getAddress() != null ?
                hospital.getAddress() : "Address not available");

        holder.btnContact.setOnClickListener(v -> {
                    if (hospital.getContactNumber() != null && !hospital.getContactNumber().isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + hospital.getContactNumber()));
                        v.getContext().startActivity(intent);
                    }
                });

        holder.btnViewProfile.setOnClickListener(v -> {
            if (listener != null && hospital.getHospitalId() != null) {
                listener.onHospitalClick(hospital.getHospitalId());
            }
        });

        holder.tvOpeningHours.setText(hospital.getOpeningHours() != null ?
                hospital.getOpeningHours() : "24/7");

        if (hospital.getImageUrl() != null && !hospital.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(hospital.getImageUrl())
                    .placeholder(R.drawable.ic_hospital)
                    .into(holder.ivHospital);
        } else {
            holder.ivHospital.setImageResource(R.drawable.ic_hospital);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && hospital.getHospitalId() != null) {
                listener.onHospitalClick(hospital.getHospitalId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHospital;
        TextView tvHospitalName, tvAddress, tvOpeningHours;

        Button btnContact, btnViewProfile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHospital = itemView.findViewById(R.id.ivHospitalIcon);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            btnContact = itemView.findViewById(R.id.btnCallHelpline);
            btnViewProfile = itemView.findViewById(R.id.btnViewProfile);
            tvOpeningHours = itemView.findViewById(R.id.tvOpeningHours);

        }
    }
}