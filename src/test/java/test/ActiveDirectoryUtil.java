package test;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

//import org.apache.commons.lang.StringUtils;

public class ActiveDirectoryUtil {

	private Properties properties;
	private DirContext dirContext;
	private SearchControls searchCtls;

	private String[] returnAttributes = { "sAMAccountName", "givenName", "sn",
			"objectClass", "objectCategory", "mail", "telephoneNumber",
			"memberOf", "userAccountControl", "distinguishedName", "initials" };

	private String baseFilter = "(&(objectCategory=Person)(objectClass=User)";

	public DirContext openConnection(String username, String password,
			String domainController, String port, String dn,
			String securityAuthentication, boolean ssl) throws NamingException {

		properties = new Properties();

		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "LDAP://" + domainController + ":"
				+ port);

		// Enable connection pooling
		// properties.put("com.sun.jndi.ldap.connect.pool", "true");
		// Specify timeout to be 5 seconds
		properties.put("com.sun.jndi.ldap.connect.timeout", "50000");

		// Specify SSL
		if (ssl)
			properties.put(Context.SECURITY_PROTOCOL, "ssl");

		// "simple", "none",GSSAPI, GSS-SPNEGO, EXTERNAL, DIGEST-MD5
		properties.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);

		properties.put(Context.SECURITY_PRINCIPAL, username);
		properties.put(Context.SECURITY_CREDENTIALS, password);

		dirContext = new InitialDirContext(properties);

		// initializing search controls
		searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchCtls.setReturningAttributes(returnAttributes);

		return dirContext;

	}

	public NamingEnumeration<SearchResult> searchUser(String searchValue,
			String searchBase) throws NamingException {
		String filter = getFilter(searchValue, null);

		return this.dirContext.search(searchBase, filter, searchCtls);
	}

	public NamingEnumeration<SearchResult> searchAccount(String searchValue,
			String searchBase) throws NamingException {
		String filter = getFilter(searchValue, "sAMAccountName");

		return this.dirContext.search(searchBase, filter, searchCtls);
	}

	public NamingEnumeration<SearchResult> searchAccountByEmail(
			String searchValue, String searchBase) throws NamingException {
		String filter = getFilter(searchValue, "mail");

		return this.dirContext.search(searchBase, filter, searchCtls);
	}

	public NamingEnumeration<SearchResult> searchAccountList(
			List<String> searchValue, String searchBase) throws NamingException {

		String filter = this.baseFilter
				+ "(!(userAccountControl:1.2.840.113556.1.4.803:=2))";
		filter += "(|";
		for (String string : searchValue) {
			filter += "";
			filter += "(sAMAccountName=" + string + ")";
		}

		filter += "))";

		return this.dirContext.search(searchBase, filter, searchCtls);
	}

	/**
	 * closes the LDAP connection with Domain controller
	 */
	public void closeLdapConnection() {
		try {
			if (dirContext != null)
				dirContext.close();
		} catch (NamingException e) {
			System.out.println(e.getMessage());
		}
	}

	private String getFilter(String searchValue, String field) {
		String filter = this.baseFilter
				+ "(!(userAccountControl:1.2.840.113556.1.4.803:=2))";
		if (StringUtils.isNotEmpty(searchValue))
			filter += "(" + field + "=" + searchValue + "))";
		else if (StringUtils.isNotEmpty(searchValue))
			filter += "(|(mail=*" + searchValue + "*)" + "(sAMAccountName=*"
					+ searchValue + "*)" + "(givenName=*" + searchValue + "*)"
					+ "(sn=*" + searchValue + "*)" + "))";
		else
			filter += ")";
		return filter;
	}

	public NamingEnumeration<SearchResult> searchIBO(String searchBase)
			throws NamingException {
		String filter = this.baseFilter + ")";
		return this.dirContext.search(searchBase, filter, searchCtls);
	}

}
