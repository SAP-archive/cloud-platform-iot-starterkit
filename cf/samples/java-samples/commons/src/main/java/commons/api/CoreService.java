package commons.api;

import java.io.IOException;

import commons.connectivity.HttpClient;
import commons.model.Authentication;
import commons.model.Authentications;
import commons.model.Command;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayStatus;
import commons.model.GatewayType;
import commons.utils.Constants;
import commons.utils.ObjectFactory;

public class CoreService {

	private HttpClient httpClient;

	private String host;

	public CoreService(String host, String user, String password) {
		this.host = String.format("https://%1$s:443", host);
		httpClient = new HttpClient(user, password);
	}

	public Gateway getOnlineGateway(GatewayType type)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/gateways", host);

		Gateway[] gateways = null;
		try {
			httpClient.connect(destination);
			gateways = httpClient.doGetJson(Gateway[].class);
		}
		finally {
			httpClient.disconnect();
		}

		for (Gateway gateway : gateways) {
			GatewayType gatewayType = gateway.getType();
			GatewayStatus gatewayStatus = gateway.getStatus();
			if (type.equals(gatewayType) && GatewayStatus.ONLINE.equals(gatewayStatus)) {
				return gateway;
			}
		}

		throw new IllegalStateException(
			String.format("No online gateway of type '%1$s' found", type));
	}

	public Device getOnlineDevice(String id)
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

		if (!device.isOnline()) {
			throw new IllegalStateException(
				String.format("No online device with ID '%1$s' found", id));
		}

		return device;
	}

	public Device getOrAddDevice(String id, Gateway gateway)
	throws IOException {
		try {
			return getOnlineDevice(id);
		}
		catch (IOException | IllegalStateException e) {
			System.err.println(e.getMessage());
			System.err.println();

			return addDevice(gateway);
		}
	}

	public Device addDevice(Gateway gateway)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/devices", host);

		Device template = ObjectFactory.buildDevice();
		template.setGatewayId(gateway.getId());

		try {
			httpClient.connect(destination);
			Device device = httpClient.doPostJson(template, Device.class);

			System.out.println();
			System.out.printf("\t%-15s : %s %n", Constants.DEVICE_ID, device.getId());

			return device;
		}
		finally {
			httpClient.disconnect();
		}
	}

	public Authentication getAuthentication(Device device)
	throws IOException {
		String destination = String.format("%1$s/iot/core/api/v1/devices/%2$s/authentication/pem",
			host, device.getId());

		Authentications authentications = null;
		try {
			httpClient.connect(destination);
			authentications = httpClient.doGetJson(Authentications.class);
		}
		finally {
			httpClient.disconnect();
		}

		Authentication[] deviceAuthenticatons = authentications.getAuthentications();
		if (deviceAuthenticatons == null || deviceAuthenticatons.length == 0) {
			throw new IllegalStateException(String
				.format("No authentications for device with ID '%1$s' found", device.getId()));
		}

		return deviceAuthenticatons[0];
	}

	public void sendCommand(Device device, Command command)
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

}