package commons.connectivity;

import java.io.IOException;

import com.google.gson.Gson;

public abstract class AbstractClient {

	protected Gson jsonParser;

	public AbstractClient() {
		jsonParser = new Gson();
	}

	public abstract void connect(String destination)
	throws IOException;

	public abstract void disconnect();

	public abstract <T> void send(T payload, Class<T> clazz)
	throws IOException;

}
