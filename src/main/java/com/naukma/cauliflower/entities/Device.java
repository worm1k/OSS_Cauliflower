package com.naukma.cauliflower.entities;

/**
 * Created by Eugene on 17.12.2014.
 */
import java.io.Serializable;

public class Device implements Serializable {

	private int id;
	private int occupiedPortCap;
	private int freePortCap;

	public Device(int id, int occupiedPortCap, int freePortCap) {
		super();
		this.id = id;
		this.occupiedPortCap = occupiedPortCap;
		this.freePortCap = freePortCap;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
