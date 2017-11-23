package commons.api;

import java.io.IOException;

import commons.model.gateway.Measure;

/**
 * An abstraction over Cloud Gateways.
 */
public interface GatewayCloud {

	public void connect(String host)
	throws IOException;

	public void disconnect();

	public void sendMeasure(Measure measure)
	throws IOException;

	public void listenCommands()
	throws IOException;

}
