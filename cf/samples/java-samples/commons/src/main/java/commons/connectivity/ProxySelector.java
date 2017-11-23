package commons.connectivity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import commons.utils.Console;

public class ProxySelector {

	private static final java.net.ProxySelector DEFAULT_SELECTOR = java.net.ProxySelector
		.getDefault();

	private ProxySelector() {
	}

	public static void setDefault() {
		java.net.ProxySelector.setDefault(DEFAULT_SELECTOR);
	}

	public static void setProxy(String proxyHost, String proxyPort) {
		if (proxyHost == null || proxyPort == null) {
			return;
		}

		if (!proxyHost.trim().isEmpty() && !proxyPort.trim().isEmpty()) {
			int proxyPortInteger;
			try {
				proxyPortInteger = Integer.parseInt(proxyPort);
			}
			catch (NumberFormatException e) {
				Console.printWarning(String.format("Invalid proxy port: %1$s", proxyPort));
				return;
			}

			final Proxy HTTP_PROXY = new Proxy(Type.HTTP,
				new InetSocketAddress(proxyHost, proxyPortInteger));
			final Proxy SOCKS_PROXY = new Proxy(Type.SOCKS,
				new InetSocketAddress(proxyHost, proxyPortInteger));
			java.net.ProxySelector.setDefault(new java.net.ProxySelector() {

				@Override
				public List<Proxy> select(URI uri) {
					List<Proxy> proxies = new LinkedList<Proxy>();
					if (uri.toString().startsWith("socket")) {
						proxies.add(SOCKS_PROXY);
					}
					else {
						proxies.add(HTTP_PROXY);
					}
					return proxies;
				}

				@Override
				public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
					DEFAULT_SELECTOR.connectFailed(uri, sa, ioe);
				}

			});
		}
	}

}