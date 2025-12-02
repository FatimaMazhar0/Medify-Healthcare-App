package com.example.healthcareproject.models;

public class Appointment {
    private String appointmentId;
    private String doctorId;
    private String patientId;
    private String hospitalId;
    private String date;
    private String time;
    private String status; // pending / confirmed / cancelled / reschedule_requested
    private String fee;
    private long createdAt;
    private String rescheduleDate;
    private String rescheduleTime;

    public Appointment() {}

    public Appointment(String appointmentId, String doctorId, String patientId,
                       String hospitalId, String date, String time, String status,
                       String fee) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.hospitalId = hospitalId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.fee = fee;
        this.createdAt = System.currentTimeMillis();
    }


    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFee() { return fee; }
    public void setFee(String fee) { this.fee = fee; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getRescheduleDate() { return rescheduleDate; }
    public void setRescheduleDate(String rescheduleDate) { this.rescheduleDate = rescheduleDate; }

    public String getRescheduleTime() { return rescheduleTime; }
    public void setRescheduleTime(String rescheduleTime) { this.rescheduleTime = rescheduleTime; }
}