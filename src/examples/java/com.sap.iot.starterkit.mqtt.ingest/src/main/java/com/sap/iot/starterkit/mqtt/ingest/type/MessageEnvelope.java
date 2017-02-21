package com.sap.iot.starterkit.mqtt.ingest.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
			if (mapping.getOutput().getReferences() == null ||
				mapping.getOutput().getReferences().isEmpty()) {

				Gson gson = GsonFactory.buildGson();
				Message message = gson.fromJson(mqttPayload, Message.class);
				fields = message.getFields();
			}
			else {
				// Gson gson = GsonFactory.buildGson();
				// Message message = gson.fromJson(mqttPayload, Message.class);

				JsonElement json = new JsonParser().parse(mqttPayload);

				List<Reference> outputReferences = mapping.getOutput().getReferences();
				List<Reference> inputReferences = mapping.getInput().getReferences();

				for (int i = 0; i < inputReferences.size(); i++) {

					String n = inputReferences.get(i).getName();

					// System.out.println(n);
					String[] parts = n.split("/");

					JsonElement sub = ((JsonObject) json).get(parts[0]);

					JsonElement val = ((JsonObject) sub).get(parts[1]);

					switch (inputReferences.get(i).getType()) {
					case DOUBLE:
						fields.put(outputReferences.get(i).getName(), val.getAsDouble());
						break;
					case INTEGER:
						fields.put(outputReferences.get(i).getName(), val.getAsInt());
						break;
					case LONG:
						fields.put(outputReferences.get(i).getName(), val.getAsLong());
						break;
					case FLOAT:
						fields.put(outputReferences.get(i).getName(), val.getAsFloat());
						break;
					case STRING:
						fields.put(outputReferences.get(i).getName(), val.getAsString());
						break;
					case BOOLEAN:
						fields.put(outputReferences.get(i).getName(), val.getAsBoolean());
						break;
					default:
						throw new IllegalStateException(
							String.format("Unsupported mapping input type '%1$s'", type));
					}
				}
			}
			break;
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
