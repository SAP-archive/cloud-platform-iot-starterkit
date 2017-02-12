package com.sap.iot.starterkit.mqtt.ingest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class KeyStoreUtil {

	public static SSLSocketFactory getSSLSocketFactory(Path path)
	throws KeyStoreException {
		String absolutePath = path.toAbsolutePath().toString();

		try {
			Properties pswd = new Properties();
			pswd.load(new FileInputStream(absolutePath + "/pswd.properties"));
			String password = pswd.getProperty("password");

			File keyStore = new File(absolutePath + "/client.ks");
			File trustStore = new File(absolutePath + "/client.ts");

			return KeyStoreUtil.getSSLSocketFactory(keyStore, trustStore, password);
		}
		catch (KeyStoreException | IOException e) {
			throw new KeyStoreException("Unable to create SSL factory", e);
		}
		finally {
			// FileUtil.deletePath(path);
		}

	}

	/**
	 * Gets SSL Socket Factory for HTTPS communication
	 */
	public static SSLSocketFactory getSSLSocketFactory(File keyStore, File trustStore,
		String password)
	throws KeyStoreException {

		KeyManager[] keyManagers = getKeyManagers(keyStore, password);
		TrustManager[] trustManagers = getTrustManagers(trustStore, password);

		return getSSLSocketFactory(keyManagers, trustManagers);
	}

	private static SSLSocketFactory getSSLSocketFactory(KeyManager[] keyManagers,
		TrustManager[] trustManagers)
	throws KeyStoreException {

		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagers, trustManagers, null);
		}
		catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new KeyStoreException("Unable to initilize a SSL context", e);
		}

		return sslContext.getSocketFactory();
	}

	private static KeyManager[] getKeyManagers(File file, String password)
	throws KeyStoreException {

		KeyStore keyStore = null;
		try {
			keyStore = loadKeyStore("JKS", new FileInputStream(file), password);
		}
		catch (FileNotFoundException e) {
			throw new KeyStoreException("Unable to find a key store file", e);
		}

		KeyManagerFactory keyManagerFactory = null;
		try {
			keyManagerFactory = KeyManagerFactory
				.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, password.toCharArray());
		}
		catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new KeyStoreException("Unable to initialize a key manager factory", e);
		}

		return keyManagerFactory.getKeyManagers();
	}

	private static TrustManager[] getTrustManagers(File file, String password)
	throws KeyStoreException {

		KeyStore trustStore = null;
		try {
			trustStore = loadKeyStore("JKS", new FileInputStream(file), password);
		}
		catch (FileNotFoundException e) {
			throw new KeyStoreException("Unable to find a trust store file", e);
		}

		TrustManagerFactory trustMaangerFactory = null;
		try {
			trustMaangerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustMaangerFactory.init(trustStore);
		}
		catch (KeyStoreException | NoSuchAlgorithmException e) {
			throw new KeyStoreException("Unable to initialize a trust manager factory", e);
		}

		return trustMaangerFactory.getTrustManagers();
	}

	private static KeyStore loadKeyStore(String type, InputStream stream, String password)
	throws KeyStoreException {

		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(type);
			keyStore.load(stream, password.toCharArray());
		}
		catch (KeyStoreException | CertificateException | NoSuchAlgorithmException
		| IOException e) {
			throw new KeyStoreException("Unable to load a key store", e);
		}

		return keyStore;
	}

}