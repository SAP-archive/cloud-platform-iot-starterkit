package commons.model;

import com.google.gson.annotations.SerializedName;

public enum PropertyType {

	@SerializedName("integer") INTEGER,

	@SerializedName("long") LONG,

	@SerializedName("float") FLOAT,

	@SerializedName("double") DOUBLE,

	@SerializedName("boolean") BOOLEAN,

	@SerializedName("date") DATE,

	@SerializedName("binary") BINARY,

	@SerializedName("string") STRING;

}
