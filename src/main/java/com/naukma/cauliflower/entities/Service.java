package com.naukma.cauliflower.entities;

import java.io.Serializable;

public class Service implements Serializable {
    private int serviceId;
    private int providerLocationId;
    private int serviceTypeId;

    private String locationAddress;
    private double locationLongitude;
    private double locationLatitude;

    private String serviceTypeName;
    private String serviceSpeed;

    private double price;

    public Service(int serviceTypeId, String locationAddress, double locationLongitude, double locationLatitude,
                   String serviceTypeName, String serviceSpeed, int providerLocationId, int serviceId, double price) {
        this.serviceTypeId = serviceTypeId;
        this.locationAddress = locationAddress;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
        this.serviceTypeName = serviceTypeName;
        this.serviceSpeed = serviceSpeed;
        this.providerLocationId = providerLocationId;
        this.serviceId = serviceId;
        this.price = price;
    }

    public int getServiceId() {
        return serviceId;
    }

    public int getProviderLocationId() {
        return providerLocationId;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public String getServiceSpeed() {
        return serviceSpeed;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public void setProviderLocationId(int providerLocationId) {
        this.providerLocationId = providerLocationId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public void setServiceSpeed(String serviceSpeed) {
        this.serviceSpeed = serviceSpeed;
    }

    public void setPrice(double price) { this.price = price; }

    public double getPrice() { return price; }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", providerLocationId=" + providerLocationId +
                ", serviceTypeId=" + serviceTypeId +
                ", locationAddress='" + locationAddress + '\'' +
                ", locationLongitude=" + locationLongitude +
                ", locationLatitude=" + locationLatitude +
                ", serviceTypeName='" + serviceTypeName + '\'' +
                ", serviceSpeed='" + serviceSpeed + '\'' +
                ", price=" + price +
                '}';
    }
}
