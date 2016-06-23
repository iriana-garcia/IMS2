package com.ghw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import com.ghw.model.ConfigurationGeneral;

public class FTPUtil {

	// Creating FTP Client instance
	private FTPSClient ftpClient = null;

	public void validateConnectFTP(ConfigurationGeneral entity)
			throws Exception {
		connectFTP(entity);
		disconnect();
	}

	public FTPSClient connectFTP(ConfigurationGeneral entity) throws Exception {

		try {

			// validate folder exists
			if (StringUtils.isNotBlank(entity.getPathOracle())) {

				if (StringUtils.isBlank(entity.getPortOracle()))
					throw new Exception("port_required");
				if (StringUtils.isBlank(entity.getUserOracle()))
					throw new Exception("user_ftp_required");
				if (StringUtils.isBlank(entity.getPasswordOracle()))
					throw new Exception("pass_ftp_required");

				ftpClient = new FTPSClient(true);

				ftpClient.setTrustManager(TrustManagerUtils
						.getAcceptAllTrustManager());
				//
				// ftpClient.setAuthValue("TLS");

				// CertificateFactory cf =
				// CertificateFactory.getInstance("X.509");
				// InputStream certFile = new FileInputStream(
				// "C:\\Users\\ifernandez\\Documents\\MY DOCUMENTS\\Great DataWorks\\ssl_certificate\\ssl_certificate.crt");
				// Certificate ca = cf.generateCertificate(certFile); // this is
				// // java.security.cert.Certificate;
				//
				// KeyStore keyStore = KeyStore.getInstance(KeyStore
				// .getDefaultType());
				// keyStore.load(null, null);
				// keyStore.setCertificateEntry("ca", ca);
				//
				// TrustManager trustManager = TrustManagerUtils
				// .getDefaultTrustManager(keyStore);
				// SSLContext sslContext =
				// SSLContextUtils.createSSLContext("TLS",
				// null /* keyManager */, trustManager);
				//
				// ftpClient = new FTPSClient(true, sslContext);

				ftpClient.addProtocolCommandListener(new PrintCommandListener(
						new PrintWriter(System.out)));

				ftpClient.connect(entity.getPathOracle(),
						new Integer(entity.getPortOracle()));
				int replyCode = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(replyCode)) {
					throw new Exception("connect_ftp_fail");

				}

				ftpClient.enterLocalPassiveMode();

				boolean success = ftpClient.login(entity.getUserOracle(),
						entity.getPasswordOracle());

				if (!success) {
					throw new SystemException("login_ftp_fail");
				}

				return ftpClient;

			}

		} catch (UnknownHostException an) {
			throw new Exception("ftp_host_incorrect");

		} catch (Exception e) {
			throw e;
		}

		return null;

	}

	// Disconnect the connection to FTP
	public void disconnect() throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.logout();
			ftpClient.disconnect();

		}
	}

	// Method to upload the File on the FTP Server
	public void uploadFTPFile(String folderName, Path pathLocal,
			ConfigurationGeneral conf) throws Exception {
		try {

			connectFTP(conf);

			ftpClient.makeDirectory(folderName);

			// Set protection buffer size
			ftpClient.execPBSZ(0);
			// ftpClient.execCCC();
			// // Set data channel protection to private
			ftpClient.execPROT("P");

			ftpClient.setEnabledProtocols(new String[] { "SSLv3", "TLSv1.2" });
			ftpClient.changeWorkingDirectory("/" + folderName);
			ftpClient.setRemoteVerificationEnabled(true);
			ftpClient.setControlKeepAliveTimeout(660);

			// ftpClient.removeDirectory(folderName);

//			for (FTPFile f : ftpClient.listFiles("/")) {
//				System.out.println(f.getRawListing());
//				System.out.println(f.toFormattedString());
//			}

			File folder = pathLocal.toFile();
			File[] listOfFiles = folder.listFiles();
			// get all the zip file an copy into
			for (File file : listOfFiles) {
				if (file.isFile()) {
					// boolean upload = uploadSingleFile(
					// Paths.get(file.getPath()), folderName,
					// file.getName());

					try (InputStream inputStream = new FileInputStream(
							file.getPath())) {

						ftpClient.storeFile(file.getName(), inputStream);

					}
					// System.out.println(upload + " "
					// + ftpClient.getReplyString());

				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());

		} finally {
			disconnect();
		}
	}

	public boolean uploadSingleFile(Path localFilePath, String remoteFilePath,
			String fileName) throws IOException {

		try (InputStream inputStream = new FileInputStream(
				localFilePath.toFile())) {

			return ftpClient.storeFile(fileName, inputStream);

		}

	}
}
