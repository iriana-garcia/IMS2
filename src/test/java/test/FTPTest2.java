package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import com.ghw.util.FTPUtil;

public class FTPTest2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			

//			FTPTest2 t = new FTPTest2();
//			// t.putFile("GHWHTTP2", 990, "ghwfinance", "t3exupHabr",
//			// "C:\\New folder\\test.txt", "test.txt");
//
//			t.uploadFile();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	private boolean uploadFile() {
		
		return true;

//		String host = "GHWHTTP2";
//		String port = "990";
//		String user = "ghwfinance";
//		String pass = "t3exupHabr";
//		String path = "";
//
//		Properties pt = new Properties();
//		pt.setProperty("connection.host", host);
//		pt.setProperty("connection.port", port);
//		pt.setProperty("user.login", user);
//		pt.setProperty("user.password", pass);
//		pt.setProperty("connection.type", "AUTH_SSL_FTP_CONNECTION");
//		pt.setProperty("connection.timeout", "10000");
//		pt.setProperty("connection.passive", "true");
//		//FTPConnection connection = null;
//		try {
//
//			// FTPFile fromFile = new FTPFile(htmlFile);
//			// FTPFile toFile = new FTPFile(path, htmlFile.getName());
//
//			//connection = FTPConnectionFactory.getInstance(pt);
//			// connection.addFTPStatusListener(this);
//			connection.connect();
//
//			// try {
//			//
//			// connection.deleteFile(toFile);
//			// } catch (FtpWorkflowException ex) {
//			// connection.noOperation();
//			// }
//
//			// connection.uploadFile(fromFile, toFile);
//			connection.disconnect();
//
//			return true;
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			if (connection != null) {
//				connection.disconnect();
//			}
//			// txtStatus.append(ex.getMessage() + newLine);
//			// txtStatus.append("Cannot continue..." + newLine);
//			return false;
//		}
//	}
//
//	public void putFile(String host, int port, String username,
//			String password, String localFilename, String remoteFilename) {
//		try {
//			FTPSClient ftpClient = new FTPSClient("SSL", true);
//			// Connect to host
//			ftpClient.connect(host, port);
//			int reply = ftpClient.getReplyCode();
//			if (FTPReply.isPositiveCompletion(reply)) {
//
//				// Login
//				if (ftpClient.login(username, password)) {
//
//					// Set protection buffer size
//					ftpClient.execPBSZ(0);
//					// Set data channel protection to private
//					ftpClient.execPROT("P");
//					// Enter local passive mode
//					ftpClient.enterLocalPassiveMode();
//
//					// Store file on host
//					InputStream is = new FileInputStream(localFilename);
//					if (ftpClient.storeFile(remoteFilename, is)) {
//						is.close();
//					} else {
//						System.out.println("Could not store file");
//					}
//					// Logout
//					ftpClient.logout();
//
//				} else {
//					System.out.println("FTP login failed");
//				}
//
//				// Disconnect
//				ftpClient.disconnect();
//
//			} else {
//				System.out.println("FTP connect to host failed");
//			}
//		} catch (IOException ioe) {
//			System.out.println("FTP client received network error");
//		}
	}

}
