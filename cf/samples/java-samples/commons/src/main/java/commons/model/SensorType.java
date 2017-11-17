package commons.model;

import java.util.Arrays;

public class SensorType {

	private String id;

	private String name;

	private String alternateId;

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

	public String getAlternateId() {
		return alternateId;
	}

	public void setAlternateId(String alternateId) {
		this.alternateId = alternateId;
	}

	public SensorTypeCapability[] getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(SensorTypeCapability[] capabilities) {
		this.capabilities = capabilities;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(capabilities);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SensorType other = (SensorType) obj;
		if (!Arrays.equals(capabilities, other.capabilities)) return false;
		if (name == null) {
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		return true;
	}

}
