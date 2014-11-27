package com.naukma.cauliflower.entities;

/**
 * Created by Eugene on 26.11.2014.
 */
public class User {

    private long userId;
    private long userRoleId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public User(long userId, long userRoleId, String email, String firstName, String lastName, String phone) {
        this.userId = userId;
        this.userRoleId = userRoleId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userRoleId=" + userRoleId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public long getUserId() {
        return userId;
    }

    public long getUserRoleId() {
        return userRoleId;
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
}

