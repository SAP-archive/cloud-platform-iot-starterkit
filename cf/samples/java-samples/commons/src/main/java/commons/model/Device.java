package commons.model;

public class Device {

	private String id;

	private String gatewayId;

	private String name;

	private String physicalAddress;

	private boolean online;

	private Sensor[] sensors;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhysicalAddress() {
		return physicalAddress;
	}

	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public Sensor[] getSensors() {
		return sensors;
	}

	public void setSensors(Sensor[] sensors) {
		this.sensors = sensors;
	}

}
