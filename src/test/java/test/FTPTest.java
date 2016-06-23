package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPSSocketFactory;

public final class FTPTest {
	private static final String KEYSTORE_PASS = "*****";
	private static final String KEYSTORE_FILE_NAME = "C:\\Users\\ifernandez\\Documents\\borrar\\test";

	public static final void main(String[] args) {
		int base = 0;
		boolean storeFile = false, binaryTransfer = false, error = false;
		String server, username, password;
		String protocol = "TLS"; // SSL/TLS
		FTPSClient ftps = null;

		server = "GHWHTTP2";
		username = "ghwfinance";
		password = "t3exupHabr";

		try {
			ftps = new FTPSClient(protocol, false);
			ftps.setRemoteVerificationEnabled(false);
			SSLContext sslContext = getSSLContext();
			FTPSSocketFactory sf = new FTPSSocketFactory(sslContext);
			ftps.setSocketFactory(sf);
			ftps.setBufferSize(1000);
			KeyManager keyManager = getKeyManagers()[0];
			TrustManager trustManager = getTrustManagers()[0];
			ftps.setControlEncoding("UTF-8");

			ftps.setKeyManager(keyManager);
			ftps.setTrustManager(trustManager);

			ftps.addProtocolCommandListener(new PrintCommandListener(
					new PrintWriter(System.out)));

			ftps.connect(server, 21);

			System.out.println("Connected to " + server + ".");

			int reply = ftps.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftps.disconnect();
				System.err.println("FTP server refused connection.");
				System.exit(1);
			}
			if (!ftps.login(username, password)) {
				ftps.logout();
				error = true;
			}

			ftps.pwd();
			ftps.changeWorkingDirectory("");

			FTPFile[] files = ftps.listFiles();
			for (FTPFile file : files) {
				System.out.println(file.getRawListing());
			}
		} catch (IOException e) {
			if (ftps.isConnected()) {
				try {
					ftps.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}
			System.err.println("Could not connect to server.");
			e.printStackTrace();
			System.exit(1);
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ftps.isConnected()) {
				try {
					ftps.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}
		}

		System.exit(error ? 1 : 0);
	} // end main

	private static SSLContext getSSLContext() throws KeyManagementException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, UnrecoverableKeyException, IOException {
		TrustManager[] tm = getTrustManagers();
		System.out.println("Init SSL Context");
		SSLContext sslContext = SSLContext.getInstance("SSLv3");
		sslContext.init(null, tm, null);

		return sslContext;
	}

	private static KeyManager[] getKeyManagers() throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, UnrecoverableKeyException {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(KEYSTORE_FILE_NAME),
				KEYSTORE_PASS.toCharArray());

		KeyManagerFactory tmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		tmf.init(ks, KEYSTORE_PASS.toCharArray());

		return tmf.getKeyManagers();
	}

	private static TrustManager[] getTrustManagers() throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, UnrecoverableKeyException {
		try (InputStream file = new FileInputStream(Paths.get(
				KEYSTORE_FILE_NAME).toFile())) {

			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(file, KEYSTORE_PASS.toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);

			return tmf.getTrustManagers();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;

	}
}
