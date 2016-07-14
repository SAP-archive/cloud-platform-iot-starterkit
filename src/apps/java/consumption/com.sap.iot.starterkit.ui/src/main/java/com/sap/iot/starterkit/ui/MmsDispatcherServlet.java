package com.sap.iot.starterkit.ui;

import java.io.IOException;

import com.sap.core.connectivity.api.configuration.DestinationConfiguration;
import com.sap.iot.starterkit.ui.util.StringUtil;

/**
 * A dispatcher for the IoT Message Management Service (MMS)
 */
public class MmsDispatcherServlet
extends AbstractDispatcherServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see {@link AbstractDispatcherServlet#getDestinationUrl()}
	 */
	@Override
	protected String getDestinationUrl()
	throws IOException {
		DestinationConfiguration destination = getDestinationConfiguration();

		String url = destination.getProperty("URL");
		// normalization
		url = StringUtil.disjoin(url, "/");
		// backward compatibility for old destination formats
		url = StringUtil.disjoin(url, "/http/push");
		url = StringUtil.disjoin(url, "/v1/api");

		return url;
	}

	/**
	 * @see {@link AbstractDispatcherServlet#getDestinationName()}
	 */
	@Override
	protected String getDestinationName() {
		return "iotmms";
	}

}