package com.naukma.cauliflower.entities;

/**
 * Created by Eugene on 17.12.2014.
 */
public class Device {

    private final int id;
    private final int occupied;
    private final int free;

    public Device(int id, int occupied, int free) {
        this.id= id;
        this.occupied= occupied;
        this.free= free;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", occupied=" + occupied +
                ", free=" + free +
                '}';
    }
    public int getFree() {
        return free;
    }

    public int getOccupied() {
        return occupied;
    }

    public int getId() {
        return id;
    }

}
