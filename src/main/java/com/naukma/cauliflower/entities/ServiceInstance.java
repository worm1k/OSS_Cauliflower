package com.naukma.cauliflower.entities;

import com.sun.xml.internal.ws.wsdl.writer.document.*;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Created by Max on 27.11.2014.
 */
public class ServiceInstance implements Serializable {
    private int instanceId;
    private int userId;
    private ServiceLocation serviceLocation;
    private int serviceId;
 //   private Service service;
    private int instanceStatusId;
    private String instanceStatus;
    private int cableId;
    private boolean isBlocked;

    public ServiceInstance(int instanceId, int userId,
                           int serviceLocationId, String locationAddress,
                           int locationLongitude, int locationLatitude,
                           int serviceId, int instanceStatusId,
                           String instanceStatus, int cableId, boolean isBlocked) {
        this.instanceId = instanceId;
        this.userId = userId;
        serviceLocation = new ServiceLocation(serviceLocationId,locationAddress,locationLongitude,locationLatitude);
        this.serviceId = serviceId;
        this.instanceStatusId = instanceStatusId;
        this.instanceStatus = instanceStatus;
        this.cableId = cableId;
        this.isBlocked = isBlocked;
    //    this.service = null;
    }


    public ServiceInstance(int instanceId, int userId,
                           ServiceLocation serviceLocation, int serviceId,
                           int instanceStatusId, String instanceStatus,
                           int cableId, boolean isBlocked) {
        this.instanceId = instanceId;
        this.userId = userId;
        this.serviceLocation = serviceLocation;
        this.serviceId = serviceId;
        this.instanceStatusId = instanceStatusId;
        this.instanceStatus = instanceStatus;
        this.cableId = cableId;
        this.isBlocked = isBlocked;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public int getUserId() {
        return userId;
    }

    public ServiceLocation getServiceLocation() {
        return serviceLocation;
    }

    public int getServiceId() {

        return serviceId;
    }


    public int getInstanceStatusId() {

        return instanceStatusId;
    }

    public String getInstanceStatus()
    {
        return instanceStatus;
    }

    public int getCableId() {

        return cableId;
    }
    public boolean getIsBlocked(){
        return this.isBlocked;
    }
}
