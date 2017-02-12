package com.sap.iot.starterkit.mqtt.ingest.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sap.iot.starterkit.mqtt.ingest.type.Configuration;
import com.sap.iot.starterkit.mqtt.ingest.type.MqttConfiguration;

/**
 * A custom JSON deserializer for {@link Configuration} type
 */
public class ConfigurationDeserializer
extends AbstractDeserializer<Configuration> {

	private static final String ATTRIBUTE_PUBLISHER = "publisher";

	private static final String ATTRIBUTE_SUBSCRIBER = "subscriber";

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

		JsonElement publisherElement = jsonObject.get(ATTRIBUTE_PUBLISHER);
		checkNotNull(publisherElement);
		checkJsonObject(publisherElement);

		MqttConfiguration publisher = context.deserialize(publisherElement,
			MqttConfiguration.class);

		JsonElement subscriberElement = jsonObject.get(ATTRIBUTE_SUBSCRIBER);
		checkNotNull(subscriberElement);
		checkJsonObject(subscriberElement);

		MqttConfiguration subscriber = context.deserialize(subscriberElement,
			MqttConfiguration.class);

		Configuration configuration = new Configuration();
		configuration.setPublisher(publisher);
		configuration.setSubscriber(subscriber);

		return configuration;
	}

}
