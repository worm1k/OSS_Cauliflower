package com.naukma.cauliflower.entities;

import java.io.Serializable;

public class CIA implements Serializable{

	public CIA(int routerId, int portId, int siId, int userId,
			String userEmail, String userFName, String userLName) {
		super();
		this.routerId = routerId;
		this.portId = portId;
		this.siId = siId;
		this.userId = userId;
		this.userEmail = userEmail;
		this.userFName = userFName;
		this.userLName = userLName;
	}

	public int getRouterId() {
		return routerId;
	}

	public void setRouterId(int routerId) {
		this.routerId = routerId;
	}

	public int getPortId() {
		return portId;
	}

	public void setPortId(int portId) {
		this.portId = portId;
	}

	public int getSiId() {
		return siId;
	}

	public void setSiId(int siId) {
		this.siId = siId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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
		return "CIA [routerId=" + routerId + ", portId=" + portId + ", siId="
				+ siId + ", userId=" + userId + ", userEmail=" + userEmail
				+ ", userFName=" + userFName + ", userLName=" + userLName + "]";
	}

	private int routerId;
	private int portId;
	private int siId;
	private int userId;
	private String userEmail;
	private String userFName;
	private String userLName;

}
