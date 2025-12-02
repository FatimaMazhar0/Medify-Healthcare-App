package com.example.healthcareproject.models;

import java.util.List;

public class DoctorRequest {
    private String requestId;
    private String doctorId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String dob;
    private String specialization;
    private int experienceYears;
    private int totalPatients;
    private float rating;
    private String fee;
    private String about;
    private String profileImage;
    private List<String> availableDates;
    private List<String> availableTimes;
    private List<String> hospitalIds;
    private String departmentId;
    private long createdAt;
    private String status; // pending, approved, rejected

    public DoctorRequest() {
        this.createdAt = System.currentTimeMillis();
        this.status = "pending";
    }


    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public int getTotalPatients() { return totalPatients; }
    public void setTotalPatients(int totalPatients) { this.totalPatients = totalPatients; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getFee() { return fee; }
    public void setFee(String fee) { this.fee = fee; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public List<String> getAvailableDates() { return availableDates; }
    public void setAvailableDates(List<String> availableDates) { this.availableDates = availableDates; }

    public List<String> getAvailableTimes() { return availableTimes; }
    public void setAvailableTimes(List<String> availableTimes) { this.availableTimes = availableTimes; }

    public List<String> getHospitalIds() { return hospitalIds; }
    public void setHospitalIds(List<String> hospitalIds) { this.hospitalIds = hospitalIds; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}