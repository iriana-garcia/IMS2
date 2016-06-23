package com.ghw.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class IpServer {

	private static List<String> hostList = new ArrayList<String>();
	private static String host = null;

	public static List<String> ipServerList() {

		try {

			if (hostList == null || hostList.size() == 0) {
				Enumeration en = NetworkInterface.getNetworkInterfaces();
				while (en.hasMoreElements()) {
					NetworkInterface ni = (NetworkInterface) en.nextElement();
					Enumeration ee = ni.getInetAddresses();
					while (ee.hasMoreElements()) {
						InetAddress ia = (InetAddress) ee.nextElement();
						hostList.add(ia.getHostAddress());

						System.out.println(ia.getHostAddress());
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return hostList;
	}

	public static String ipServer() {

		try {

			if (StringUtils.isBlank(host)) {

				Enumeration en = NetworkInterface.getNetworkInterfaces();
				while (en.hasMoreElements()) {
					NetworkInterface ni = (NetworkInterface) en.nextElement();
					Enumeration ee = ni.getInetAddresses();
					while (ee.hasMoreElements()) {
						InetAddress ia = (InetAddress) ee.nextElement();
						if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress()
								&& ia instanceof Inet4Address
								&& !ia.getHostAddress().equals("127.0.0.1")) {
							host = ia.getHostAddress();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return host;
	}

	public static void main(String[] args) {
		IpServer.ipServer();
		IpServer.ipServerList();
	}

}
