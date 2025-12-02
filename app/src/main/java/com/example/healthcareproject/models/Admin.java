package com.example.healthcareproject.models;

public class Admin extends User {

    private String roleDescription;

    public Admin() {
        super();
        setUserType(UserType.ADMIN);
    }

    public Admin(String uid, String name, String email, String password, String phone, String dob, String roleDescription) {
        super(uid, name, email, password,  phone, dob, UserType.ADMIN);
        this.roleDescription = roleDescription;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
}
