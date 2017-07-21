package commons.model;

import java.util.Map;

public class Command {

	private String capabilityId;

	private String sensorId;

	private Map<String, Object> command;

	public String getCapabilityId() {
		return capabilityId;
	}

	public void setCapabilityId(String capabilityId) {
		this.capabilityId = capabilityId;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public Map<String, Object> getCommand() {
		return command;
	}

	public void setCommand(Map<String, Object> command) {
		this.command = command;
	}

}
