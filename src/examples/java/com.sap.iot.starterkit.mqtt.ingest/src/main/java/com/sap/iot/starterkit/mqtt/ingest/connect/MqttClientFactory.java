package com.sap.iot.starterkit.mqtt.ingest.connect;

import com.sap.iot.starterkit.mqtt.ingest.type.Authorization;
import com.sap.iot.starterkit.mqtt.ingest.type.BasicAuthorization;
import com.sap.iot.starterkit.mqtt.ingest.type.ClientCertificateAuthorization;
import com.sap.iot.starterkit.mqtt.ingest.type.MqttConfiguration;

/**
 * A factory for instantiating the {@link MqttClient} instances based on the user provided
 * {@link MqttConfiguration} settings
 */
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
			throw new UnsupportedOperationException(
				"Client certificate authentication type is not supported yet");
		}

		throw new IllegalStateException("Unknown authorization settings");
	}

}
