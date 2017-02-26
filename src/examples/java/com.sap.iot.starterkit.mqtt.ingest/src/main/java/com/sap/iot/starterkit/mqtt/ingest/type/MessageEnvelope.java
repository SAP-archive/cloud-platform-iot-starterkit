package com.sap.iot.starterkit.mqtt.ingest.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.iot.starterkit.mqtt.ingest.json.GsonFactory;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((messages == null) ? 0 : messages.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MessageEnvelope other = (MessageEnvelope) obj;
		if (messageType == null) {
			if (other.messageType != null) return false;
		}
		else if (!messageType.equals(other.messageType)) return false;
		if (messages == null) {
			if (other.messages != null) return false;
		}
		else if (!messages.equals(other.messages)) return false;
		return true;
	}

	public static MessageEnvelope fromMqttMessage(MqttMessage mqttMessage, Mapping mapping) {
		if (mqttMessage == null || mqttMessage.getPayload() == null ||
			mqttMessage.getPayload().length == 0) {
			throw new IllegalStateException("MQTT message is null or empty");
		}
		String mqttPayload = new String(mqttMessage.getPayload());

		Map<String, Object> fields = new HashMap<String, Object>();

		Type inputType = mapping.getInput().getType();
		List<Reference> inputReferences = mapping.getInput().getReferences();
		List<Reference> outputReferences = mapping.getOutput().getReferences();

		switch (inputType) {
		case DOUBLE:
			fields.put(outputReferences.get(0).getName(), Double.parseDouble(mqttPayload));
			break;
		case INTEGER:
			fields.put(outputReferences.get(0).getName(), Integer.parseInt(mqttPayload));
			break;
		case LONG:
			fields.put(outputReferences.get(0).getName(), Long.parseLong(mqttPayload));
			break;
		case FLOAT:
			fields.put(outputReferences.get(0).getName(), Float.parseFloat(mqttPayload));
			break;
		case STRING:
			fields.put(outputReferences.get(0).getName(), mqttPayload);
			break;
		case BOOLEAN:
			fields.put(outputReferences.get(0).getName(), Boolean.parseBoolean(mqttPayload));
			break;
		case JSON:
			if (outputReferences == null || outputReferences.isEmpty()) {
				Message message = GsonFactory.buildGson().fromJson(mqttPayload, Message.class);
				fields = message.getFields();
			}
			else {
				JsonElement jsonElement = GsonFactory.buildParser().parse(mqttPayload);
				for (int i = 0; i < inputReferences.size(); i++) {

					String inputReferenceName = inputReferences.get(i).getName();
					String outputReferenceName = outputReferences.get(i).getName();
					JsonElement jsonValue = revealJsonValue(inputReferenceName, jsonElement);

					switch (inputReferences.get(i).getType()) {
					case DOUBLE:
						fields.put(outputReferenceName, jsonValue.getAsDouble());
						break;
					case INTEGER:
						fields.put(outputReferenceName, jsonValue.getAsInt());
						break;
					case LONG:
						fields.put(outputReferenceName, jsonValue.getAsLong());
						break;
					case FLOAT:
						fields.put(outputReferenceName, jsonValue.getAsFloat());
						break;
					case STRING:
						fields.put(outputReferenceName, jsonValue.getAsString());
						break;
					case BOOLEAN:
						fields.put(outputReferenceName, jsonValue.getAsBoolean());
						break;
					default:
						throw new IllegalStateException(
							String.format("Unsupported mapping input type '%1$s'",
								inputReferences.get(i).getType()));
					}
				}
			}
			break;
		default:
			throw new IllegalStateException(
				String.format("Unsupported mapping input type '%1$s'", inputType));
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

	private static JsonElement revealJsonValue(String referenceName, JsonElement root) {
		String[] parts = referenceName.split("/");
		if (parts.length == 1) {
			return ((JsonObject) root).get(parts[0]);
		}
		int begIndex = referenceName.indexOf("/") + 1;
		return revealJsonValue(referenceName.substring(begIndex),
			((JsonObject) root).get(parts[0]));
	}

}