package com.sap.iot.starterkit.mqtt.ingest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sap.iot.starterkit.mqtt.ingest.type.Client;
import com.sap.iot.starterkit.mqtt.ingest.type.Configuration;
import com.sap.iot.starterkit.mqtt.ingest.type.MessageEnvelope;
import com.sap.iot.starterkit.mqtt.ingest.util.ConfigurationDeserializer;
import com.sap.iot.starterkit.mqtt.ingest.util.ResponseMessage;

public class IngestServlet
extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(IngestServlet.class);

	private Configuration configuration;

	private Gson gson;

	private MqttClient subscriber;

	private MqttClient publisher;

	@Override
	public void init()
	throws ServletException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Configuration.class, new ConfigurationDeserializer());
		gson = builder.create();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		if (configuration == null) {
			printText(response, HttpServletResponse.SC_NOT_FOUND,
				ResponseMessage.CONFIGURATION_MISSING);
		}
		else {
			printJson(response, gson.toJson(configuration, Configuration.class));
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doModify(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doModify(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		configuration = null;
		LOGGER.info("Configuration has been deleted");

		disconnect();
		printText(response, HttpServletResponse.SC_OK, ResponseMessage.CONFIGURATION_REMOVED);
	}

	protected void doModify(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		super.doValidate(request, response);
		if (response.isCommitted()) {
			return;
		}

		doParse(request, response);
		if (response.isCommitted()) {
			return;
		}

		doReconnect(request, response);
		if (response.isCommitted()) {
			return;
		}

		printJson(response, gson.toJson(configuration, Configuration.class));
	}

	private void doParse(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		try {
			this.configuration = gson.fromJson(request.getReader(), Configuration.class);
		}
		catch (JsonParseException e) {
			LOGGER.error("Unable to parse configuration", e);

			printText(response, HttpServletResponse.SC_BAD_REQUEST,
				ResponseMessage.PAYLOAD_UNEXPECTED);
		}
	}

	private void doReconnect(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		disconnect();
		try {
			connect();
			subscribe();
		}
		catch (IOException e) {
			LOGGER.error("Unable to reconnect the clients", e);
			disconnect();

			printText(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				ResponseMessage.INTERNAL_ERROR);
		}
	}

	private void disconnect() {
		if (subscriber != null) {
			subscriber.disconnect();
		}
		if (publisher != null) {
			publisher.disconnect();
		}
	}

	private void connect()
	throws IOException {
		Client publisherConf = configuration.getPublisher();
		Client subscriberConf = configuration.getSubscriber();

		publisher = new MqttClient(publisherConf.getServerUri(), publisherConf.getClientId());
		publisher.connect(publisherConf.getUsername(), publisherConf.getPassword());

		subscriber = new MqttClient(subscriberConf.getServerUri(), subscriberConf.getClientId());
		subscriber.connect(subscriberConf.getUsername(), subscriberConf.getPassword());
	}

	private void subscribe()
	throws IOException {
		if (subscriber == null) {
			LOGGER.warn("MQTT subscriber instance is null");
			return;
		}

		Client subscriberConf = configuration.getSubscriber();
		subscriber.subscribe(subscriberConf.getTopic(), new IMqttMessageListener() {

			@Override
			public void messageArrived(String topic, MqttMessage message)
			throws Exception {
				LOGGER.info(
					String.format("MQTT message arrived for topic '%1$s': %2$s", topic, message));

				if (publisher == null) {
					LOGGER.warn("MQTT publisher instance is null");
					return;
				}

				Client publisherConf = configuration.getPublisher();
				MessageEnvelope envelope = MessageEnvelope.fromMqttMessage(message);
				publisher.publish(publisherConf.getTopic(),
					gson.toJson(envelope, MessageEnvelope.class));
			}

		});
	}

}