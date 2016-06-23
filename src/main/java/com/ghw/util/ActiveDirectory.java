package com.ghw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.commons.lang3.StringUtils;

import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.UserUtil;

/**
 * Utilitary class for connect to AD
 * 
 * @author ifernandez
 * 
 */
public class ActiveDirectory {

	private Properties properties;
	private DirContext dirContext;
	private SearchControls searchCtls;
	private String[] returnAttributes = { "sAMAccountName", "givenName", "sn",
			"objectClass", "objectCategory", "mail", "telephoneNumber",
			"memberOf", "userAccountControl", "userPrincipalName",
			"distinguishedName", "initials" };
	private String baseFilter = "(&(objectCategory=Person)(objectClass=User)";
	private ConfigurationGeneral configuration;

	private String userState = "userAccountControl:1.2.840.113556.1.4.803";
	private String userActive = "2";

	public DirContext openConnection(ConfigurationGeneral conf)
			throws NamingException {

		this.configuration = conf;
		properties = new Properties();

		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "LDAP://" + conf.getLdapServer()
				+ ":" + conf.getLdapPort());

		properties.put("com.sun.jndi.ldap.connect.timeout", "5000");

		// Specify SSL
		if (conf.isLdapSsl())
			properties.put(Context.SECURITY_PROTOCOL, "ssl");

		// "simple", "none",GSSAPI, GSS-SPNEGO, EXTERNAL, DIGEST-MD5
		properties.put(Context.SECURITY_AUTHENTICATION, conf.getLdapSecType());
		//

		if (StringUtils.isNotEmpty(conf.getLdapUser())) {
			properties.put(Context.SECURITY_PRINCIPAL, conf.getLdapUser());
			properties.put(Context.SECURITY_CREDENTIALS, conf.getLdapPass());
		}

		dirContext = new InitialDirContext(properties);

		// initializing search controls
		searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchCtls.setReturningAttributes(returnAttributes);

		return dirContext;

	}

	public DirContext openConnectionUser(ConfigurationGeneral conf,
			String name, String password) throws NamingException {

		this.configuration = conf;
		properties = new Properties();

		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "LDAP://" + conf.getLdapServer()
				+ ":" + conf.getLdapPort());

		properties.put("com.sun.jndi.ldap.connect.timeout", "10000");

		// Specify SSL
		if (conf.isLdapSsl())
			properties.put(Context.SECURITY_PROTOCOL, "ssl");

		// "simple", "none",GSSAPI, GSS-SPNEGO, EXTERNAL, DIGEST-MD5
		properties.put(Context.SECURITY_AUTHENTICATION, conf.getLdapSecType());
		//
		// get the last past in the user
		Integer i = conf.getLdapUser().indexOf("@");
		String lastPart = conf.getLdapUser().substring(i,
				conf.getLdapUser().length());

		properties.put(Context.SECURITY_PRINCIPAL, name + lastPart);
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

	public NamingEnumeration<SearchResult> searchAccountName(String name,
			String searchBase) throws NamingException {

		String filter = getFilter(name, configuration.getLdapAuthenti());

		return this.dirContext.search(searchBase, filter, searchCtls);
	}

	private String getFilterAll(String searchValue, String field) {
		String filter = this.baseFilter;
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

	public UserUtil searchAccount(String name) throws NamingException,
			SystemException {
		String filter = getFilterAll(name, "sAMAccountName");

		NamingEnumeration<SearchResult> results = dirContext.search(
				configuration.getLdapDn(), filter, searchCtls);

		SearchResult searchResult = null;
		UserUtil userUtil = null;
		if (results.hasMoreElements()) {
			searchResult = (SearchResult) results.nextElement();

			// make sure there is not another item available, there should be
			// only 1 match
			if (results.hasMoreElements()) {
				throw new SystemException("repeated_user");

			}

			Attributes attrs = searchResult.getAttributes();

			userUtil = new UserUtil();
			userUtil.setUserName(attrs.get("userPrincipalName").toString());

			Attribute bitsAttribute = attrs.get("userAccountControl");
			boolean stateAD = false;
			if (bitsAttribute != null) {
				long lng = Long.parseLong(bitsAttribute.get(0).toString());
				stateAD = ((lng & 2) == 0);
			}
			userUtil.setState(stateAD);
		}

		return userUtil;
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
		String filter = this.baseFilter + "(!(" + userState + ":=" + userActive
				+ "))";
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

	// public NamingEnumeration<SearchResult> searchAccountActiveList(
	// List<String> searchValue, String searchBase) throws NamingException {
	//
	// String filter = this.baseFilter + "(!(" + userState + ":=" + userActive
	// + "))";
	// filter += "(|";
	// for (String string : searchValue) {
	// filter += "";
	// filter += "(sAMAccountName=" + string + ")";
	// }
	//
	// filter += "))";
	//
	// return this.dirContext.search(searchBase, filter, searchCtls);
	// }
	//
	// public NamingEnumeration<SearchResult> searchAccountActiveList(
	// String searchBase) throws NamingException {
	//
	// String filter = this.baseFilter + "(!(" + userState + ":=" + userActive
	// + ")))";
	//
	// return this.dirContext.search(searchBase, filter, searchCtls);
	// }

	public List<UserUtil> searchAllUser(String searchBase)
			throws NamingException {
		String filter = this.baseFilter + ")";
		return searchAccountAll(filter, searchBase);
	}

	public List<UserUtil> searchAllUserActive(String searchBase)
			throws NamingException {
		String filter = this.baseFilter + "(!(" + userState + ":=" + userActive
				+ ")))";
		return searchAccountAll(filter, searchBase);
	}

	public List<UserUtil> searchAccountActiveListExclude(List<String> names,
			String searchBase) throws NamingException {

		String filter = this.baseFilter + "(!(" + userState + ":=" + userActive
				+ ")) (!(";
		filter += "(|";
		for (String string : names) {
			filter += "";
			filter += "(sAMAccountName=" + string + ")";
		}

		filter += "))))";

		return searchAccountAll(filter, searchBase);
	}

	private List<UserUtil> searchAccountAll(String filter, String searchBase)
			throws NamingException {

		// Create the initial directory context
		LdapContext ctx = new InitialLdapContext(properties, null);

		List<UserUtil> listUser = new ArrayList<UserUtil>();

		try {
			// We want all results.
			searchCtls.setCountLimit(0);

			// We want to wait to get all results.
			searchCtls.setTimeLimit(0);

			// Active Directory limits our results, so we need multiple
			// requests to retrieve all results. The cookie is used to
			// save the current position.
			byte[] cookie = null;

			// We want 500 results per request.
			ctx.setRequestControls(new Control[] { new PagedResultsControl(500,
					Control.CRITICAL) });

			// The request loop starts here.
			do {
				// Start the search with our configuration.
				NamingEnumeration<SearchResult> answer = ctx.search(searchBase,
						filter, searchCtls);

				while (answer.hasMoreElements()) {

					UserUtil user = new UserUtil();

					SearchResult rs = (SearchResult) answer.nextElement();
					Attributes attrs = rs.getAttributes();

					NamingEnumeration<String> ids = attrs.getIDs();
					while (ids.hasMoreElements()) {
						String s = (String) ids.nextElement();
						Attribute temp = attrs.get(s);
						if (temp != null) {
							if (temp.getID().equals("sAMAccountName"))
								user.setUserName(temp.get().toString());
							else if (temp.getID().equals("givenName"))
								user.setFirstName(temp.get().toString());
							else if (temp.getID().equals("initials"))
								user.setMiddleName(temp.get().toString());
							else if (temp.getID().equals("sn"))
								user.setLastName(temp.get().toString());
							else if (temp.getID().equals("mail"))
								user.setEmail(temp.get().toString());
							else if (temp.getID().equals("telephoneNumber"))
								user.setTelephoneNumber(temp.get().toString());
							else if (temp.getID().equals("distinguishedName"))
								user.setDistinguishedName(temp.get().toString());
							else if (temp.getID().equals("userAccountControl")) {
								Attribute bitsAttribute = attrs
										.get("userAccountControl");
								boolean stateAD = false;
								if (bitsAttribute != null) {
									long lng = Long.parseLong(bitsAttribute
											.get(0).toString());
									stateAD = ((lng & 2) == 0);
								}
								user.setState(stateAD);
							}

						}
					}

					listUser.add(user);
				}

				// Find the cookie in our response and save it.
				Control[] controls = ctx.getResponseControls();
				if (controls != null) {
					for (int i = 0; i < controls.length; i++) {
						if (controls[i] instanceof PagedResultsResponseControl) {
							PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
							cookie = prrc.getCookie();
						}
					}
				}

				// Use the cookie to configure our new request
				// to start from the saved position in the cookie.
				ctx.setRequestControls(new Control[] { new PagedResultsControl(
						500, cookie, Control.CRITICAL) });
			} while (cookie != null);

			// We are done, so close the Context object.
			ctx.close();

		} catch (NamingException e) {
			System.err.println("Paged Search failed." + e);
		} catch (java.io.IOException e) {
			System.err.println("Paged Search failed." + e);
		}

		return listUser;

	}

	public void testConnection(ConfigurationGeneral entity)
			throws NamingException {

		openConnection(entity);

		searchUser("prueba", entity.getLdapDn());

		closeLdapConnection();
	}

}
