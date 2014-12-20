package com.naukma.cauliflower.entities;

import java.io.Serializable;

public class CIA implements Serializable{

	public CIA(String router, String port, int siId,
			String userEmail, String userFName, String userLName) {
		super();
		this.router = router;
		this.port = port;
		this.siId = siId;
		this.userEmail = userEmail;
		this.userFName = userFName;
		this.userLName = userLName;
	}

	public String getRouterId() {
		return router;
	}

	public void setRouterId(String routerId) {
		this.router = routerId;
	}

	public String getPortId() {
		return port;
	}

	public void setPortId(String portId) {
		this.port = portId;
	}

	public int getSiId() {
		return siId;
	}

	public void setSiId(int siId) {
		this.siId = siId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserFName() {
		return userFName;
	}

	public void setUserFName(String userFName) {
		this.userFName = userFName;
	}

	public String getUserLName() {
		return userLName;
	}

	public void setUserLName(String userLName) {
		this.userLName = userLName;
	}

	@Override
	public String toString() {
		return "CIA [router=" + router + ", port=" + port + ", siId="
				+ siId + ", userEmail=" + userEmail
				+ ", userFName=" + userFName + ", userLName=" + userLName + "]";
	}

	private String router;
	private String port;
	private int siId;
	private String userEmail;
	private String userFName;
	private String userLName;

}
