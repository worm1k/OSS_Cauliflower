package com.naukma.cauliflower.entities;

import java.io.Serializable;

public class Circuit implements Serializable {

	public Circuit(int cableId, int portId, int routerId) {
		super();
		this.cableId = cableId;
		this.portId = portId;
		this.routerId = routerId;
	}

	public int getCableId() {
		return cableId;
	}

	public void setCableId(int cableId) {
		this.cableId = cableId;
	}

	public int getPortId() {
		return portId;
	}

	public void setPortId(int portId) {
		this.portId = portId;
	}

	public int getRouterId() {
		return routerId;
	}

	public void setRouterId(int routerId) {
		this.routerId = routerId;
	}

	@Override
	public String toString() {
		return "Circuit [cableId=" + cableId + ", portId=" + portId
				+ ", routerId=" + routerId + "]";
	}

	private int cableId;
	private int portId;
	private int routerId;
}
