package commons.api;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import commons.connectivity.HttpClient;
import commons.model.Device;
import commons.model.gateway.Command;
import commons.model.gateway.Measure;
import commons.model.gateway.Response;
import commons.utils.Console;

public class GatewayCloudHttp
implements GatewayCloud {

	private HttpClient httpClient;

	private String upstreamServerUri;

	private String downstreamServerUri;

	private ScheduledExecutorService executor;

	public GatewayCloudHttp(Device device, SSLSocketFactory sslSocketFactory) {
		String deviceAlternateId = device.getAlternateId();

		upstreamServerUri = String.format("/iot/gateway/rest/measures/%1$s", deviceAlternateId);
		downstreamServerUri = String.format("/iot/gateway/rest/commands/%1$s", deviceAlternateId);

		httpClient = new HttpClient(sslSocketFactory);
	}

	@Override
	public void connect(String host)
	throws IOException {
		host = String.format("https://%1$s:443", host);

		upstreamServerUri = host.concat(upstreamServerUri);
		downstreamServerUri = host.concat(downstreamServerUri);
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
		if (!upstreamServerUri.equalsIgnoreCase(httpClient.getServerUri())) {
			httpClient.connect(upstreamServerUri);
		}

		httpClient.doPost(measure, Measure.class, Response[].class);
	}

	@Override
	public void listenCommands()
	throws IOException {
		if (!downstreamServerUri.equalsIgnoreCase(httpClient.getServerUri())) {
			httpClient.connect(downstreamServerUri);
		}

		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					httpClient.doGet(Command[].class);
				} catch (IOException e) {
					Console.printError(e.getMessage());
				} finally {
					Console.printSeparator();
				}
			}

		}, 0, 5000, TimeUnit.MILLISECONDS);
	}

}