package com.example.healthcareproject.models;

import java.util.Date;

public class BloodRequest {
    private String requestId;
    private String patientId;
    private String patientName;
    private String donorId;
    private String donorName;
    private String bloodGroup;
    private String hospitalName;
    private String urgency;
    private String status; // pending, approved, rejected, completed
    private String message;
    private long timestamp;
    private String contactNumber;

    public BloodRequest() {}

    public BloodRequest(String requestId, String patientId, String patientName, String donorId,
                        String donorName, String bloodGroup, String hospitalName, String urgency) {
        this.requestId = requestId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.donorId = donorId;
        this.donorName = donorName;
        this.bloodGroup = bloodGroup;
        this.hospitalName = hospitalName;
        this.urgency = urgency;
        this.status = "pending";
        this.timestamp = new Date().getTime();
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}