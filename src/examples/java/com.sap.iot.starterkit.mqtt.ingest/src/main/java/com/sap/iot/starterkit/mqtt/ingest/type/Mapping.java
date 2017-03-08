package com.sap.iot.starterkit.mqtt.ingest.type;

public class Mapping {

	private String messageTypeId;

	private MappingConfiguration input;

	private MappingConfiguration output;

	public String getMessageTypeId() {
		return messageTypeId;
	}

	public void setMessageTypeId(String messageTypeId) {
		this.messageTypeId = messageTypeId;
	}

	public MappingConfiguration getInput() {
		return input;
	}

	public void setInput(MappingConfiguration input) {
		this.input = input;
	}

	public MappingConfiguration getOutput() {
		return output;
	}

	public void setOutput(MappingConfiguration output) {
		this.output = output;
	}

}
