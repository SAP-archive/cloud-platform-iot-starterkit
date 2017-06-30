package com.sap.iot.starterkit.mqtt.ingest.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.sap.iot.starterkit.mqtt.ingest.type.Reference;

/**
 * A custom JSON deserializer for {@link Reference} type
 */
public class ReferenceDeserializer
extends AbstractDeserializer<Reference> {

	/**
	 * GSON invokes this call-back method during deserialization when it encounters a field of type
	 * {@link Reference}
	 *
	 * @param json
	 *            the JSON data being deserialized
	 * @param type
	 *            The type of the Object to deserialize to
	 * @return a deserialized object of type {@link Reference}
	 * @throws JsonParseException
	 *             if JSON is not in the expected format
	 */
	@Override
	public Reference deserialize(JsonElement json, Type type, JsonDeserializationContext context)
	throws JsonParseException {

		Reference reference = new Reference();

		checkNotNull(json);
		checkJsonObject(json);

		JsonObject jsonObject = json.getAsJsonObject();

		JsonElement typeElement = jsonObject.get("type");
		checkNotNull(typeElement);
		checkJsonPrimitive(typeElement);

		JsonPrimitive typePrimitive = typeElement.getAsJsonPrimitive();
		checkString(typePrimitive);

		try {
			reference.setType(com.sap.iot.starterkit.mqtt.ingest.type.MappingConfiguration.Type
				.fromValue(typePrimitive.getAsString()));
		}
		catch (IllegalArgumentException e) {
			throw new JsonParseException(e);
		}

		JsonElement nameElement = jsonObject.get("name");
		checkNotNull(nameElement);
		checkJsonPrimitive(nameElement);

		JsonPrimitive namePrimitive = typeElement.getAsJsonPrimitive();
		checkString(namePrimitive);

		reference.setName(nameElement.getAsString());

		return reference;
	}

}
