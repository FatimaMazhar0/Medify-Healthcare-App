package com.example.healthcareproject.models;

public class Patient extends User {
    private String bloodGroup;
    private boolean isBloodDonor;

    public Patient() {}

    public Patient(String uid, String name, String email, String password, String phone, String dob) {
        super(uid, name, email, password, phone, dob, UserType.PATIENT);
        this.isBloodDonor = false;
    }

    public Patient(String uid, String name, String email, String password, String phone, String dob, String bloodGroup, boolean isBloodDonor) {
        super(uid, name, email, password, phone, dob, UserType.PATIENT);
        this.bloodGroup = bloodGroup;
        this.isBloodDonor = isBloodDonor;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public boolean isBloodDonor() {
        return isBloodDonor;
    }

    public void setBloodDonor(boolean bloodDonor) {
        isBloodDonor = bloodDonor;
    }
}