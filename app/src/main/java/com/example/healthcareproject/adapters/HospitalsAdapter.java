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

public class HospitalsAdapter extends RecyclerView.Adapter<HospitalsAdapter.HospitalViewHolder> {

    private Context context;
    private List<Hospital> hospitalList;

    public HospitalsAdapter(Context context, List<Hospital> hospitalList) {
        this.context = context;
        this.hospitalList = hospitalList;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hospital_admin, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital hospital = hospitalList.get(position);

        holder.tvHospitalName.setText(hospital.getName());
        holder.tvHospitalAddress.setText(hospital.getAddress());
        holder.tvHospitalCity.setText(hospital.getCity());
        holder.tvHospitalContact.setText("Contact: " + hospital.getContactNumber());
        holder.tvHospitalHours.setText("Hours: " + hospital.getOpeningHours());

        if (hospital.getLatitude() != 0.0 || hospital.getLongitude() != 0.0) {
            holder.tvHospitalLocation.setText(String.format("Location: %.6f, %.6f",
                    hospital.getLatitude(), hospital.getLongitude()));
            holder.tvHospitalLocation.setVisibility(View.VISIBLE);
        } else {
            holder.tvHospitalLocation.setVisibility(View.GONE);
        }

        int doctorCount = hospital.getDoctorIds() != null ? hospital.getDoctorIds().size() : 0;
        int deptCount = hospital.getDepartmentIds() != null ? hospital.getDepartmentIds().size() : 0;
        holder.tvHospitalStats.setText(String.format("Doctors: %d | Departments: %d", doctorCount, deptCount));
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView tvHospitalName, tvHospitalAddress, tvHospitalCity, tvHospitalContact,
                tvHospitalHours, tvHospitalLocation, tvHospitalStats;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvHospitalAddress = itemView.findViewById(R.id.tvHospitalAddress);
            tvHospitalCity = itemView.findViewById(R.id.tvHospitalCity);
            tvHospitalContact = itemView.findViewById(R.id.tvHospitalContact);
            tvHospitalHours = itemView.findViewById(R.id.tvHospitalHours);
            tvHospitalLocation = itemView.findViewById(R.id.tvHospitalLocation);
            tvHospitalStats = itemView.findViewById(R.id.tvHospitalStats);
        }
    }
}