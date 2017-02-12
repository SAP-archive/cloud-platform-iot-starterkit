package com.sap.iot.starterkit.mqtt.ingest.type;

public class BasicAuthorization
implements Authorization {

	private String username;

	private transient String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
