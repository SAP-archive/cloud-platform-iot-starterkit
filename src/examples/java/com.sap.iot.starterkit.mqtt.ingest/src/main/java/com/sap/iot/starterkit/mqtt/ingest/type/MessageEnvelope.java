package com.sap.iot.starterkit.mqtt.ingest.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.sap.iot.starterkit.mqtt.ingest.type.MappingConfiguration.Type;

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

	public static MessageEnvelope fromMqttMessage(MqttMessage mqttMessage, Mapping mapping) {

		if (mqttMessage == null || mqttMessage.getPayload() == null ||
			mqttMessage.getPayload().length == 0) {
			throw new IllegalStateException("MQTT message is null or empty");
		}

		String mqttPayload = new String(mqttMessage.getPayload());

		Map<String, Object> fields = new HashMap<String, Object>();

		Type type = mapping.getInput().getType();
		switch (type) {
		case DOUBLE:
			fields.put(mapping.getOutput().getReferences().get(0).getName(),
				Double.parseDouble(mqttPayload));
			break;
		case INTEGER:
			fields.put(mapping.getOutput().getReferences().get(0).getName(),
				Integer.parseInt(mqttPayload));
			break;
		case LONG:
			fields.put(mapping.getOutput().getReferences().get(0).getName(),
				Long.parseLong(mqttPayload));
			break;
		case FLOAT:
			fields.put(mapping.getOutput().getReferences().get(0).getName(),
				Float.parseFloat(mqttPayload));
			break;
		case STRING:
			fields.put(mapping.getOutput().getReferences().get(0).getName(), mqttPayload);
			break;
		case BOOLEAN:
			fields.put(mapping.getOutput().getReferences().get(0).getName(),
				Boolean.parseBoolean(mqttPayload));
			break;
		case JSON:
		default:
			throw new IllegalStateException(
				String.format("Unsupported mapping input type '%1$s'", type));
		}

		Message message = new Message();
		message.setFields(fields);

		List<Message> messages = new ArrayList<Message>();
		messages.add(message);

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		messageEnvelope.setMessages(messages);
		messageEnvelope.setMessageType(mapping.getMessageTypeId());

		return messageEnvelope;
	}

}
