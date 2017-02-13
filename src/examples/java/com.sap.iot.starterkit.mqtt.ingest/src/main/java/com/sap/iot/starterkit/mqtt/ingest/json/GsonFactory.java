package com.sap.iot.starterkit.mqtt.ingest.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sap.iot.starterkit.mqtt.ingest.type.Authorization;
import com.sap.iot.starterkit.mqtt.ingest.type.Configuration;
import com.sap.iot.starterkit.mqtt.ingest.type.MappingConfiguration;
import com.sap.iot.starterkit.mqtt.ingest.type.MessageEnvelope;
import com.sap.iot.starterkit.mqtt.ingest.type.MqttConfiguration;
import com.sap.iot.starterkit.mqtt.ingest.type.Reference;

public class GsonFactory {

	public static Gson buildGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Configuration.class, new ConfigurationDeserializer());
		builder.registerTypeAdapter(MqttConfiguration.class, new MqttConfigurationDeserializer());
		builder.registerTypeAdapter(Authorization.class, new AuthorizationDeserializer());
		builder.registerTypeAdapter(MappingConfiguration.class,
			new MappingConfigurationDeserializer());
		builder.registerTypeAdapter(Reference.class, new ReferenceDeserializer());
		builder.registerTypeAdapter(MessageEnvelope.class, new MessageEnvelopeSerializer());
		return builder.create();
	}

}
