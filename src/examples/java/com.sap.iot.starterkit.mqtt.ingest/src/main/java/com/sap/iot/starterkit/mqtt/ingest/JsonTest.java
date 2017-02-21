package com.sap.iot.starterkit.mqtt.ingest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;
import com.sap.iot.starterkit.mqtt.ingest.json.GsonFactory;
import com.sap.iot.starterkit.mqtt.ingest.type.Mapping;
import com.sap.iot.starterkit.mqtt.ingest.type.MappingConfiguration;
import com.sap.iot.starterkit.mqtt.ingest.type.MappingConfiguration.Type;
import com.sap.iot.starterkit.mqtt.ingest.type.MessageEnvelope;
import com.sap.iot.starterkit.mqtt.ingest.type.Reference;

public class JsonTest {

	public static void main(String[] args)
	throws Exception {
		Gson gson = GsonFactory.buildGson();

		String payload = "{\"payloadFields\":{\"celcius\":33.09}}";

		MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setPayload(payload.getBytes("UTF-8"));

		MappingConfiguration input = new MappingConfiguration();
		input.setType(Type.JSON);
		List<Reference> inputReferences = new ArrayList<Reference>();
		Reference inputReference1 = new Reference();
		inputReference1.setName("payloadFields/celcius");
		inputReference1.setType(Type.DOUBLE);
		inputReferences.add(inputReference1);
		input.setReferences(inputReferences);
		MappingConfiguration output = new MappingConfiguration();
		output.setType(Type.JSON);
		List<Reference> outputReferences = new ArrayList<Reference>();
		Reference outputReference1 = new Reference();
		outputReference1.setName("value");
		outputReference1.setType(Type.DOUBLE);
		outputReferences.add(outputReference1);
		output.setReferences(outputReferences);

		Mapping mapping = new Mapping();
		mapping.setInput(input);
		mapping.setOutput(output);
		mapping.setMessageTypeId("123456");

		MessageEnvelope messageEnvelope = MessageEnvelope.fromMqttMessage(mqttMessage, mapping);

		System.out.println(gson.toJson(messageEnvelope));
	}

}
