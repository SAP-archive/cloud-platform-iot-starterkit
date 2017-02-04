package com.sap.iot.starterkit.mqtt.ingest.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sap.iot.starterkit.mqtt.ingest.type.Client;
import com.sap.iot.starterkit.mqtt.ingest.type.Configuration;

/**
 * A custom JSON deserializer for {@link Configuration} type
 */
public class ConfigurationDeserializer
implements JsonDeserializer<Configuration> {

	/**
	 * GSON invokes this call-back method during deserialization when it encounters a field of type
	 * {@link Configuration}
	 *
	 * @param json
	 *            the JSON data being deserialized
	 * @param type
	 *            The type of the Object to deserialize to
	 * @return a deserialized object of type {@link Configuration}
	 * @throws JsonParseException
	 *             if JSON is not in the expected format
	 */
	@Override
	public Configuration deserialize(JsonElement json, Type type,
		JsonDeserializationContext context)
	throws JsonParseException {
		checkNotNull(json);
		checkJsonObject(json);

		JsonObject jsonObject = json.getAsJsonObject();

		JsonElement publisherElement = jsonObject.get("publisher");
		checkNotNull(publisherElement);
		checkJsonObject(publisherElement);
		Client publisher = context.deserialize(publisherElement, Client.class);

		JsonElement subscriberElement = jsonObject.get("subscriber");
		checkNotNull(subscriberElement);
		checkJsonObject(subscriberElement);
		Client subscriber = context.deserialize(subscriberElement, Client.class);

		Configuration configuration = new Configuration();
		configuration.setPublisher(publisher);
		configuration.setSubscriber(subscriber);

		return configuration;
	}

	private void checkNotNull(JsonElement jsonElement)
	throws JsonParseException {
		if (jsonElement == null || jsonElement.isJsonNull()) {
			throw new JsonParseException("JSON element was null");
		}
	}

	private void checkJsonObject(JsonElement jsonElement)
	throws JsonParseException {
		if (!jsonElement.isJsonObject()) {
			throw new JsonParseException("JSON object is expected");
		}
	}

}
