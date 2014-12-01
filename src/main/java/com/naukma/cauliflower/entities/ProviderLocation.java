package com.naukma.cauliflower.entities;

import java.io.Serializable;

/**
 * Created by Max on 29.11.2014.
 */
public class ProviderLocation  implements Serializable{

    private int providerLocationId;
    private String locationAddress;
    private double locationLongitude;
    private double locationLatitude;


    public ProviderLocation(int providerLocationId, String locationAddress, double locationLongitude, double locationLatitude) {
        this.providerLocationId = providerLocationId;
        this.locationAddress = locationAddress;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
    }

    public int getProviderLocationId() {
        return providerLocationId;
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
}
