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
import com.google.gson.JsonParseException;
import com.sap.iot.starterkit.mqtt.ingest.connect.MqttClient;
import com.sap.iot.starterkit.mqtt.ingest.connect.MqttClientFactory;
import com.sap.iot.starterkit.mqtt.ingest.json.GsonFactory;
import com.sap.iot.starterkit.mqtt.ingest.type.Configuration;
import com.sap.iot.starterkit.mqtt.ingest.type.MessageEnvelope;

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
		gson = GsonFactory.buildGson();
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
		LOGGER.info("Configuration was removed");

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
			configuration = gson.fromJson(request.getReader(), Configuration.class);
		}
		catch (JsonParseException e) {
			LOGGER.error("Unable to parse configuration", e);

			printText(response, HttpServletResponse.SC_BAD_REQUEST,
				ResponseMessage.PAYLOAD_UNEXPECTED);
		}

		if (configuration == null) {
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
		try {
			subscriber = MqttClientFactory.buildMqttClient(configuration.getSubscriber());
			publisher = MqttClientFactory.buildMqttClient(configuration.getPublisher());
		}
		catch (IllegalStateException e) {
			throw new IOException("Unable to instantiate the MQTT client", e);
		}

		subscriber.connect(configuration.getSubscriber().getServerUri());
		publisher.connect(configuration.getPublisher().getServerUri());
	}

	private void subscribe()
	throws IOException {
		if (subscriber == null) {
			LOGGER.warn("MQTT subscriber instance is null");
			return;
		}

		final String subscribeTopic = configuration.getSubscriber().getTopic();
		final String publishTopic = configuration.getPublisher().getTopic();

		subscriber.subscribe(subscribeTopic, new IMqttMessageListener() {

			@Override
			public void messageArrived(String topic, MqttMessage message)
			throws Exception {
				LOGGER.info(
					String.format("MQTT message arrived for topic '%1$s': %2$s", topic, message));

				if (publisher == null) {
					LOGGER.warn("MQTT publisher instance is null");
					return;
				}

				MessageEnvelope messageEnvelope = MessageEnvelope.fromMqttMessage(message);
				publisher.publish(publishTopic,
					gson.toJson(messageEnvelope, MessageEnvelope.class));
			}

		});

	}

}