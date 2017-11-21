package commons.model.gateway;

public class Measure {

	private String capabilityAlternateId;

	private String[][] measures;

	private String sensorAlternateId;

	public String getCapabilityAlternateId() {
		return capabilityAlternateId;
	}

	public void setCapabilityAlternateId(String capabilityAlternateId) {
		this.capabilityAlternateId = capabilityAlternateId;
	}

	public String[][] getMeasures() {
		return measures;
	}

	public void setMeasures(String[][] measures) {
		this.measures = measures;
	}

	public String getSensorAlternateId() {
		return sensorAlternateId;
	}

	public void setSensorAlternateId(String sensorAlternateId) {
		this.sensorAlternateId = sensorAlternateId;
	}

}
