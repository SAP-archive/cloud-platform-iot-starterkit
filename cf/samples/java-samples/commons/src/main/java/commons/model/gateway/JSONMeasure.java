package commons.model.gateway;

import com.google.gson.JsonArray;

public class JSONMeasure implements commons.model.Measure<JsonArray> {

private String sensorAlternateId;
	
	private String sensorTypeAlternateId;
	
	private String capabilityAlternateId;
	
	private String timestamp;
	
	private JsonArray measures;	

	public String getSensorAlternateId() {
		return sensorAlternateId;
	}

	public void setSensorAlternateId(String sensorAlternateId) {
		this.sensorAlternateId = sensorAlternateId;
	}

	public String getSensorTypeAlternateId() {
		return sensorTypeAlternateId;
	}
	
	public void setSensorTypeAlternateId(String sensorTypeAlternateId) {
		this.sensorAlternateId = sensorTypeAlternateId;
	}

	public String getCapabilityAlternateId() {
		return capabilityAlternateId;
	}

	public void setCapabilityAlternateId(String capabilityAlternateId) {
		this.capabilityAlternateId = capabilityAlternateId;
	}

	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	public JsonArray getMeasures() {
		return measures;
	}

	public void setMeasures(JsonArray measures) {
		this.measures = measures;	
	}

}
