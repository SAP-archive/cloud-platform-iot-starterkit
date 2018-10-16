package commons.connectivity;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import commons.model.Measure;
import commons.model.gateway.JSONMeasure;
import commons.model.gateway.StringArrayMeasure;

/**
 * An abstraction over connectivity clients.
 */
public abstract class AbstractClient {

	protected Gson jsonParser;

	@SuppressWarnings("rawtypes")
	public AbstractClient() {
		RuntimeTypeAdapterFactory<Measure> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
			    .of(Measure.class)
			    .registerSubtype(StringArrayMeasure.class)
			    .registerSubtype(JSONMeasure.class);

		jsonParser = new GsonBuilder()
			    .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
			    .create();
	}

	public abstract void connect(String serverUri)
	throws IOException;

	public abstract void disconnect();

}
