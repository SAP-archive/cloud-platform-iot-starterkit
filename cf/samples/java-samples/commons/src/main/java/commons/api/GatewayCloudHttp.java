package commons.api;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import commons.connectivity.HttpClient;
import commons.model.Device;
import commons.model.gateway.Measure;
import commons.utils.Console;

public class GatewayCloudHttp
implements GatewayCloud {

	private HttpClient httpClient;

	private String upstreamEndpoint;

	private String downstreamEndpoint;

	private ScheduledExecutorService executor;

	public GatewayCloudHttp(Device device, SSLSocketFactory sslSocketFactory) {
		String deviceAlternateId = device.getAlternateId();

		upstreamEndpoint = String.format("/iot/gateway/rest/measures/%1$s", deviceAlternateId);
		downstreamEndpoint = String.format("/iot/gateway/rest/commands/%1$s", deviceAlternateId);

		httpClient = new HttpClient(sslSocketFactory);
	}

	@Override
	public void connect(String host)
	throws IOException {
		host = String.format("https://%1$s:443", host);

		upstreamEndpoint = host.concat(upstreamEndpoint);
		downstreamEndpoint = host.concat(downstreamEndpoint);
	}

	@Override
	public void disconnect() {
		if (executor != null) {
			executor.shutdown();
		}
		httpClient.disconnect();
	}

	@Override
	public void sendMeasure(Measure measure)
	throws IOException {
		if (!upstreamEndpoint.equalsIgnoreCase(httpClient.getDestination())) {
			httpClient.connect(upstreamEndpoint);
		}

		httpClient.doPostJson(measure, Measure.class);
	}

	@Override
	public void listenCommands()
	throws IOException {
		if (!downstreamEndpoint.equalsIgnoreCase(httpClient.getDestination())) {
			httpClient.connect(downstreamEndpoint);
		}

		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					httpClient.doGetString();
				}
				catch (IOException e) {
					Console.printError(e.getMessage());
				}
				finally {
					Console.printSeparator();
				}
			}

		}, 0, 2000, TimeUnit.MILLISECONDS);
	}

}