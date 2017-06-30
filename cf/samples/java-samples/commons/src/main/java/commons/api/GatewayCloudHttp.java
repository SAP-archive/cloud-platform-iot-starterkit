package commons.api;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

import commons.connectivity.HttpClient;
import commons.model.Device;

public class GatewayCloudHttp
implements GatewayCloud {

	private HttpClient httpClient;

	private Device device;

	public GatewayCloudHttp(Device device, SSLSocketFactory sslSocketFactory) {
		this.device = device;
		httpClient = new HttpClient(sslSocketFactory);
	}

	@Override
	public void connect(String host)
	throws IOException {
		host = String.format("https://%1$s:443/iot/gateway/rest/measures/%2$s", host,
			device.getPhysicalAddress());

		httpClient.connect(host);
	}

	@Override
	public void disconnect() {
		httpClient.disconnect();
	}

	@Override
	public <T> void send(T payload, Class<T> clazz)
	throws IOException {
		httpClient.send(payload, clazz);
	}

}