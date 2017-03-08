package com.sap.iot.starterkit.mqtt.ingest.type;

import java.util.List;

public class MappingConfiguration {

	public enum Type {

		JSON("json"),
		DOUBLE("double"),
		INTEGER("integer"),
		LONG("long"),
		FLOAT("float"),
		STRING("string"),
		BOOLEAN("boolean"),
		DATE("date");

		private String value;

		Type(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return getValue();
		}

		public static Type fromValue(String name) {
			for (Type next : Type.values()) {
				if (next.getValue().equalsIgnoreCase(name)) {
					return next;
				}
			}

			throw new IllegalArgumentException(String.format("Unsupported type name '%1$s'", name));
		}
	}

	private Type type;

	private List<Reference> references;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

}
