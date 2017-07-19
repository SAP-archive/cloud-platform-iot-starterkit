package commons.model;

import com.google.gson.annotations.SerializedName;

public enum GatewayType {

	@SerializedName("mqtt") MQTT("mqtt"),

	@SerializedName("rest") REST("rest");

	private String value;

	private GatewayType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

	public static GatewayType getDefault() {
		return REST;
	}

	public static GatewayType fromValue(String value) {
		for (GatewayType next : GatewayType.values()) {
			if (next.getValue().equalsIgnoreCase(value)) {
				return next;
			}
		}
		return getDefault();
	}

}
