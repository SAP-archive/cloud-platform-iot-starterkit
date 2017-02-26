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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Message other = (Message) obj;
		if (fields == null) {
			if (other.fields != null) return false;
		}
		else if (!fields.equals(other.fields)) return false;
		return true;
	}

}
