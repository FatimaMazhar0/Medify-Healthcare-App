package com.example.healthcareproject.models;

import java.util.ArrayList;
import java.util.List;

public class Department {

    private String departmentId;
    private String name;
    private String hospitalId;
    private List<String> doctorIds;

    public Department() {
        this.doctorIds = new ArrayList<>();
    }

    public Department(String departmentId, String name, String hospitalId, List<String> doctorIds) {
        this.departmentId = departmentId;
        this.name = name;
        this.hospitalId = hospitalId;
        this.doctorIds = doctorIds != null ? doctorIds : new ArrayList<>();
    }


    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public List<String> getDoctorIds() { return doctorIds; }
    public void setDoctorIds(List<String> doctorIds) { this.doctorIds = doctorIds; }
}
