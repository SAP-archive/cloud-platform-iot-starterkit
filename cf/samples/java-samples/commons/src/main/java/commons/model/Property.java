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

}
