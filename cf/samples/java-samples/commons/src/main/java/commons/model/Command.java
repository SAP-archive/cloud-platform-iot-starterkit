package commons.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Command {

	private String capabilityId;

	private String sensorId;

	@SerializedName("command")
	private Map<String, Object> properties;

	public String getCapabilityId() {
		return capabilityId;
	}

	public void setCapabilityId(String capabilityId) {
		this.capabilityId = capabilityId;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

}
