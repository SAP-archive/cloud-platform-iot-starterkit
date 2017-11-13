package commons.model;

public class Gateway {

	private String id;

	private String name;

	private GatewayProtocol protocolId;

	private GatewayStatus status;

	private long creationTimestamp;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public GatewayProtocol getProtocolId() {
		return protocolId;
	}

	public GatewayStatus getStatus() {
		return status;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	@Override
	public String toString() {
		return "Gateway [id=" + id + ", name=" + name + ", protocolId=" + protocolId + ", status=" +
			status + ", creationTimestamp=" + creationTimestamp + "]";
	}

}
