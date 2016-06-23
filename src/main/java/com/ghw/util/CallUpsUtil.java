package com.ghw.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class CallUpsUtil {

	private static final String userId = "141GHW005842";

	public String[] searchStateCityByZip(String zipCode) {

		String[] o = new String[2];
		String city = null;
		String state = null;

		try {

			HttpClient httpclient = new HttpClient();
			BufferedReader bufferedreader = null;

			String call = "http://production.shippingapis.com/ShippingAPITest.dll?API=CityStateLookup"
					+ "&XML=<CityStateLookupRequest USERID=\""
					+ userId
					+ "\"><ZipCode ID=\"0\">"
					+ "<Zip5>"
					+ zipCode
					+ "</Zip5></ZipCode></CityStateLookupRequest>";

			call = URIUtil.encodeQuery(call);
			GetMethod getmethod = new GetMethod(call);
			int rCode = httpclient.executeMethod(getmethod);

			if (rCode != HttpStatus.SC_NOT_IMPLEMENTED) {

				StringBuilder result = new StringBuilder();

				bufferedreader = new BufferedReader(new InputStreamReader(
						getmethod.getResponseBodyAsStream()));
				String readLine;
				while (((readLine = bufferedreader.readLine()) != null)) {
					//System.err.println(readLine);
					result.append(readLine);
				}

				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new InputSource(new StringReader(
						result.toString())));
				doc.getDocumentElement().normalize();
//
//				System.out.println("Root element :"
//						+ doc.getDocumentElement().getNodeName());

				NodeList list = doc.getElementsByTagName("ZipCode");

				for (int temp = 0; temp < list.getLength(); temp++) {

					Node nNode = list.item(temp);

//					System.out.println("\nCurrent Element :"
//							+ nNode.getNodeName());

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;

						NodeList ciList = eElement.getElementsByTagName("City");
						Element ciElement = (Element) ciList.item(0);

						NodeList textCityList = ciElement.getChildNodes();
						city = ((Node) textCityList.item(0)).getNodeValue()
								.trim();

						NodeList ciState = eElement
								.getElementsByTagName("State");
						Element stateElement = (Element) ciState.item(0);

						NodeList textStateList = stateElement.getChildNodes();
						state = ((Node) textStateList.item(0)).getNodeValue()
								.trim();

					}
				}

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		o[0] = city;
		o[1] = state;

		return o;

	}

	public static void main(String[] args) {
		CallUpsUtil callUpsUtil = new CallUpsUtil();
		callUpsUtil.searchStateCityByZip("sas");
	}
}
