package com.sap.iot.starterkit.mqtt.ingest.type;

import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MessageEnvelope {

	private String messageType;

	private List<Message> messages;

	public String getMessageType() {
		return messageType;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public static MessageEnvelope fromMqttMessage(MqttMessage mqttMessage) {
		return new MessageEnvelope();
	}

}
