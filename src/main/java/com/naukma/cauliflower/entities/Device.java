package com.naukma.cauliflower.entities;

/**
 * Created by Eugene on 17.12.2014.
 */
import java.io.Serializable;

public class Device implements Serializable {

	private String device;
	private int occupiedPortCap;
	private int freePortCap;

	public Device(String device, int occupiedPortCap, int freePortCap) {
		super();
		this.device = device;
		this.occupiedPortCap = occupiedPortCap;
		this.freePortCap = freePortCap;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public int getOccupiedPortCap() {
		return occupiedPortCap;
	}

	public void setOccupiedPortCap(int occupiedPortCap) {
		this.occupiedPortCap = occupiedPortCap;
	}

	public int getFreePortCap() {
		return freePortCap;
	}

	public void setFreePortCap(int freePortCap) {
		this.freePortCap = freePortCap;
	}

}
