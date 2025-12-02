package com.example.healthcareproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.models.Hospital;

import java.util.List;

public class HospitalListAdapter extends RecyclerView.Adapter<HospitalListAdapter.HospitalViewHolder> {

    private List<Hospital> hospitals;
    private Context context;

    public HospitalListAdapter(List<Hospital> hospitals, Context context) {
        this.hospitals = hospitals;
        this.context = context;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hospital_doctor_profile, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);
        holder.tvHospitalName.setText(hospital.getName());
        holder.tvHospitalAddress.setText(hospital.getAddress());
        holder.tvHospitalContact.setText(hospital.getContactNumber());
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView tvHospitalName, tvHospitalAddress, tvHospitalContact;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvHospitalAddress = itemView.findViewById(R.id.tvHospitalAddress);
            tvHospitalContact = itemView.findViewById(R.id.tvHospitalContact);
        }
    }
}