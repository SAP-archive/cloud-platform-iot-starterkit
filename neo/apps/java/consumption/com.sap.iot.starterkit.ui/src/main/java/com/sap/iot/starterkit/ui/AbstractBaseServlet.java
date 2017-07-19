package com.sap.iot.starterkit.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * An abstraction over various HTTP servlets
 */
public abstract class AbstractBaseServlet
extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns a JNDI resource
	 * 
	 * @param name
	 *            the name of the JNDI resource like specified in the web.xml
	 * @return a JDNI resource or {@code null} if resource is not found in context
	 * @throws IOException
	 *             - if failed to look for a JNDI resource in the context or got an unexpected
	 *             resource type
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T getResource(String name)
	throws IOException {
		try {
			Context context = new InitialContext();
			return (T) context.lookup("java:comp/env/" + name);
		}
		catch (NamingException e) {
			throw new IOException("Unable to look for a JNDI resource [" + name + "]", e);
		}
		catch (ClassCastException e) {
			throw new IOException("Unexpected JNDI resource object type", e);
		}
	}

	/**
	 * Flushes a JSON string output to the client with the HTTP 200 code
	 * 
	 * @param response
	 *            an {@link HttpServletResponse} object that contains the response the servlet sends
	 *            to the client
	 * @param message
	 *            the String to be printed to a client
	 * @throws IOException
	 *             - if an input or output exception occurred
	 */
	protected void printJson(HttpServletResponse response, String message)
	throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		print(response, message);
	}

	/**
	 * Flushes a text output to the client with the HTTP 500 code
	 * 
	 * @param response
	 *            an {@link HttpServletResponse} object that contains the response the servlet sends
	 *            to the client
	 * @param message
	 *            the String to be printed to a client
	 * @throws IOException
	 *             - if an input or output exception occurred
	 */
	protected void printError(HttpServletResponse response, String message)
	throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		print(response, message);
	}

	/**
	 * Flushes an output to the client using UTF-8 encoding
	 * 
	 * @param response
	 *            an {@link HttpServletResponse} object that contains the response the servlet sends
	 *            to the client
	 * @param message
	 *            the String to be printed to a client
	 * @throws IOException
	 *             - if an input or output exception occurred
	 */
	protected void print(HttpServletResponse response, String message)
	throws IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(message);
		writer.flush();
		writer.close();
	}

}