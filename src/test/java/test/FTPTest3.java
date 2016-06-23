package test;


public class FTPTest3{ 
	// implements FTPListener {
	//
	// private FTPConnection ftpConnection = null;
	//
	// public static void main(String[] args) {
	// FTPTest3 ftp = new FTPTest3();
	//
	// try {
	// String host = "GHWHTTP2";
	// String port = "990";
	// String username = "ghwfinance";
	// String password = "t3exupHabr";
	// String path = "/";
	// FTPConnection connection = ftp.connect(host, 990, username,
	// password, 2, 10000000, true);
	//
	// Path path2 = Paths.get("C:\\New folder\\test.txt");
	// FTPFile fromFile = new FTPFile(path2.toFile());
	// FTPFile toFile = new FTPFile(path, "test.txt");
	//
	// InputStream upStream = new FileInputStream(path2.toFile());
	// connection.uploadStream(upStream, toFile);
	// // .uploadFile(fromFile, toFile)
	// connection.disconnect();
	//
	// } catch (Exception e) {
	// System.out.println(e.getMessage());
	// }
	//
	// }
	//
	// public FTPConnection connect(String hostname, int port, String username,
	// String password, int connectionType, int timeout,
	// boolean passiveMode) {
	// FTPConnection connection = null;
	// try {
	// connection = FTPConnectionFactory.getInstance(getProperties(
	// hostname, port, username, password, connectionType,
	// timeout, passiveMode));
	// connection.addFTPStatusListener(this);
	// connection.connect();
	//
	// } catch (Exception e) {
	// }
	// ftpConnection = connection;
	// return connection;
	// }
	//
	// public Properties getProperties(String hostname, int port, String
	// username,
	// String password, int connectionType, int timeout,
	// boolean passiveMode) {
	// Properties pt = new Properties();
	// pt.setProperty("connection.host", hostname);
	// pt.setProperty("connection.port", "" + port);
	// pt.setProperty("user.login", username);
	// pt.setProperty("user.password", password);
	// pt.setProperty("connection.type", getConnectionType(connectionType));
	// pt.setProperty("connection.timeout", "" + timeout);
	// pt.setProperty("connection.passive", "" + passiveMode);
	// return pt;
	// }
	//
	// public String getConnectionType(int connectionType) {
	// String type = "FTP_CONNECTION";
	// if (connectionType == 2) {
	// type = "IMPLICIT_SSL_FTP_CONNECTION";
	// }
	// if (connectionType == 3) {
	// type = "AUTH_SSL_FTP_CONNECTION";
	// }
	// if (connectionType == 4) {
	// type = "AUTH_TLS_FTP_CONNECTION";
	// }
	// if (connectionType == 5) {
	// type = "IMPLICIT_TLS_FTP_CONNECTION";
	// }
	// return type;
	// }
	//
	// @Override
	// public void connectionStatusChanged(FTPEvent arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// private ArrayList replies = new ArrayList();
	//
	// @Override
	// public void replyMessageArrived(FTPEvent event) {
	// replies = new ArrayList();
	// for (String e : event.getReply().getLines()) {
	// if (!e.trim().equals("")) {
	// e = e.substring(3).trim().replace("\n", "");
	// if (!e.toUpperCase().contains("COMMAND SUCCESSFUL")) {
	// e = e.substring(1).trim();
	// replies.add(e);
	// System.out.println(e);
	// }
	// }
	// }
	// }

}
