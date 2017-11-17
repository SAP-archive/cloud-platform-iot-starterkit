package commons.model;

public class Property {

	private String name;

	private PropertyType dataType;

	private String unitOfMeasure;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PropertyType getDataType() {
		return dataType;
	}

	public void setDataType(PropertyType dataType) {
		this.dataType = dataType;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Property other = (Property) obj;
		if (dataType != other.dataType) return false;
		if (name == null) {
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		return true;
	}

}
