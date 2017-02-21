package com.sap.iot.starterkit.mqtt.ingest.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sap.iot.starterkit.mqtt.ingest.type.Message;

/**
 * A custom Json deserializer for {@link Message} type
 */
public class MessageDeserializer
extends AbstractDeserializer<Message> {

	/**
	 * Gson invokes this call-back method during deserialization when it encounters a field of type
	 * {@link Message}
	 *
	 * @param json
	 *            The Json data being deserialized
	 * @param type
	 *            The type of the Object to deserialize to
	 * @return a deserialized object of type {@link Message}
	 * @throws JsonParseException
	 *             if json is not in the expected format
	 */
	@Override
	public Message deserialize(JsonElement json, Type type, JsonDeserializationContext context)
	throws JsonParseException {

		// precondition checks
		checkNotNull(json);
		checkJsonObject(json);

		JsonObject jsonObject = json.getAsJsonObject();

		Map<String, Object> fields = new HashMap<String, Object>();

		// iterate over all properties
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			fields.put(entry.getKey(), entry.getValue().getAsString());
		}

		Message message = new Message();
		message.setFields(fields);

		return message;
	}

}
