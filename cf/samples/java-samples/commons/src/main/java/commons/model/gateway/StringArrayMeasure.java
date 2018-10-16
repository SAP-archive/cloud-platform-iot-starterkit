package commons.model.gateway;

public class StringArrayMeasure implements commons.model.Measure<Object[][]>{

	private String sensorAlternateId;
	
	private String sensorTypeAlternateId;
	
	private String capabilityAlternateId;
	
	private String timestamp;
	
	private Object[][] measures;	

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


	public Object[][] getMeasures() {
		return measures;
	}

	public void setMeasures(Object[][] measures) {
		this.measures = measures;	
	}

}
