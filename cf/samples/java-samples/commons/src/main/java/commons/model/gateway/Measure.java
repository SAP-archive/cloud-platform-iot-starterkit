package commons.model.gateway;

public class Measure {

	private String[] measureIds;

	private String[] values;

	private String logNodeAddr;

	public String[] getMeasureIds() {
		return measureIds;
	}

	public void setMeasureIds(String[] measureIds) {
		this.measureIds = measureIds;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public String getLogNodeAddr() {
		return logNodeAddr;
	}

	public void setLogNodeAddr(String logNodeAddr) {
		this.logNodeAddr = logNodeAddr;
	}

}
