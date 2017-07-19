package commons.model;

public class Gateway {

	private String id;

	private String name;

	private GatewayType type;

	private GatewayStatus status;

	private long creationTimestamp;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public GatewayType getType() {
		return type;
	}

	public GatewayStatus getStatus() {
		return status;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

}
