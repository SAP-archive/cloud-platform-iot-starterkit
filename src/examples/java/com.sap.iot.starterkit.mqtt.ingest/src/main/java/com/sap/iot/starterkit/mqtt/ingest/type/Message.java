package com.sap.iot.starterkit.mqtt.ingest.type;

import java.util.Map;

public class Message {

	private Map<String, Object> fields;

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

}
