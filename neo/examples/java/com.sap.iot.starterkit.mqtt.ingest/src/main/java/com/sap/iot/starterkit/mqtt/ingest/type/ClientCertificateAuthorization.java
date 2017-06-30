package com.sap.iot.starterkit.mqtt.ingest.type;

public class ClientCertificateAuthorization
implements Authorization {

	private String keyStoreLocation;

	private transient String keyStorePassword;

	private String trustStoreLocation;

	private transient String trustStorePassword;

	public String getKeyStoreLocation() {
		return keyStoreLocation;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public String getTrustStoreLocation() {
		return trustStoreLocation;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

}
