package com.example.healthcareproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.models.Hospital;

import java.util.List;

public class EditHospitalAdapter extends RecyclerView.Adapter<EditHospitalAdapter.HospitalViewHolder> {

    private List<Hospital> hospitals;
    private Context context;
    private OnHospitalRemoveListener removeListener;

    public interface OnHospitalRemoveListener {
        void onRemove(Hospital hospital);
    }

    public EditHospitalAdapter(List<Hospital> hospitals, Context context, OnHospitalRemoveListener removeListener) {
        this.hospitals = hospitals;
        this.context = context;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hospital_editable, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);
        holder.tvHospitalName.setText(hospital.getName());
        holder.tvHospitalAddress.setText(hospital.getAddress());

        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemove(hospital);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView tvHospitalName, tvHospitalAddress;
        ImageButton btnRemove;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvHospitalAddress = itemView.findViewById(R.id.tvHospitalAddress);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}