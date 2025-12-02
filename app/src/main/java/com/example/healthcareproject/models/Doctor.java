package com.example.healthcareproject.models;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User {

    private List<String> hospitalIds;
    private String departmentId;
    private List<String> patientIds;

    private String specialization;
    private int experienceYears;
    private int totalPatients;
    private float rating;

    private String fee;
    private String about;
    private String profileImage;

    private List<String> availableDates;
    private List<String> availableTimes;


    public Doctor() {
        super();
        setUserType(UserType.DOCTOR);


        this.hospitalIds = new ArrayList<>();
        this.patientIds = new ArrayList<>();
        this.availableDates = new ArrayList<>();
        this.availableTimes = new ArrayList<>();
    }

    public Doctor(String uid, String name, String email, String password, String phone, String dob,
                  List<String> hospitalIds, String departmentId, List<String> patientIds,
                  String specialization, int experienceYears, int totalPatients, float rating,
                  String fee, String about, String profileImage,
                  List<String> availableDates, List<String> availableTimes) {

        super(uid, name, email, password, phone, dob, UserType.DOCTOR);

        this.hospitalIds = hospitalIds != null ? hospitalIds : new ArrayList<>();
        this.departmentId = departmentId;
        this.patientIds = patientIds != null ? patientIds : new ArrayList<>();

        this.specialization = specialization;
        this.experienceYears = experienceYears;
        this.totalPatients = totalPatients;
        this.rating = rating;

        this.fee = fee;
        this.about = about;
        this.profileImage = profileImage;

        this.availableDates = availableDates != null ? availableDates : new ArrayList<>();
        this.availableTimes = availableTimes != null ? availableTimes : new ArrayList<>();
    }

    public List<String> getHospitalIds() { return hospitalIds; }
    public void setHospitalIds(List<String> hospitalIds) { this.hospitalIds = hospitalIds; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public List<String> getPatientIds() { return patientIds; }
    public void setPatientIds(List<String> patientIds) { this.patientIds = patientIds; }

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
}
