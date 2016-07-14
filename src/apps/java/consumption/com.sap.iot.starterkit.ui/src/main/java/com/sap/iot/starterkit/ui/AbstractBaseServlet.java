package com.sap.iot.starterkit.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractBaseServlet
extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	protected static <T> T getResource(String name)
	throws IOException {
		try {
			Context context = new InitialContext();
			return (T) context.lookup("java:comp/env/" + name);
		}
		catch (NamingException e) {
			throw new IOException("", e);
		}
		catch (ClassCastException e) {
			throw new IOException("", e);
		}
	}

	/**
	 * Flushes a JSON string output to the client with HTTP 200 code
	 */
	protected void printJson(HttpServletResponse response, String message)
	throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		print(response, message);
	}

	/**
	 * Flushes a text output to the client with HTTP 500 code
	 */
	protected void printError(HttpServletResponse response, String message)
	throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		print(response, message);
	}

	/**
	 * Flushes an output to the client using UTF-8 encoding
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
