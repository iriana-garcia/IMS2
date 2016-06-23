package com.ghw.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

public class Context {

	public static final String app_resources = "constantes";
	public static final String ui_messages = "message_en";
	public static final String error_messages = "message_error_en";

	// not to be instantiated
	private Context() {

	}

	public static String getIp() {

		return ((HttpServletRequest) Context.getFacesContext()
				.getExternalContext().getRequest()).getRemoteAddr();
	}

	/**
	 * Gets a managed bean specified by name
	 * 
	 * @param name
	 *            The name of the managed bean
	 * @return Object
	 */
	public static Object getBean(String name) {
		try {
			return (Object) getFacesContext().getApplication().getELResolver()
					.getValue(getFacesContext().getELContext(), null, name);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Adds a message to FacesContext
	 * 
	 * @param msg
	 *            message text
	 * @param severity
	 *            severity text
	 */
	public static void addMessage(String msg, Severity severity) {
		getFacesContext().addMessage("form:txtCorp",
				new FacesMessage(severity, msg, null));
	}

	/**
	 * Adds a message to FacesContext
	 * 
	 * @param msg
	 *            message text
	 * @param severity
	 *            severity text
	 */
	public static void addMessage(String msg, String componentId,
			Severity severity) {
		getFacesContext().addMessage(componentId,
				new FacesMessage(severity, msg, null));
		if (StringUtils.isNotBlank(componentId)) {
			// set the component to Not valid to add clase
			// ui-state-error
			UIViewRoot uiv = FacesContext.getCurrentInstance().getViewRoot();
			UIInput uii = (UIInput) uiv.findComponent(componentId);
			if (uii != null) {
				Object value = uii.getValue();
				uii.setValid(false);
				uii.setValue(value);
			}
		}
	}

	/**
	 * Adds a message to FacesContext with severity INFO
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addInfoMessage(String msg) {
		addMessage(msg, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Adds a message to FacesContext with severity ERROR
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addErrorMessage(String msg) {
		addMessage(msg, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Adds a message to FacesContext with severity FATAL
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addFatalMessage(String msg) {
		addMessage(msg, FacesMessage.SEVERITY_FATAL);
	}

	/**
	 * Adds a message to FacesContext with severity WARN
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addWarnMessage(String msg) {
		addMessage(msg, FacesMessage.SEVERITY_WARN);
	}

	/**
	 * Adds a message from the resource file to FacesContext with severity INFO
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addInfoMessageFromMSG(String id_msg) {
		String msg = (!getUIMsg(id_msg).equals("")) ? getUIMsg(id_msg)
				: getUIMsg("genericerror");
		addMessage(msg, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Adds a message from the resource file to FacesContext with severity ERROR
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addErrorMessageFromMSG(String id_msg, String componentId) {
		String msg = (!getUIMsg(id_msg).equals("")) ? getUIMsg(id_msg)
				: (getUIMsg("genericerror") + " " + id_msg);
		addMessage(msg, componentId, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Adds a message from the resource file to FacesContext with severity ERROR
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addErrorMessageFromMSG(String id_msg,
			String componentId, Severity severity) {
		String msg = (!getUIMsg(id_msg).equals("")) ? getUIMsg(id_msg)
				: (getUIMsg("genericerror") + " " + id_msg);
		addMessage(msg, componentId, severity);
	}

	/**
	 * Adds a message from the resource file to FacesContext with severity ERROR
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addErrorMessageFromMSG(SystemException e) {

		if (e.getExceptions().size() > 0)
			for (SystemException ex : e.getExceptions()) {
				addMessage(
						(!getUIMsg(ex.getMessage()).equals("")) ? getUIMsg(ex.getMessage())
								: ex.getMessage(), ex.getIdComponent(),
						ex.getSeverity());

			}
		else {
			addMessage(
					(!getUIMsg(e.getMessage()).equals("")) ? getUIMsg(e.getMessage())
							: e.getMessage(), e.getIdComponent(),
					e.getSeverity());
		}
	}

	/**
	 * Adds a message from the resource file to FacesContext with severity ERROR
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addErrorMessageFromMSG(String id_msg) {
		String msg = (!getUIMsg(id_msg).equals("")) ? getUIMsg(id_msg)
				: (getUIMsg("genericerror") + " " + id_msg);
		addMessage(msg, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Adds a message from the resource file to FacesContext with severity FATAL
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addFatalMessageFromMSG(String id_msg) {
		String msg = (!getUIMsg(id_msg).equals("")) ? getUIMsg(id_msg)
				: getUIMsg("genericerror");
		addMessage(msg, FacesMessage.SEVERITY_FATAL);
	}

	/**
	 * Adds a message from the resource file to FacesContext with severity WARN
	 * 
	 * @param msg
	 *            message text
	 */
	public static void addWarnMessageFromMSG(String id_msg) {
		String msg = (!getUIMsg(id_msg).equals("")) ? getUIMsg(id_msg)
				: getUIMsg("genericerror");
		addMessage(msg, FacesMessage.SEVERITY_WARN);
	}

	/**
	 * Gets a resource from a resource file name and the name of the resource
	 * 
	 * @param resource_file
	 *            Filename resource
	 * 
	 * @param resource_name
	 *            resource name
	 * 
	 * @return Object
	 */
	public static Object getResource(String resource_file, String resource_name) {
		try {
			return ResourceBundle.getBundle(resource_file).getObject(
					resource_name);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getAllResource(String resource_file) {
		try {

			ResourceBundle labels = ResourceBundle.getBundle(resource_file);

			Enumeration bundleKeys = labels.getKeys();
			Map<String, String> llaves = new HashMap<String, String>();

			while (bundleKeys.hasMoreElements()) {

				// key[0]
				String key = (String) bundleKeys.nextElement();
				// key[1]
				String value = labels.getString(key);
				// System.out.println("key = " + key + ", " + "value = " +
				// value);
				if (!llaves.containsKey(key)) {
					llaves.put(key, value);
				}

			}

			return llaves;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets a message from its identifier
	 * 
	 * @param id_msg
	 *            Message ID
	 * 
	 * @return String Message
	 */
	public static String getUIMsg(String id_msg) {
		try {
			return ResourceBundle.getBundle(error_messages,
					getFacesContext().getViewRoot().getLocale()).getString(
					id_msg);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Gets a text action from its identifier
	 * 
	 * @param id
	 *            Message ID
	 * @return String Message
	 */
	public static String getMSGText(String id) {
		return (String) getResource(ui_messages, id);
	}

	/**
	 * Gets the HTTP session
	 * 
	 * @param create
	 *            Specifies whether the session is created if it does not exist
	 * @return HttpSession
	 */
	public static HttpSession getSession(boolean create) {

		return (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(create);

	}

	/**
	 * Gets or creates an HTTP session
	 * 
	 * @return HttpSession
	 */
	public static HttpSession getSession() {
		return (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
	}

	/**
	 * Get the FacesContext
	 * 
	 * @return FacesContext
	 */
	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	/**
	 * Get the ApplicationContext
	 * 
	 * @return FacesContext
	 */
	public static Application getApplication() {
		return getFacesContext().getApplication();
	}

	/**
	 * Get the Response object
	 * 
	 * @return HttpServletResponse
	 */
	public static HttpServletResponse getResponse() {
		return (HttpServletResponse) getFacesContext().getExternalContext()
				.getResponse();
	}

	/**
	 * Get the Request object
	 * 
	 * @return HttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) getFacesContext().getExternalContext()
				.getRequest();
	}

	/**
	 * Add a Cookie
	 * 
	 * @param cookie
	 *            Objet Cookie to add
	 */
	public static void addCookie(Cookie cookie) {
		getResponse().addCookie(cookie);
	}

	/**
	 * Add a Cookie
	 * 
	 * @param name
	 *            cookie name
	 * @param value
	 *            cookie value
	 */
	public static void addCookie(String name, String value, int expiry) {

		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(expiry);
		cookie.setPath("");
		getResponse().addCookie(cookie);
	}

	/**
	 * Get all cookies
	 * 
	 * @return Cookie []
	 */
	public static Cookie[] getCookies() {
		return getRequest().getCookies();
	}

	/**
	 * Gets an specific cookie
	 * 
	 * @param cookieName
	 *            Nombre de la cookie
	 * @return Cookie
	 */
	public static Cookie getCookie(String cookieName) {
		Cookie[] cookies = getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equalsIgnoreCase(cookieName)) {
					return cookie;
				}
			}
		}
		return null;
	}

	/**
	 * Get session attribute
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return Object attribute value
	 */
	public static Object getSessionAttribute(String name) {
		return getSession().getAttribute(name);
	}

	public static void setSessionAttribute(String name, Object value) {
		getSession().setAttribute(name, value);
	}

	public static void removeSessionAttribute(String name) {
		getSession().removeAttribute(name);
	}

	public static Object getEventAttribute(ActionEvent event, Object att) {
		return event.getComponent().getAttributes().get(att);
	}

	public static ServletContext getServletContext() {
		return (ServletContext) getFacesContext().getExternalContext()
				.getContext();
	}

	public static String getRealPath() {

		return getServletContext().getRealPath("/");
	}

	public static String getPath() {
		return getServletContext().getContextPath();
	}

}