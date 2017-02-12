package com.sap.iot.starterkit.mqtt.ingest.type;

public class Configuration {

	private MqttConfiguration subscriber;

	private MqttConfiguration publisher;

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

}
