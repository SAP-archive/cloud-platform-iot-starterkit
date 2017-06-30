package com.sap.iot.starterkit.mqtt.ingest.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.sap.iot.starterkit.mqtt.ingest.type.Authorization;
import com.sap.iot.starterkit.mqtt.ingest.type.BasicAuthorization;

/**
 * A custom JSON deserializer for {@link Authorization} type
 */
public class AuthorizationDeserializer
extends AbstractDeserializer<Authorization> {

	/**
	 * GSON invokes this call-back method during deserialization when it encounters a field of type
	 * {@link Authorization}
	 *
	 * @param json
	 *            the JSON data being deserialized
	 * @param type
	 *            The type of the Object to deserialize to
	 * @return a deserialized object of type {@link Authorization}
	 * @throws JsonParseException
	 *             if JSON is not in the expected format
	 */
	@Override
	public Authorization deserialize(JsonElement json, Type type,
		JsonDeserializationContext context)
	throws JsonParseException {

		Authorization authorization = null;

		if (json == null || json.isJsonNull()) {
			return authorization;
		}

		checkJsonObject(json);

		JsonObject jsonObject = json.getAsJsonObject();

		if (jsonObject.has("username") && jsonObject.has("password")) {
			JsonElement usernameElement = jsonObject.get("username");
			checkNotNull(usernameElement);
			checkJsonPrimitive(usernameElement);

			JsonPrimitive usernamePrimitive = usernameElement.getAsJsonPrimitive();
			checkString(usernamePrimitive);

			JsonElement passwordElement = jsonObject.get("password");
			checkNotNull(passwordElement);
			checkJsonPrimitive(passwordElement);

			JsonPrimitive passwordPrimitive = passwordElement.getAsJsonPrimitive();
			checkString(passwordPrimitive);

			authorization = new BasicAuthorization();
			((BasicAuthorization) authorization).setUsername(usernamePrimitive.getAsString());
			((BasicAuthorization) authorization).setPassword(passwordPrimitive.getAsString());

			return authorization;
		}

		// TODO: handle certificates

		return authorization;
	}

}
