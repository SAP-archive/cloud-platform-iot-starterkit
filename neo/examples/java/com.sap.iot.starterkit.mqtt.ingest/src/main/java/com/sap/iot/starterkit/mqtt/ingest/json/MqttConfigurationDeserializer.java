package com.sap.iot.starterkit.mqtt.ingest.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.sap.iot.starterkit.mqtt.ingest.type.Authorization;
import com.sap.iot.starterkit.mqtt.ingest.type.MqttConfiguration;

/**
 * A custom JSON deserializer for {@link MqttConfiguration} type
 */
public class MqttConfigurationDeserializer
extends AbstractDeserializer<MqttConfiguration> {

	/**
	 * GSON invokes this call-back method during deserialization when it encounters a field of type
	 * {@link MqttConfiguration}
	 *
	 * @param json
	 *            the JSON data being deserialized
	 * @param type
	 *            The type of the Object to deserialize to
	 * @return a deserialized object of type {@link MqttConfiguration}
	 * @throws JsonParseException
	 *             if JSON is not in the expected format
	 */
	@Override
	public MqttConfiguration deserialize(JsonElement json, Type type, JsonDeserializationContext context)
	throws JsonParseException {

		checkNotNull(json);
		checkJsonObject(json);

		JsonObject jsonObject = json.getAsJsonObject();

		JsonElement serverUriElement = jsonObject.get("serverUri");
		checkNotNull(serverUriElement);
		checkJsonPrimitive(serverUriElement);

		JsonPrimitive serverUriPrimitive = serverUriElement.getAsJsonPrimitive();
		checkString(serverUriPrimitive);

		String clientId = null;
		JsonElement clientIdElement = jsonObject.get("clientId");
		if (clientIdElement != null && !clientIdElement.isJsonNull()) {
			checkJsonPrimitive(clientIdElement);

			JsonPrimitive clientIdPrimitive = clientIdElement.getAsJsonPrimitive();
			checkString(clientIdPrimitive);

			clientId = clientIdPrimitive.getAsString();
		}

		JsonElement topicElement = jsonObject.get("topic");
		checkNotNull(topicElement);
		checkJsonPrimitive(topicElement);

		JsonPrimitive topicPrimitive = topicElement.getAsJsonPrimitive();
		checkString(topicPrimitive);

		Authorization authorization = null;
		if (jsonObject.has("authorization")) {
			authorization = context.deserialize(jsonObject.get("authorization"),
				Authorization.class);
		}

		MqttConfiguration client = new MqttConfiguration();
		client.setServerUri(serverUriPrimitive.getAsString());
		client.setClientId(clientId);
		client.setTopic(topicPrimitive.getAsString());
		client.setAuthorization(authorization);

		return client;
	}

}
