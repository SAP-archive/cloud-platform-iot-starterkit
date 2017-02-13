package com.sap.iot.starterkit.mqtt.ingest.connect;

import javax.net.ssl.SSLSocketFactory;

import com.sap.iot.starterkit.mqtt.ingest.type.Authorization;
import com.sap.iot.starterkit.mqtt.ingest.type.BasicAuthorization;
import com.sap.iot.starterkit.mqtt.ingest.type.ClientCertificateAuthorization;
import com.sap.iot.starterkit.mqtt.ingest.type.MqttConfiguration;

public class MqttClientFactory {

	public static MqttClient buildMqttClient(MqttConfiguration configuration) {
		String clientId = configuration.getClientId();

		Authorization authorization = configuration.getAuthorization();
		if (authorization == null) {
			return new MqttClient(clientId);
		}

		if (authorization instanceof BasicAuthorization) {
			String username = ((BasicAuthorization) authorization).getUsername();
			String password = ((BasicAuthorization) authorization).getPassword();
			return new MqttClient(clientId, username, password);
		}
		else if (authorization instanceof ClientCertificateAuthorization) {

			// TODO: prepare SSL socket factory
			SSLSocketFactory sslSocketFactory = null;

			return new MqttClient(clientId, sslSocketFactory);
		}

		throw new IllegalStateException("Unknown authorization settings");
	}

}
