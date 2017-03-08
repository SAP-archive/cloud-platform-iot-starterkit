package com.sap.iot.starterkit.mqtt.ingest.util;

public class ResponseMessage {

	public static final String PAYLOAD_TOO_LARGE = "Request payload too large. Expect less or equal to %1$d bytes.";

	public static final String PAYLOAD_UNEXPECTED = "Unexpected request payload format";

	public static final String CONTENT_TYPE_UNSUPPORTED = "Unsupported content type %1$s";

	public static final String CONTENT_TYPE_NOT_ALLOWED = "Not allowed content type. Expect %1$s";

	public static final String INTERNAL_ERROR = "Internal error. Please, check application logs for details.";

	public static final String CONFIGURATION_MISSING = "No configuration found";

	public static final String CONFIGURATION_REMOVED = "Configuration was removed. MQTT clients disconnected.";

}
