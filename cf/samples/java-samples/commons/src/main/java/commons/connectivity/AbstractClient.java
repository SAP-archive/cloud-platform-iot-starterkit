package commons.connectivity;

import java.io.IOException;

import com.google.gson.Gson;

/**
 * An abstraction over connectivity clients.
 */
public abstract class AbstractClient {

	protected Gson jsonParser;

	public AbstractClient() {
		jsonParser = new Gson();
	}

	public abstract void connect(String serverUri)
	throws IOException;

	public abstract void disconnect();

}
