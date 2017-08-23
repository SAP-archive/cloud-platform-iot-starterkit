package commons.model;

public class Sensor {

	private String id;

	private String deviceId;

	private String sensorTypeId;

	private String name;

	private String alternateId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSensorTypeId() {
		return sensorTypeId;
	}

	public void setSensorTypeId(String sensorTypeId) {
		this.sensorTypeId = sensorTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlternateId() {
		return alternateId;
	}

	public void setAlternateId(String alternateId) {
		this.alternateId = alternateId;
	}

}
