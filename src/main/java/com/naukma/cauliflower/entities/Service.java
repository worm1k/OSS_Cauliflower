package com.naukma.cauliflower.entities;

import java.io.Serializable;

public class Service implements Serializable {
    private int serviceId;
    private int providerLocationId;
    private int serviceTypeId;

    private String locationAddress;
    private int locationLongitude;
    private int locationLatitude;

    private String serviceTypeName;
    private String serviceSpeed;

    public Service(int serviceTypeId, String locationAddress, int locationLongitude, int locationLatitude,
                   String serviceTypeName, String serviceSpeed, int providerLocationId, int serviceId) {
        this.serviceTypeId = serviceTypeId;
        this.locationAddress = locationAddress;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
        this.serviceTypeName = serviceTypeName;
        this.serviceSpeed = serviceSpeed;
        this.providerLocationId = providerLocationId;
        this.serviceId = serviceId;
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

    public int getLocationLongitude() {
        return locationLongitude;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public int getLocationLatitude() {
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

    public void setLocationLongitude(int locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public void setLocationLatitude(int locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public void setServiceSpeed(String serviceSpeed) {
        this.serviceSpeed = serviceSpeed;
    }
}
