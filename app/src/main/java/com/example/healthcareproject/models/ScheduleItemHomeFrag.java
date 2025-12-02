package com.example.healthcareproject.models;

public class ScheduleItemHomeFrag {
    private String appointmentId;
    private String doctorId;
    private String doctorName;
    private String doctorImage;
    private String hospitalId;
    private String hospitalName;
    private String specialty;
    private String date;
    private String time;
    private String status;

    public ScheduleItemHomeFrag() {
    }

    public ScheduleItemHomeFrag(String appointmentId, String doctorId, String doctorName,
                        String doctorImage, String hospitalId, String hospitalName,
                        String specialty, String date, String time, String status) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorImage = doctorImage;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.specialty = specialty;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDoctorImage() { return doctorImage; }
    public void setDoctorImage(String doctorImage) { this.doctorImage = doctorImage; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}