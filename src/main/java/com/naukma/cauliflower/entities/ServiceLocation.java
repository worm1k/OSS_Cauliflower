package com.naukma.cauliflower.entities;

import java.io.Serializable;

/**
 * Created by Max on 29.11.2014.
 */
public class ServiceLocation implements Serializable {

    private int serviceLocationId;
    private String locationAddress;
    private double locationLongitude;
    private double locationLatitude;


    public ServiceLocation(int serviceLocationId, String locationAddress, double locationLongitude, double locationLatitude) {
        this.serviceLocationId = serviceLocationId;
        this.locationAddress = locationAddress;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
    }

    public int getServiceLocationId() {
        return serviceLocationId;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public void setServiceLocationId(int serviceLocationId) {
        this.serviceLocationId = serviceLocationId;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    @Override
    public String toString() {
        return "ServiceLocation{" +
                "serviceLocationId=" + serviceLocationId +
                ", locationAddress='" + locationAddress + '\'' +
                ", locationLongitude=" + locationLongitude +
                ", locationLatitude=" + locationLatitude +
                '}';
    }
}
