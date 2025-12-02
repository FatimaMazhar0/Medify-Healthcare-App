package com.example.healthcareproject.models;

public class BloodDonor {
    private String donorId;
    private String patientId;
    private String name;
    private String bloodGroup;
    private String phone;
    private String email;

    private boolean isAvailable;
    private long lastDonationDate;

    public BloodDonor() {}

    public BloodDonor(String donorId, String patientId, String name, String bloodGroup, String phone, String email) {
        this.donorId = donorId;
        this.patientId = patientId;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.email = email;
        this.isAvailable = true;
        this.lastDonationDate = 0;
    }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }



    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public long getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(long lastDonationDate) { this.lastDonationDate = lastDonationDate; }
}