package com.example.healthcareproject.models;

import java.util.List;

public class Prescription {

    private String id;
    private String doctorId;
    private String patientId;
    private String appointmentId;

    private List<String> medicineIds;
    private String notes;

    public Prescription() {}

    public Prescription(String id, String doctorId, String patientId, String appointmentId, List<String> medicineIds, String notes) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.medicineIds = medicineIds;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public List<String> getMedicineIds() {
        return medicineIds;
    }

    public void setMedicineIds(List<String> medicineIds) {
        this.medicineIds = medicineIds;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
