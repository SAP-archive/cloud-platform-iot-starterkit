package commons.model;

import com.google.gson.annotations.SerializedName;

public enum GatewayProtocol {

	@SerializedName("mqtt") MQTT("mqtt"),

	@SerializedName("rest") REST("rest");

	private String value;

	private GatewayProtocol(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

	public static GatewayProtocol getDefault() {
		return REST;
	}

	public static GatewayProtocol fromValue(String value) {
		for (GatewayProtocol next : GatewayProtocol.values()) {
			if (next.getValue().equalsIgnoreCase(value)) {
				return next;
			}
		}
		return getDefault();
	}

}
