package com.naukma.cauliflower.entities;

/**
 * Created by Max on 29.11.2014.
 */
public class ProviderLocation {

    private int serviceLocationId;
    private String locationAddress;
    private int locationLongitude;
    private int locationLatitude;


    public ProviderLocation(int serviceLocationId, String locationAddress, int locationLongitude, int locationLatitude) {
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

    public int getLocationLongitude() {
        return locationLongitude;
    }

    public int getLocationLatitude() {
        return locationLatitude;
    }
}
