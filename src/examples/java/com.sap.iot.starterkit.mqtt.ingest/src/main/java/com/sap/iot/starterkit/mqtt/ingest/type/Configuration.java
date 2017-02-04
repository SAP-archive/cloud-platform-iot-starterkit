package com.sap.iot.starterkit.mqtt.ingest.type;

public class Configuration {

	private Client subscriber;

	private Client publisher;

	public Client getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Client subscriber) {
		this.subscriber = subscriber;
	}

	public Client getPublisher() {
		return publisher;
	}

	public void setPublisher(Client publisher) {
		this.publisher = publisher;
	}

}
