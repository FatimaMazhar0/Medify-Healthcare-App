package com.example.healthcareproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.models.Doctor;
import com.example.healthcareproject.models.DoctorRequest;
import com.example.healthcareproject.models.User.UserType;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class AdminRequestAdapter extends RecyclerView.Adapter<AdminRequestAdapter.RequestViewHolder> {

    private Context context;
    private List<DoctorRequest> requestList;
    private DatabaseReference requestsRef;
    private DatabaseReference usersRef;

    public AdminRequestAdapter(Context context, List<DoctorRequest> requestList,
                               DatabaseReference requestsRef, DatabaseReference usersRef) {
        this.context = context;
        this.requestList = requestList;
        this.requestsRef = requestsRef;
        this.usersRef = usersRef;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        DoctorRequest request = requestList.get(position);

        holder.tvDoctorName.setText(request.getName());
        holder.tvDoctorEmail.setText(request.getEmail());
        holder.tvDoctorSpecialty.setText(request.getSpecialization());
        holder.tvDoctorExperience.setText(request.getExperienceYears() + " years experience");
        holder.tvDoctorFee.setText("Fee: " + request.getFee());
        holder.tvDoctorAbout.setText(request.getAbout());
        holder.tvDoctorPhone.setText("Phone: " + request.getPhone());

        holder.btnApprove.setOnClickListener(v -> approveDoctorRequest(request));
        holder.btnReject.setOnClickListener(v -> rejectDoctorRequest(request));
    }

    private void approveDoctorRequest(DoctorRequest request) {
        Doctor doctor = new Doctor();
        doctor.setUid(request.getDoctorId());
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPassword(request.getPassword());
        doctor.setPhone(request.getPhone());
        doctor.setDob(request.getDob());
        doctor.setUserType(UserType.DOCTOR);
        doctor.setSpecialization(request.getSpecialization());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setTotalPatients(request.getTotalPatients());
        doctor.setRating(request.getRating());
        doctor.setFee(request.getFee());
        doctor.setAbout(request.getAbout());
        doctor.setProfileImage(request.getProfileImage());
        doctor.setAvailableDates(request.getAvailableDates() != null ? request.getAvailableDates() : new ArrayList<>());
        doctor.setAvailableTimes(request.getAvailableTimes() != null ? request.getAvailableTimes() : new ArrayList<>());
        doctor.setHospitalIds(request.getHospitalIds() != null ? request.getHospitalIds() : new ArrayList<>());
        doctor.setPatientIds(new ArrayList<>());
        doctor.setDepartmentId(request.getDepartmentId() != null ? request.getDepartmentId() : "");

        usersRef.child(request.getDoctorId()).setValue(doctor)
                .addOnSuccessListener(aVoid -> {
                    requestsRef.child(request.getRequestId()).child("status").setValue("approved")
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(context, "Doctor approved and account created successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Doctor created but failed to update request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to create doctor account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectDoctorRequest(DoctorRequest request) {
        requestsRef.child(request.getRequestId()).child("status").setValue("rejected")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Doctor request rejected", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to reject request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void updateData(List<DoctorRequest> newList) {
        requestList = newList;
        notifyDataSetChanged();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDoctorEmail, tvDoctorSpecialty, tvDoctorExperience,
                tvDoctorFee, tvDoctorAbout, tvDoctorPhone;
        Button btnApprove, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorEmail = itemView.findViewById(R.id.tvDoctorEmail);
            tvDoctorSpecialty = itemView.findViewById(R.id.tvDoctorSpecialty);
            tvDoctorExperience = itemView.findViewById(R.id.tvDoctorExperience);
            tvDoctorFee = itemView.findViewById(R.id.tvDoctorFee);
            tvDoctorAbout = itemView.findViewById(R.id.tvDoctorAbout);
            tvDoctorPhone = itemView.findViewById(R.id.tvDoctorPhone);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}