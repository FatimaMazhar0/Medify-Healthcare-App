package com.example.healthcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.models.BloodDonor;

import java.util.List;

public class BloodDonorAdapter extends RecyclerView.Adapter<BloodDonorAdapter.DonorViewHolder> {

    private List<BloodDonor> donorList;
    private OnRequestClickListener requestClickListener;

    public interface OnRequestClickListener {
        void onRequestClick(BloodDonor donor);
    }

    public BloodDonorAdapter(List<BloodDonor> donorList, OnRequestClickListener listener) {
        this.donorList = donorList;
        this.requestClickListener = listener;
    }

    @NonNull
    @Override
    public DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_donor, parent, false);
        return new DonorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorViewHolder holder, int position) {
        BloodDonor donor = donorList.get(position);

        holder.tvDonorName.setText(donor.getName());
        holder.tvBloodGroup.setText(donor.getBloodGroup());
        holder.tvDonorContact.setText("Phone: " + donor.getPhone());
        holder.tvDonorEmail.setText("Email: " + donor.getEmail());

        holder.btnRequestBlood.setOnClickListener(v -> {
            if (requestClickListener != null) {
                requestClickListener.onRequestClick(donor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public static class DonorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDonorName, tvBloodGroup, tvDonorContact, tvDonorEmail;
        Button btnRequestBlood;

        public DonorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDonorName = itemView.findViewById(R.id.tvDonorName);
            tvBloodGroup = itemView.findViewById(R.id.tvBloodGroup);
            tvDonorContact = itemView.findViewById(R.id.tvDonorContact);
            tvDonorEmail = itemView.findViewById(R.id.tvDonorEmail);
            btnRequestBlood = itemView.findViewById(R.id.btnRequestBlood);
        }
    }
}