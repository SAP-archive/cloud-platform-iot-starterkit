package commons.model;

public class SensorType {

	private String id;

	private String name;

	private SensorTypeCapability[] capabilities;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SensorTypeCapability[] getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(SensorTypeCapability[] capabilities) {
		this.capabilities = capabilities;
	}

}
