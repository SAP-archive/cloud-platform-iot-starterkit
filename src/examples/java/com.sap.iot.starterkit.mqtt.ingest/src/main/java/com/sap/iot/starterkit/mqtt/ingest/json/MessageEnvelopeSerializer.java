package com.sap.iot.starterkit.mqtt.ingest.json;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sap.iot.starterkit.mqtt.ingest.type.Message;
import com.sap.iot.starterkit.mqtt.ingest.type.MessageEnvelope;

public class MessageEnvelopeSerializer
implements JsonSerializer<MessageEnvelope> {

	@Override
	public JsonElement serialize(MessageEnvelope src, Type typeOfSrc,
		JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.addProperty("messageType", src.getMessageType());

		JsonArray jsonArray = new JsonArray();

		List<Message> messages = src.getMessages();
		for (Message nextMessage : messages) {
			Map<String, Object> fields = nextMessage.getFields();
			for (Entry<String, Object> nextEntry : fields.entrySet()) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.add(nextEntry.getKey(), context.serialize(nextEntry.getValue()));
				jsonArray.add(jsonObject);
			}

		}

		json.add("messages", jsonArray);

		return json;
	}

}
