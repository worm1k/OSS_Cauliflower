package com.naukma.cauliflower.entities;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Igor
 * Date: 17.12.14
 * Time: 0:59
 * To change this template use File | Settings | File Templates.
 */
public class Port implements Serializable{

    public Port(int id, int routerId, boolean used) {
        this.id = id;
        this.routerId=routerId;
        this.used = used;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public int getRouterId() {
        return routerId;
    }

    public void setRouterId(int routerId) {
        this.routerId = routerId;
    }



    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }



    private int id;
    private boolean used;
    private int routerId;
    }
