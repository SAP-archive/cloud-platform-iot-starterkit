package commons.model;

public class SensorTypeCapability {

	private String id;

	private CapabilityType type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CapabilityType getType() {
		return type;
	}

	public void setType(CapabilityType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SensorTypeCapability other = (SensorTypeCapability) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		if (type != other.type) return false;
		return true;
	}

}
