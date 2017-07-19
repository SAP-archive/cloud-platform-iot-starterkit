package com.sap.iot.starterkit.mqtt.ingest.json;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sap.iot.starterkit.mqtt.ingest.type.Message;

public class MessageSerializer
implements JsonSerializer<Message> {

	@Override
	public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();

		Map<String, Object> fields = src.getFields();
		for (Entry<String, Object> nextEntry : fields.entrySet()) {
			Object value = nextEntry.getValue();
			if (value instanceof Number) {
				jsonObject.addProperty(nextEntry.getKey(), (Number) value);
			}
			else if (value instanceof Boolean) {
				jsonObject.addProperty(nextEntry.getKey(), (Boolean) value);
			}
			else {
				jsonObject.addProperty(nextEntry.getKey(), value.toString());
			}
		}

		return jsonObject;
	}

}