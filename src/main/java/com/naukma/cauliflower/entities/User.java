package com.naukma.cauliflower.entities;

import java.io.Serializable;

/**
 * Created by Eugene on 26.11.2014.
 */
public class User implements Serializable {

    private int userId;
    private int userRoleId;
    private String userRole;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public User(int userId, int userRoleId, String userRole, String email, String firstName, String lastName, String phone) {
        this.userId = userId;
        this.userRoleId = userRoleId;
        this.userRole = userRole;
        this.email = email;

        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public User(int userRoleId, String userRole, String email, String firstName, String lastName, String phone) {
        this.userRoleId = userRoleId;
        this.userRole = userRole;
        this.email = email;

        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public int getUserId() {
        return userId;
    }

    public int getUserRoleId() {
        return userRoleId;
    }


    public String getUserRole() {
        return userRole;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }


    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
                ", userRole= "+ userRole +
               ", userRoleId=" + userRoleId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}

