package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RestTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			// new RestTest()
			// .execCurlCmd("curl -X POST -vu clientapp:123456 http://localhost:8080/datagreatworks/oauth/token -H 'Accept: application/json' -d 'password=Gh486!daT05gr3&username=datagreatadmin&grant_type=password&scope=read%20write&client_secret=123456&client_id=clientapp'");

			HttpClient httpclient = new HttpClient();
			BufferedReader bufferedreader = null;

			// PostMethod postmethod = new PostMethod(
			// "http://localhost:8081/datagreatworks/oauth/token");
			// // postmethod.addParameter("Bearer",
			// "4395fb06-8ee5-40eb-817b-224369eb91");
			// //http://localhost:8081/datagreatworks/oauth/token?grant_type=password&client_id=restapp&client_secret=restapp&username=datagreatadmin&password=Gh486!daT05gr3
			// //curl -X POST -vu clientapp:123456
			// //http://localhost:8080/oauth/token -H "Accept: application/json"
			// //-d
			// "password=spring&username=roy&grant_type=password&scope=read%20write&client_secret=123456&client_id=clientapp"
			PostMethod postmethod = new PostMethod(
					"http://gvwiboimsdev:8080/greatdataworks/oauth/token");
			postmethod.addParameter("grant_type", "password");
			postmethod.addParameter("client_id", "restapp");
			postmethod.addParameter("client_secret", "restapp");
			postmethod.addParameter("password", "Gh486!daT05gr3");
			postmethod.addParameter("username", "datagreatadmin");
			// http://localhost:8081/datagreatworks/api/users/?access_tokenb06b811b-d6ff-4679-84b7-275a98237409
//			PostMethod postmethod = new PostMethod(
//					"http://gvwiboimsdev:8080/greatdataworks/api/users/");
//			postmethod.addParameter("access_token",
//					"f9040be2-64db-4fe9-96dc-e93d2d05893f");
			//

			// PostMethod postmethod = new PostMethod(
			// "http://http://gvwiboims:8080/greatdataworks/oauth/token");
			// postmethod.addParameter("grant_type", "refresh_token");
			// postmethod.addParameter("client_id", "restapp");
			// postmethod.addParameter("client_secret", "restapp");
			// postmethod.addParameter("refresh_token",
			// "740f5e00-8f6b-46ab-b21b-d532b9a7fe4b");

			int rCode = httpclient.executeMethod(postmethod);

			if (rCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err
						.println("The Post postmethod is not implemented by this URI");
				postmethod.getResponseBodyAsString();
			} else {
				bufferedreader = new BufferedReader(new InputStreamReader(
						postmethod.getResponseBodyAsStream()));
				String readLine;
				while (((readLine = bufferedreader.readLine()) != null)) {
					System.err.println(readLine);

					JSONParser parser = new JSONParser();
					Object o = parser.parse(readLine);

					JSONArray jsonObject = (JSONArray) o;

					Iterator it = jsonObject.iterator();
					while (it.hasNext()) {
						JSONObject jsonUser = (JSONObject) it.next();
						System.out.println((String) jsonUser.get("id"));
						System.out.println((String) jsonUser.get("name"));
					}
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public int execCurlCmd(String command) {
		String s = null;
		try {

			// run the Unix "ps -ef" command

			Process p = Runtime
					.getRuntime()
					.exec(new String[] {
							"curl",
							"-X",
							"POST",
							"-vu",
							"clientapp:123456",
							"http://localhost:8080/datagreatworks/oauth/token",
							"-H",
							"Accept: application/json",
							"-d",
							"password=Gh486!daT05gr3&username=datagreatadmin&grant_type=password&scope=read%20write&client_secret=123456&client_id=clientapp" });

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			// read the output from the command

			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			// read any errors from the attempted command

			System.out
					.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}

			return (0);
		} catch (IOException e) {
			System.out.println("exception happened - here's what I know: ");
			e.printStackTrace();
			return (-1);
		}
	}

}
