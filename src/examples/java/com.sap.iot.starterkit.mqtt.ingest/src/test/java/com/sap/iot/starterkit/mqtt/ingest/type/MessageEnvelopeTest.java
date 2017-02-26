package com.sap.iot.starterkit.mqtt.ingest.type;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Assert;
import org.junit.Test;

import com.sap.iot.starterkit.mqtt.ingest.AbstractServlet;
import com.sap.iot.starterkit.mqtt.ingest.TestSuite;

public class MessageEnvelopeTest
extends TestSuite {

	@Test
	public void positiveJsonDeepMapping() {
		assertMessageEnvelope("input_json_deep.json", "mapping_json_deep.json", "output_json.json");
	}

	@Test
	public void positiveJsonFlatMapping() {
		assertMessageEnvelope("input_json_flat.json", "mapping_json_flat.json", "output_json.json");
	}

	@Test
	public void positiveDoubleMapping()
	throws Exception {
		assertMessageEnvelope("input_double.txt", "mapping_double.json", "output_double.json");
	}

	@Test
	public void positiveIntegerMapping() {
		assertMessageEnvelope("input_integer.txt", "mapping_integer.json", "output_integer.json");
	}

	@Test
	public void positiveStringMapping() {
		assertMessageEnvelope("input_string.txt", "mapping_string.json", "output_string.json");
	}

	@Test
	public void positiveBooleanMapping() {
		assertMessageEnvelope("input_boolean.txt", "mapping_boolean.json", "output_boolean.json");
	}

	private void assertMessageEnvelope(String inputSample, String mappingSample,
		String outputSample) {
		String payload = getResourceAsString(inputSample);
		MqttMessage mqttMessage = new MqttMessage();
		try {
			mqttMessage.setPayload(payload.getBytes(AbstractServlet.ENCODING));
		}
		catch (UnsupportedEncodingException e) {
			Assert.fail(e.getMessage());
		}

		Mapping mapping = gson.fromJson(getResourceAsString(mappingSample), Mapping.class);

		MessageEnvelope expectedMessageEnvelope = gson.fromJson(getResourceAsString(outputSample),
			MessageEnvelope.class);

		MessageEnvelope actualMessageEnvelope = MessageEnvelope.fromMqttMessage(mqttMessage,
			mapping);

		assertEquals("Unexpected message envelope", expectedMessageEnvelope, actualMessageEnvelope);
	}

}
