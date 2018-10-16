package commons.model;

public interface Measure<T> {

	public String getSensorAlternateId();
	public String getSensorTypeAlternateId();
	public String getCapabilityAlternateId();
	public String getTimestamp();
	public T getMeasures();
	
	public void setSensorAlternateId(String sensorAlternateId);
	public void setSensorTypeAlternateId(String sensorTypeAlternateId);
	public void setCapabilityAlternateId(String capabilityAlternateId);
	public void setTimestamp(String timestamp);
	public void setMeasures(T measures);
	
}
