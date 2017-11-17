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

	private String host;

	public CoreService(String host, String user, String password) {
		this.host = String.format("https://%1$s:443", host);
		httpClient = new HttpClient(user, password);
	}

	public Gateway getOnlineCloudGateway(GatewayProtocol protocolId)
	throws IOException {
		String destination = String.format(
			"%1$s/iot/core/api/v1/gateways?filter=protocolId eq '%2$s' and status eq 'online'",
			host, protocolId);

		Gateway[] gateways = null;
		try {
			httpClient.connect(destination);
			gateways = httpClient.doGetJson(Gateway[].class);
		}
		finally {
			httpClient.disconnect();
		}

		if (gateways == null || gateways.length == 0) {
			throw new IllegalStateException(
				String.format("No online Gateways with protocol ID '%1$s' found", protocolId));
		}

		if (gateways.length > 1) {
			throw new IllegalStateException(String
				.format("Multiple online Gateways with protocol ID '%1$s' found", protocolId));
		}

		Gateway gateway = gateways[0];
		if (!protocolId.equals(gateway.getProtocolId()) ||
			!GatewayStatus.ONLINE.equals(gateway.getStatus())) {
			throw new IllegalStateException(
				String.format("Unexpected Gateway returned '%1$s'", gateway));
		}

		return gateway;
	}

	public Device getOnlineDevice(String id, Gateway gateway)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/devices/%2$s", host, id);

		Device device = null;
		try {
			httpClient.connect(destination);
			device = httpClient.doGetJson(Device.class);
		}
		finally {
			httpClient.disconnect();
		}

		if (!device.isOnline() || !device.getGatewayId().equals(gateway.getId())) {
			throw new IllegalStateException(
				String.format("No online Device with ID '%1$s' found in the '%2$s' Gateway", id,
					gateway.getProtocolId().getValue()));
		}

		return device;
	}

	public Device addDevice(Device device)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/devices", host);

		try {
			httpClient.connect(destination);
			return httpClient.doPostJson(device, Device.class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public Sensor addSensor(Sensor sensor)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/sensors", host);

		try {
			httpClient.connect(destination);
			return httpClient.doPostJson(sensor, Sensor.class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public Authentication getAuthentication(Device device)
	throws IOException {
		String destination = String.format(
			"%1$s/iot/core/api/v1/devices/%2$s/authentications/clientCertificate/pem", host,
			device.getId());

		try {
			httpClient.connect(destination);
			return httpClient.doGetJson(Authentication.class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public Measure[] getLatestMeasures(Device device, Capability capability, int top)
	throws IOException {
		String destination = String.format(
			"%1$s/iot/core/api/v1/devices/%2$s/measures?orderby=timestamp desc&filter=capabilityId eq '%3$s'&top=%4$d",
			host, device.getId(), capability.getId(), top);

		try {
			httpClient.connect(destination);
			return httpClient.doGetJson(Measure[].class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public void sendCommand(Command command, Device device)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/devices/%2$s/commands", host,
			device.getId());

		try {
			httpClient.connect(destination);
			httpClient.doPost(command, Command.class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public Capability[] getCapabilities()
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/capabilities", host);

		try {
			httpClient.connect(destination);
			return httpClient.doGetJson(Capability[].class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public Capability getCapability(String id)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/capabilities/%2$s", host, id);

		try {
			httpClient.connect(destination);
			return httpClient.doGetJson(Capability.class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public Capability addCapability(Capability capability)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/capabilities", host);

		try {
			httpClient.connect(destination);
			return httpClient.doPostJson(capability, Capability.class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public SensorType[] getSensorTypes()
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/sensorTypes", host);

		try {
			httpClient.connect(destination);
			return httpClient.doGetJson(SensorType[].class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public SensorType getSensorType(String id)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/sensorTypes/%2$s", host, id);

		try {
			httpClient.connect(destination);
			return httpClient.doGetJson(SensorType.class);
		}
		finally {
			httpClient.disconnect();
		}
	}

	public SensorType addSensorType(SensorType sensorType)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/sensorTypes", host);

		try {
			httpClient.connect(destination);
			return httpClient.doPostJson(sensorType, SensorType.class);
		}
		finally {
			httpClient.disconnect();
		}
	}

}