package commons.api;

import java.io.IOException;

import commons.connectivity.HttpClient;
import commons.model.Authentication;
import commons.model.Capability;
import commons.model.Command;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayProtocol;
import commons.model.GatewayStatus;
import commons.model.Measure;
import commons.model.Sensor;
import commons.model.SensorType;

public class CoreService {

	private HttpClient httpClient;

	private String baseUri;

	public CoreService(String host, String user, String password) {
		baseUri = String.format("https://%1$s/iot/core/api/v1", host);
		httpClient = new HttpClient(user, password);
	}

	public void shutdown() {
		httpClient.disconnect();
	}

	public Gateway getOnlineCloudGateway(GatewayProtocol protocolId)
	throws IOException {
		String destination = String.format(
			"%1$s/gateways?filter=protocolId eq '%2$s' and status eq 'online' and type eq 'cloud'", baseUri,
			protocolId);

		Gateway[] gateways = null;
		try {
			httpClient.connect(destination);
			gateways = httpClient.doGet(Gateway[].class);
		} finally {
			httpClient.disconnect();
		}

		if (gateways == null || gateways.length == 0) {
			throw new IllegalStateException(
				String.format("No online Gateways with protocol ID '%1$s' found", protocolId));
		}

		if (gateways.length > 1) {
			throw new IllegalStateException(
				String.format("Multiple online Gateways with protocol ID '%1$s' found", protocolId));
		}

		Gateway gateway = gateways[0];
		if (!protocolId.equals(gateway.getProtocolId()) || !GatewayStatus.ONLINE.equals(gateway.getStatus())) {
			throw new IllegalStateException(String.format("Unexpected Gateway returned '%1$s'", gateway));
		}

		return gateway;
	}

	public Device getDevice(String id, Gateway gateway)
	throws IOException {
		String destination = String.format("%1$s/devices/%2$s", baseUri, id);

		Device device = null;
		try {
			httpClient.connect(destination);
			device = httpClient.doGet(Device.class);
		} finally {
			httpClient.disconnect();
		}

		if (!device.getGatewayId().equals(gateway.getId())) {
			throw new IllegalStateException(String.format("No Device with ID '%1$s' found in the '%2$s' Gateway", id,
				gateway.getProtocolId().getValue()));
		}

		return device;
	}

	public Device addDevice(Device device)
	throws IOException {
		String destination = String.format("%1$s/devices", baseUri);

		try {
			httpClient.connect(destination);
			return httpClient.doPost(device, Device.class);
		} finally {
			httpClient.disconnect();
		}
	}

	public Sensor addSensor(Sensor sensor)
	throws IOException {
		String destination = String.format("%1$s/sensors", baseUri);

		try {
			httpClient.connect(destination);
			return httpClient.doPost(sensor, Sensor.class);
		} finally {
			httpClient.disconnect();
		}
	}

	public Authentication getAuthentication(Device device)
	throws IOException {
		String destination = String.format("%1$s/devices/%2$s/authentications/clientCertificate/pem", baseUri,
			device.getId());

		try {
			httpClient.connect(destination);
			return httpClient.doGet(Authentication.class);
		} finally {
			httpClient.disconnect();
		}
	}

	public Measure[] getLatestMeasures(Device device, Capability capability, int top)
	throws IOException {
		String destination = String.format(
			"%1$s/devices/%2$s/measures?orderby=timestamp desc&filter=capabilityId eq '%3$s'&top=%4$d", baseUri,
			device.getId(), capability.getId(), top);

		try {
			httpClient.connect(destination);
			return httpClient.doGet(Measure[].class);
		} finally {
			httpClient.disconnect();
		}
	}

	public void sendCommand(Command command, Device device)
	throws IOException {
		String destination = String.format("%1$s/devices/%2$s/commands", baseUri, device.getId());

		try {
			httpClient.connect(destination);
			httpClient.doPost(command, Command.class);
		} finally {
			httpClient.disconnect();
		}
	}

	public Capability[] getCapabilities()
	throws IOException {
		String destination = String.format("%1$s/capabilities", baseUri);

		try {
			httpClient.connect(destination);
			return httpClient.doGet(Capability[].class);
		} finally {
			httpClient.disconnect();
		}
	}

	public Capability getCapability(String id)
	throws IOException {
		String destination = String.format("%1$s/capabilities/%2$s", baseUri, id);

		try {
			httpClient.connect(destination);
			return httpClient.doGet(Capability.class);
		} finally {
			httpClient.disconnect();
		}
	}

	public Capability addCapability(Capability capability)
	throws IOException {
		String destination = String.format("%1$s/capabilities", baseUri);

		try {
			httpClient.connect(destination);
			return httpClient.doPost(capability, Capability.class);
		} finally {
			httpClient.disconnect();
		}
	}

	public SensorType[] getSensorTypes()
	throws IOException {
		String destination = String.format("%1$s/sensorTypes", baseUri);

		try {
			httpClient.connect(destination);
			return httpClient.doGet(SensorType[].class);
		} finally {
			httpClient.disconnect();
		}
	}

	public SensorType getSensorType(String id)
	throws IOException {
		String destination = String.format("%1$s/sensorTypes/%2$s", baseUri, id);

		try {
			httpClient.connect(destination);
			return httpClient.doGet(SensorType.class);
		} finally {
			httpClient.disconnect();
		}
	}

	public SensorType addSensorType(SensorType sensorType)
	throws IOException {
		String destination = String.format("%1$s/sensorTypes", baseUri);

		try {
			httpClient.connect(destination);
			return httpClient.doPost(sensorType, SensorType.class);
		} finally {
			httpClient.disconnect();
		}
	}

}