package commons.model;

import com.google.gson.annotations.SerializedName;

public enum GatewayStatus {

	@SerializedName("online") ONLINE,

	@SerializedName("offline") OFFLINE,

	@SerializedName("unknown") UNKNOWN;

}
