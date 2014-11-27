package com.naukma.cauliflower.entities;

import java.io.Serializable;

/**
 * Created by Max on 27.11.2014.
 */
public class ServiceOrder implements Serializable {
    private int serviceOrderId;
    private int orderStatusId;
    private String orderStatus;
    private int serviceInstanceId;
    private int orderScenarioId;
    private String orderScenario;

    public ServiceOrder(int serviceOrderId, int orderStatusId, String orderStatus, int serviceInstanceId, int orderScenarioId, String orderScenario) {
        this.serviceOrderId = serviceOrderId;
        this.orderStatusId = orderStatusId;
        this.orderStatus = orderStatus;
        this.serviceInstanceId = serviceInstanceId;
        this.orderScenarioId = orderScenarioId;
        this.orderScenario = orderScenario;
    }


    public int getServiceOrderId() {
        return serviceOrderId;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public int getServiceInstanceId() {
        return serviceInstanceId;
    }

    public int getOrderScenarioId() {
        return orderScenarioId;
    }

    public String getOrderScenario() {
        return orderScenario;
    }

}
