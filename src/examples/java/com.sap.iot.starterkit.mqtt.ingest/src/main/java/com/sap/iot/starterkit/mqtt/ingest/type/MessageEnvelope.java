package com.sap.iot.starterkit.mqtt.ingest.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MessageEnvelope {

	private String messageType;

	private List<Message> messages;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public static MessageEnvelope fromMqttMessage(MqttMessage mqttMessage) {
		String mqttPayload = new String(mqttMessage.getPayload());
		double mqttValue = Double.parseDouble(mqttPayload);

		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("value", mqttValue);

		Message message = new Message();
		message.setFields(fields);

		List<Message> messages = new ArrayList<Message>();
		messages.add(message);

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		messageEnvelope.setMessages(messages);
		messageEnvelope.setMessageType("a825148f132eb9cfa5ef");

		return messageEnvelope;
	}

}
