package commons.api;

import java.io.IOException;

import commons.connectivity.HttpClient;
import commons.model.Capability;
import commons.model.Device;
import commons.model.Measure;

public class ProcessingService {

	private HttpClient httpClient;

	private String baseUri;

	public ProcessingService(String host, String tenant, String user, String password) {
		baseUri = String.format("https://%1$s/iot/processing/api/v1/tenant/%2$s", host, tenant);
		httpClient = new HttpClient(user, password);
	}

	public void shutdown() {
		httpClient.disconnect();
	}

	public Measure[] getLatestMeasures(Capability capability, Device device, int top)
	throws IOException {
		String destination = String.format(
			"%1$s/measures/capabilities/%2$s?orderby=timestamp desc&filter=deviceId eq '%3$s'&top=%4$d", baseUri,
			capability.getId(), device.getId(), top);

		try {
			httpClient.connect(destination);
			return httpClient.doGet(Measure[].class);
		} finally {
			httpClient.disconnect();
		}
	}

}