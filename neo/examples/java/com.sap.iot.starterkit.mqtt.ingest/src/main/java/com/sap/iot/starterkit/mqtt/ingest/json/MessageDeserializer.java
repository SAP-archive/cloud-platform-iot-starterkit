package com.sap.iot.starterkit.mqtt.ingest.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.sap.iot.starterkit.mqtt.ingest.type.Message;

public class MessageDeserializer
extends AbstractDeserializer<Message> {

	@Override
	public Message deserialize(JsonElement json, Type type, JsonDeserializationContext context)
	throws JsonParseException {

		checkNotNull(json);
		checkJsonObject(json);

		JsonObject jsonObject = json.getAsJsonObject();

		Map<String, Object> fields = new HashMap<String, Object>();
		for (Entry<String, JsonElement> nextEntry : jsonObject.entrySet()) {
			Object value = null;
			JsonElement jsonElement = nextEntry.getValue();
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
				if (primitive.isBoolean()) {
					value = primitive.getAsBoolean();
				}
				else if (primitive.isNumber()) {
					Number number = primitive.getAsNumber();
					if (number.toString().contains(".")) {
						value = number.doubleValue();
					}
					else {
						value = number.intValue();
					}
				}
				else {
					value = primitive.getAsString();
				}
			}
			else {
				value = jsonElement.getAsString();
			}

			fields.put(nextEntry.getKey(), value);
		}

		Message message = new Message();
		message.setFields(fields);

		return message;
	}

}