package com.sap.iot.starterkit.mqtt.ingest.type;

public class Configuration {

	private MqttConfiguration subscriber;

	private MqttConfiguration publisher;

	private Mapping mapping;

	public MqttConfiguration getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(MqttConfiguration subscriber) {
		this.subscriber = subscriber;
	}

	public MqttConfiguration getPublisher() {
		return publisher;
	}

	public void setPublisher(MqttConfiguration publisher) {
		this.publisher = publisher;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

}
