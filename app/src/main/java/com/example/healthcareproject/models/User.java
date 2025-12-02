package com.example.healthcareproject.models;


public class User {

    public enum UserType {
        USER, PATIENT, DOCTOR, ADMIN
    }

    private String uid;
    private String name;
    private String email;

    private String password;
    private String phone;
    private String dob;
    private UserType userType;


    public User() {
        this.userType = UserType.USER;
    }


    public User(String uid, String name, String email, String password, String phone, String dob) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.dob = dob;
        this.userType = UserType.USER;
    }


    public User(String uid, String name, String email, String password, String phone, String dob, UserType userType) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.dob = dob;
        this.userType = userType != null ? userType : UserType.USER;
    }


    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDob() { return dob; }
    public UserType getUserType() { return userType; }


    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDob(String dob) { this.dob = dob; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
