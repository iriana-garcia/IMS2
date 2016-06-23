package test;

import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NamingSecurityException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

public class ActiveDirectory {

	public static void main(String[] args) {
		try {

			// Hashtable props = new Hashtable();
			// String principalName = "jsemander@testad.com";
			// props.put(Context.SECURITY_PRINCIPAL, principalName);
			// props.put(Context.SECURITY_CREDENTIALS, "Taxman123!");
			// DirContext context;
			//
			// context = LdapCtxFactory.getLdapCtxInstance("ldap://" +
			// serverName
			// + "." + domainName + '/', props);
			// System.out.println("Authentication succeeded!");

			ActiveDirectoryUtil activeDirectory = new ActiveDirectoryUtil();

			activeDirectory.openConnection("appad@ghw.com", "Welcome01",
					"ghw.com", "389", "DC=ghw,DC=com", "simple", false);

			// activeDirectory.openConnection("bjami@testad.com",
			// "sdf", "172.16.0.22", "389", "DC=TestAD,DC=com",
			// "simple", false);

			System.out.println("ok");
			//
			// List<String> list = new ArrayList<String>();
			// list.add("ifernandez");
			// list.add("ibo1");
			//
			// // Searching
			// NamingEnumeration<SearchResult> result = activeDirectory
			// .searchIBO("DC=TestAD,DC=com");
			NamingEnumeration<SearchResult> result = activeDirectory
					.searchAccount("ifernandez",
							"DC=ghw,DC=com");
			//
			// StringBuffer content = new StringBuffer();
			//
			// System.out.println(new Date());
			//
			// int cant = 0;
			//
			while (result.hasMoreElements()) {
				// cant++;
				//
				// content.append("NUEVA PERSONA \n");
				//
				SearchResult rs = (SearchResult) result.nextElement();
				Attributes attrs = rs.getAttributes();

				// Attribute bitsAttribute = attrs.get("userAccountControl");
				//
				// // System.out.println(" " + attrs.get("displayName"));
				// // System.out.println("Aquiiiiiiiiii " +
				// // (Long.parseLong(bitsAttribute.get(0).toString())& 2));
				//
				// if (bitsAttribute != null) {
				// long lng = Long.parseLong(bitsAttribute.get(0).toString());
				// long secondBit = lng & 2; // get bit 2
				// if (secondBit == 0) {
				// System.out.println("Activo");
				// } else
				// System.out.println("Inactivo");
				// }
				//
				// System.out.println(attrs.size());
				// System.out.println("Otroooooo ooooooooooooooooooooooooo");
				//
				NamingEnumeration<String> ids = attrs.getIDs();
				while (ids.hasMoreElements()) {
					String s = (String) ids.nextElement();

					// content.append("\n");
					Attribute temp = attrs.get(s);
					// if (temp != null)
					// content.append(temp.toString());
					System.out.println(temp.getID() + " ----------- "
							+ temp.get().toString());
					;

				}

			}
			// //
			// Files.write(
			// Paths.get("C:\\Users\\ifernandez\\Documents\\MY DOCUMENTS\\ad\\ad.txt"),
			// content.toString().getBytes(), StandardOpenOption.CREATE);
			//
			// System.out.println(cant);
			//
			// // Closing LDAP Connection
			// activeDirectory.closeLdapConnection();
			//
			// System.out.println(new Date());
			//
			// System.out.println("termino: " + cant);

		} catch (AuthenticationException ne) {
			System.out.println("AuthenticationException " + ne.getMessage());
		} catch (AuthenticationNotSupportedException ne) {
			System.out.println("AuthenticationNotSupportedException"
					+ ne.getMessage());

		} catch (CommunicationException ne) {
			System.out.println("CommunicationException" + ne.getMessage());

		} catch (NamingSecurityException ne) {
			System.out.println("NamingSecurityException" + ne.getMessage());

		} catch (NamingException ne) {
			System.out.println("NamingException" + ne.getMessage());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
