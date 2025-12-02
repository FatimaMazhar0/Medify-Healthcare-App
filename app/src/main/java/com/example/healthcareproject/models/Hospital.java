package com.example.healthcareproject.models;

import java.util.ArrayList;
import java.util.List;

public class Hospital {

    private String hospitalId;
    private String name;
    private String address;

    private String city;

    // Updated location fields
    private double latitude;
    private double longitude;



    String openingHours = "24/7";
    private String contactNumber;
    private String imageUrl;

    private List<String> departmentIds;
    private List<String> doctorIds;

    public Hospital() {
        this.departmentIds = new ArrayList<>();
        this.doctorIds = new ArrayList<>();
    }

    public Hospital(String hospitalId, String name, String address,
                    double latitude, double longitude,
                    String contactNumber, String imageUrl,
                    List<String> departmentIds, List<String> doctorIds) {

        this.hospitalId = hospitalId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contactNumber = contactNumber;
        this.imageUrl = imageUrl;

        this.departmentIds = departmentIds != null ? departmentIds : new ArrayList<>();
        this.doctorIds = doctorIds != null ? doctorIds : new ArrayList<>();
    }


    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getDepartmentIds() { return departmentIds; }
    public void setDepartmentIds(List<String> departmentIds) { this.departmentIds = departmentIds; }

    public List<String> getDoctorIds() { return doctorIds; }
    public void setDoctorIds(List<String> doctorIds) { this.doctorIds = doctorIds; }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
