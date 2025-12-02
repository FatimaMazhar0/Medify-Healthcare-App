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
import com.example.healthcareproject.models.Appointment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.AppointmentViewHolder> {

    private Context context;
    private List<Appointment> appointmentList;
    private DatabaseReference usersRef;
    private DatabaseReference appointmentsRef;

    public DoctorAppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
        this.appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        fetchPatientInfo(appointment.getPatientId(), holder);

        holder.tvDate.setText("Date: " + appointment.getDate());
        holder.tvTime.setText("Time: " + appointment.getTime());
        holder.tvStatus.setText("Status: " + appointment.getStatus());

        if ("reschedule_requested".equals(appointment.getStatus())) {
            holder.tvRescheduleInfo.setVisibility(View.VISIBLE);
            holder.tvRescheduleInfo.setText("Reschedule to: " +
                    appointment.getRescheduleDate() + " at " + appointment.getRescheduleTime());
        } else {
            holder.tvRescheduleInfo.setVisibility(View.GONE);
        }

        holder.btnAccept.setOnClickListener(v -> acceptAppointment(appointment));
        holder.btnReject.setOnClickListener(v -> rejectAppointment(appointment));
    }

    private void fetchPatientInfo(String patientId, AppointmentViewHolder holder) {
        usersRef.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String patientName = snapshot.child("name").getValue(String.class);
                    holder.tvPatientName.setText(patientName != null ? patientName : "Patient");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.tvPatientName.setText("Patient");
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public void updateData(List<Appointment> newList) {
        appointmentList = newList;
        notifyDataSetChanged();
    }

    private void acceptAppointment(Appointment appointment) {
        if ("reschedule_requested".equals(appointment.getStatus())) {
            appointmentsRef.child(appointment.getAppointmentId()).child("date").setValue(appointment.getRescheduleDate());
            appointmentsRef.child(appointment.getAppointmentId()).child("time").setValue(appointment.getRescheduleTime());
            appointmentsRef.child(appointment.getAppointmentId()).child("rescheduleDate").setValue(null);
            appointmentsRef.child(appointment.getAppointmentId()).child("rescheduleTime").setValue(null);
        }

        appointmentsRef.child(appointment.getAppointmentId()).child("status").setValue("confirmed")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Appointment confirmed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to confirm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectAppointment(Appointment appointment) {
        appointmentsRef.child(appointment.getAppointmentId()).child("status").setValue("cancelled")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Appointment rejected", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to reject: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvDate, tvTime, tvStatus, tvRescheduleInfo;
        Button btnAccept, btnReject;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRescheduleInfo = itemView.findViewById(R.id.tvRescheduleInfo);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}