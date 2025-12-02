package com.example.healthcareproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.models.Appointment;
import com.example.healthcareproject.models.Doctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.AppointmentViewHolder> {

    private Context context;
    private List<Appointment> appointmentList;
    private String currentUserId;
    private DatabaseReference usersRef;
    private DatabaseReference appointmentsRef;
    private String currentSearchQuery = "";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public ScheduleAdapter(Context context, List<Appointment> appointmentList, String currentUserId) {
        this.context = context;
        this.appointmentList = appointmentList != null ? appointmentList : new ArrayList<>();
        this.currentUserId = currentUserId;
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
        this.appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item_layout_schedulescreen, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        if (appointmentList == null || position >= appointmentList.size()) {
            return;
        }

        Appointment appointment = appointmentList.get(position);
        if (appointment == null) {
            return;
        }

        holder.appointmentDate.setText(appointment.getDate() != null ? appointment.getDate() : "N/A");
        holder.appointmentTime.setText(appointment.getTime() != null ? appointment.getTime() : "N/A");

        String status = appointment.getStatus() != null ? appointment.getStatus() : "N/A";
        holder.tvStatus.setText(status);

        if ("pending".equals(status)) {
            holder.btnCancel.setBackgroundResource(R.drawable.status_pending_bg);
        } else if ("confirmed".equals(status)) {
            holder.btnCancel.setBackgroundResource(R.drawable.status_approved_bg);
        } else if ("cancelled".equals(status)) {
            holder.btnCancel.setBackgroundResource(R.drawable.status_rejected_bg);
        } else if ("reschedule_requested".equals(status)) {
            holder.btnCancel.setBackgroundResource(R.drawable.status_completed_bg);
        }

        holder.btnCancel.setText("Cancel");

        fetchDoctorInfo(appointment.getDoctorId(), holder, appointment);

        boolean isDatePassed = isDatePassed(appointment.getDate());

        if (!isDatePassed && !"cancelled".equals(status)) {
            holder.btnReschedule.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnReschedule.setText("Reschedule");
        } else {
            holder.btnReschedule.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        }

        holder.btnReschedule.setOnClickListener(v -> {
            showRescheduleDialog(appointment);
        });

        holder.btnCancel.setOnClickListener(v -> {
            cancelAppointment(appointment);
        });
    }

    private boolean isDatePassed(String appointmentDate) {
        try {
            Date appointment = dateFormat.parse(appointmentDate);
            Date currentDate = new Date();

            return appointment != null && appointment.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fetchDoctorInfo(String doctorId, AppointmentViewHolder holder, Appointment appointment) {
        if (doctorId == null) {
            holder.doctorName.setText("Doctor");
            holder.doctorSpecialty.setText("General");
            holder.hospitalName.setText("Hospital");
            return;
        }

        usersRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String doctorName = snapshot.child("name").getValue(String.class);
                    String specialization = snapshot.child("specialization").getValue(String.class);

                    if (currentSearchQuery.isEmpty() ||
                            (doctorName != null && doctorName.toLowerCase().contains(currentSearchQuery)) ||
                            (specialization != null && specialization.toLowerCase().contains(currentSearchQuery))) {

                        holder.doctorName.setText(doctorName != null ? doctorName : "Doctor");
                        holder.doctorSpecialty.setText(specialization != null ? specialization : "General");

                        holder.hospitalName.setText("Cengkareng Hospital");

                        holder.itemView.setVisibility(View.VISIBLE);
                    } else {
                        holder.itemView.setVisibility(View.GONE);
                    }
                } else {
                    holder.doctorName.setText("Doctor");
                    holder.doctorSpecialty.setText("General");
                    holder.hospitalName.setText("Hospital");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.doctorName.setText("Doctor");
                holder.doctorSpecialty.setText("General");
                holder.hospitalName.setText("Hospital");
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList != null ? appointmentList.size() : 0;
    }

    public void updateData(List<Appointment> newList, String searchQuery) {
        this.appointmentList = newList != null ? newList : new ArrayList<>();
        this.currentSearchQuery = searchQuery != null ? searchQuery.toLowerCase() : "";
        notifyDataSetChanged();
    }

    private void showRescheduleDialog(Appointment appointment) {
        if (appointment == null || appointment.getDoctorId() == null) {
            Toast.makeText(context, "Invalid appointment", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.child(appointment.getDoctorId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Doctor doctor = snapshot.getValue(Doctor.class);
                    if (doctor != null && doctor.getAvailableDates() != null && doctor.getAvailableTimes() != null) {
                        showRescheduleSelectionDialog(appointment, doctor.getAvailableDates(), doctor.getAvailableTimes());
                    } else {
                        Toast.makeText(context, "Doctor availability not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Doctor not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to fetch doctor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRescheduleSelectionDialog(Appointment appointment, List<String> availableDates, List<String> availableTimes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reschedule Appointment");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_reschedule, null);
        builder.setView(dialogView);

        Spinner spinnerDate = dialogView.findViewById(R.id.spinnerDate);
        Spinner spinnerTime = dialogView.findViewById(R.id.spinnerTime);

        if (availableDates == null) availableDates = new ArrayList<>();
        if (availableTimes == null) availableTimes = new ArrayList<>();

        if (availableDates.isEmpty()) {
            availableDates.add("No available dates");
        }
        if (availableTimes.isEmpty()) {
            availableTimes.add("No available times");
        }

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, availableDates);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(dateAdapter);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, availableTimes);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeAdapter);

        builder.setPositiveButton("Request Reschedule", (dialog, which) -> {
            String newDate = spinnerDate.getSelectedItem().toString();
            String newTime = spinnerTime.getSelectedItem().toString();

            if (!newDate.equals("No available dates") && !newTime.equals("No available times")) {
                requestReschedule(appointment, newDate, newTime);
            } else {
                Toast.makeText(context, "Please select valid date and time", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void requestReschedule(Appointment appointment, String newDate, String newTime) {
        if (appointment == null || appointment.getAppointmentId() == null) {
            Toast.makeText(context, "Invalid appointment", Toast.LENGTH_SHORT).show();
            return;
        }

        appointmentsRef.child(appointment.getAppointmentId()).child("rescheduleDate").setValue(newDate);
        appointmentsRef.child(appointment.getAppointmentId()).child("rescheduleTime").setValue(newTime);
        appointmentsRef.child(appointment.getAppointmentId()).child("status").setValue("reschedule_requested")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Reschedule request sent to doctor", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to send request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void cancelAppointment(Appointment appointment) {
        if (appointment == null || appointment.getAppointmentId() == null) {
            Toast.makeText(context, "Invalid appointment", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    appointmentsRef.child(appointment.getAppointmentId()).child("status").setValue("cancelled")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Appointment cancelled", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to cancel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName, doctorSpecialty, hospitalName;
        ImageView doctorAvatar, hospitalIcon, timeIcon, dateIcon;

        TextView appointmentTime, appointmentDate, tvStatus;

        Button btnReschedule, btnCancel;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            doctorName = itemView.findViewById(R.id.doctorName);
            doctorSpecialty = itemView.findViewById(R.id.doctorSpecialty);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            doctorAvatar = itemView.findViewById(R.id.doctorAvatar);
            hospitalIcon = itemView.findViewById(R.id.hospitalIcon);
            timeIcon = itemView.findViewById(R.id.timeIcon);
            dateIcon = itemView.findViewById(R.id.dateIcon);

            appointmentTime = itemView.findViewById(R.id.appointmentTime);
            appointmentDate = itemView.findViewById(R.id.appointmentDate);

            btnReschedule = itemView.findViewById(R.id.btnReschedule);
            btnCancel = itemView.findViewById(R.id.btnCancel);

            if (doctorName == null || doctorSpecialty == null || appointmentTime == null ||
                    appointmentDate == null || btnReschedule == null || btnCancel == null) {
                throw new IllegalStateException("One or more critical views not found in schedule_item_layout_schedulescreen.xml");
            }
        }
    }
}