package com.sap.iot.starterkit.mqtt.ingest.type;

import com.sap.iot.starterkit.mqtt.ingest.type.MappingConfiguration.Type;

public class Reference {

	private Type type;

	private String name;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
