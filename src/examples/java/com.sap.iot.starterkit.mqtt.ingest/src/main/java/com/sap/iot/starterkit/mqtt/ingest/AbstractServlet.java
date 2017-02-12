package com.sap.iot.starterkit.mqtt.ingest;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstraction over HTTP servlet
 */
public abstract class AbstractServlet
extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServlet.class);

	private static final String APPLICATION_JSON = "application/json";

	private static final String TEXT_HTML = "text/html";

	private static final int MAX_PAYLOAD_SIZE = 1 * 1024 * 1024;

	public static final String ENCODING = StandardCharsets.UTF_8.toString();

	/**
	 * Performs request basic validation
	 */
	protected void doValidate(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		int contentLength = request.getContentLength();
		if (contentLength > MAX_PAYLOAD_SIZE) {
			String message = String.format(ResponseMessage.PAYLOAD_TOO_LARGE, MAX_PAYLOAD_SIZE);
			printText(response, HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, message);
			return;
		}

		ContentType contentType = null;
		try {
			contentType = new ContentType(request.getContentType());
		}
		catch (ParseException e) {
			String message = String.format(ResponseMessage.CONTENT_TYPE_UNSUPPORTED,
				request.getContentType());
			printText(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, message);
			return;
		}

		if (!APPLICATION_JSON.equalsIgnoreCase(contentType.getBaseType())) {
			String message = String.format(ResponseMessage.CONTENT_TYPE_NOT_ALLOWED,
				APPLICATION_JSON);
			printText(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, message);
			return;
		}

		if (request.getCharacterEncoding() == null) {
			String message = String.format(
				"No character encoding specified in the request. Use a default %1$s one", ENCODING);
			LOGGER.warn(message);
			request.setCharacterEncoding(ENCODING);
		}
	}

	/**
	 * Flushes a JSON string output to the client with the 200 HTTP code
	 * 
	 * @param response
	 *            an {@link HttpServletResponse} object that contains the response the servlet sends
	 *            to the client
	 * @param message
	 *            the JSON String to be printed to a client
	 * @throws IOException
	 *             - if an input or output exception occurred
	 */
	protected void printJson(HttpServletResponse response, String message)
	throws IOException {
		printJson(response, HttpServletResponse.SC_OK, message);
	}

	/**
	 * Flushes a JSON string output to the client with the given HTTP code
	 * 
	 * @param response
	 *            an {@link HttpServletResponse} object that contains the response the servlet sends
	 *            to the client
	 * @param status
	 *            HTTP code
	 * @param message
	 *            the JSON String to be printed to a client
	 * @throws IOException
	 *             - if an input or output exception occurred
	 */
	protected void printJson(HttpServletResponse response, int status, String message)
	throws IOException {
		response.setContentType(APPLICATION_JSON);
		print(response, status, message);
	}

	/**
	 * Flushes a text output to the client with the given HTTP code
	 * 
	 * @param response
	 *            an {@link HttpServletResponse} object that contains the response the servlet sends
	 *            to the client
	 * @param status
	 *            HTTP code
	 * @param message
	 *            the String to be printed to a client
	 * @throws IOException
	 *             - if an input or output exception occurred
	 */
	protected void printText(HttpServletResponse response, int status, String message)
	throws IOException {
		response.setContentType(TEXT_HTML);
		print(response, status, message);
	}

	/**
	 * Flushes an output to the client using UTF-8 encoding
	 * 
	 * @param response
	 *            an {@link HttpServletResponse} object that contains the response the servlet sends
	 *            to the client
	 * @param status
	 *            HTTP code
	 * @param message
	 *            the String to be printed to a client
	 * @throws IOException
	 *             - if an input or output exception occurred
	 */
	private void print(HttpServletResponse response, int status, String message)
	throws IOException {
		response.setStatus(status);
		response.setCharacterEncoding(ENCODING);
		try {
			PrintWriter writer = response.getWriter();
			writer.print(message);
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			LOGGER.error("Unable to flush the stream", e);
		}
	}

}