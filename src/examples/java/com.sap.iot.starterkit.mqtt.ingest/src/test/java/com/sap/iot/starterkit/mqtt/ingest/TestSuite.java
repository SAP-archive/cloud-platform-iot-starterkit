package com.sap.iot.starterkit.mqtt.ingest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Assert;

import com.google.gson.Gson;
import com.sap.iot.starterkit.mqtt.ingest.json.GsonFactory;

public class TestSuite {

	protected static Gson gson;

	static {
		gson = GsonFactory.buildGson();
	}

	protected String getResourceAsString(String fileName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(fileName);
		StringBuilder content = new StringBuilder();
		Reader reader = null;
		try {
			reader = new InputStreamReader(is, AbstractServlet.ENCODING);
			char[] buffer = new char[4096];
			int nextChar;
			while ((nextChar = reader.read(buffer, 0, buffer.length)) >= 0) {
				content.append(buffer, 0, nextChar);
			}
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				reader.close();
			}
			catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
		return content.toString();
	}

}
