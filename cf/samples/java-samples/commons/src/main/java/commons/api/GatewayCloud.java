package commons.api;

import java.io.IOException;

/**
 * An abstraction over Gateway Cloud clients
 */
public interface GatewayCloud {

	public void connect(String host)
	throws IOException;

	public void disconnect();

	public <T> void send(T payload, Class<T> clazz)
	throws IOException;

}
