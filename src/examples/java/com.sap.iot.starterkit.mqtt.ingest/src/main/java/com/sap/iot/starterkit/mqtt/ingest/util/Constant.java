package com.sap.iot.starterkit.mqtt.ingest.util;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class Constant {

	public static final int MAX_PAYLOAD_SIZE = 1 * 1024 * 1024;

	public static final String ENCODING = StandardCharsets.UTF_8.toString();

	public static final Locale LOCALE = Locale.ENGLISH;

}
