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
    private boolean isBlocked;

    public User(int userId, int userRoleId, String userRole, String email, String firstName, String lastName, String phone, boolean isBlocked) {
        this.userId = userId;
        this.userRoleId = userRoleId;
        this.userRole = userRole;
        this.email = email;

        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.isBlocked = isBlocked;
    }
/*
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
    */

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

    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
                ", userRole="+ userRole +
               ", userRoleId=" + userRoleId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (isBlocked != user.isBlocked) return false;
        if (userId != user.userId) return false;
        if (userRoleId != user.userRoleId) return false;
        if (!email.equals(user.email)) return false;
        if (!firstName.equals(user.firstName)) return false;
        if (!lastName.equals(user.lastName)) return false;
        if (!phone.equals(user.phone)) return false;
        if (!userRole.equals(user.userRole)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + userRoleId;
        result = 31 * result + userRole.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + phone.hashCode();
        result = 31 * result + (isBlocked ? 1 : 0);
        return result;
    }
}

