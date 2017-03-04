package com.sap.iot.starterkit.mqtt.ingest.json;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

/**
 * An abstraction over all JSON deserializers
 * 
 * @param <T>
 *            type for which the deserializer is being registered
 */
public abstract class AbstractDeserializer<T>
implements JsonDeserializer<T> {

	protected void checkNotNull(JsonElement jsonElement)
	throws JsonParseException {
		if (jsonElement == null || jsonElement.isJsonNull()) {
			throw new JsonParseException("JSON element was null");
		}
	}

	protected void checkJsonObject(JsonElement jsonElement)
	throws JsonParseException {
		if (!jsonElement.isJsonObject()) {
			throw new JsonParseException("JSON object is expected");
		}
	}

	protected void checkJsonPrimitive(JsonElement jsonElement)
	throws JsonParseException {
		if (!jsonElement.isJsonPrimitive()) {
			throw new JsonParseException("JSON primitive is expected");
		}
	}

	protected void checkString(JsonPrimitive jsonPrimitive)
	throws JsonParseException {
		if (!jsonPrimitive.isString()) {
			throw new JsonParseException("String is expected");
		}
	}

}
