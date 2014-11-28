package com.naukma.cauliflower.entities;

import java.io.Serializable;

/**
 * Created by Max on 27.11.2014.
 */
public class ServiceInstance implements Serializable {
    private int instanceId;
    private int userId;
    private int serviceLocationId;
    private String locationAddress;
    private int locationLongitude;
    private int locationLatitude;
    private int serviceId;
 //   private Service service;
    private int instanceStatusId;
    private String instanceStatus;
    private int cableId;
    
    public ServiceInstance(int instanceId, int userId,
                           int serviceLocationId, String locationAddress,
                           int locationLongitude, int locationLatitude,
                           int serviceId, int instanceStatusId,
                           String instanceStatus, int cableId) {
        this.instanceId = instanceId;
        this.userId = userId;
        this.serviceLocationId = serviceLocationId;
        this.locationAddress = locationAddress;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
        this.serviceId = serviceId;
        this.instanceStatusId = instanceStatusId;
        this.instanceStatus = instanceStatus;
        this.cableId = cableId;
    //    this.service = null;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public int getUserId() {
        return userId;
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

    public int getServiceId() {
        return serviceId;
    }


    public int getInstanceStatusId() {
        return instanceStatusId;
    }

    public String getInstanceStatus() {
        return instanceStatus;
    }

    public int getCableId() {
        return cableId;
    }
}
