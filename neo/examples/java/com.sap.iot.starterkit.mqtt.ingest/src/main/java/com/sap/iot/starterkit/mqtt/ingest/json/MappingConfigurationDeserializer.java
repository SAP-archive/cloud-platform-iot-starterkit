package com.sap.iot.starterkit.mqtt.ingest.json;

import java.lang.reflect.Type;
import java.util.Arrays;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.sap.iot.starterkit.mqtt.ingest.type.MappingConfiguration;
import com.sap.iot.starterkit.mqtt.ingest.type.Reference;

/**
 * A custom JSON deserializer for {@link MappingConfiguration} type
 */
public class MappingConfigurationDeserializer
extends AbstractDeserializer<MappingConfiguration> {

	/**
	 * GSON invokes this call-back method during deserialization when it encounters a field of type
	 * {@link MappingConfiguration}
	 *
	 * @param json
	 *            the JSON data being deserialized
	 * @param type
	 *            The type of the Object to deserialize to
	 * @return a deserialized object of type {@link MappingConfiguration}
	 * @throws JsonParseException
	 *             if JSON is not in the expected format
	 */
	@Override
	public MappingConfiguration deserialize(JsonElement json, Type type,
		JsonDeserializationContext context)
	throws JsonParseException {

		MappingConfiguration configuration = new MappingConfiguration();

		checkNotNull(json);
		checkJsonObject(json);

		JsonObject jsonObject = json.getAsJsonObject();

		JsonElement typeElement = jsonObject.get("type");
		checkNotNull(typeElement);
		checkJsonPrimitive(typeElement);

		JsonPrimitive typePrimitive = typeElement.getAsJsonPrimitive();
		checkString(typePrimitive);

		try {
			configuration.setType(com.sap.iot.starterkit.mqtt.ingest.type.MappingConfiguration.Type
				.fromValue(typePrimitive.getAsString()));
		}
		catch (IllegalArgumentException e) {
			throw new JsonParseException(e);
		}

		if (jsonObject.has("references")) {
			Reference[] references = context.deserialize(jsonObject.get("references"),
				Reference[].class);
			configuration.setReferences(Arrays.asList(references));
		}

		return configuration;
	}

}
