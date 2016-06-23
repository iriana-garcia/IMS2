package com.ghw.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;

import com.ghw.service.LogSystemService;
import com.ghw.service.UserService;
import com.ghw.util.Context;

//@ManagedBean(name = "loginBean")
//@RequestScoped
public class LoginBean1 implements PhaseListener {

	private String name;
	private String password;

	public static final String bdProperties = "file:///C:\\db.properties";

	public static String getBdproperties() {
		return bdProperties;
	}

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	@ManagedProperty(value = "#{logSystemService}")
	private LogSystemService logSystemService;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	private StreamedContent file;

	private boolean appBlocked;

	/**
	 * Get the PDF in the login page
	 * 
	 * @return
	 */
	public StreamedContent getFile() {
		try {

			InputStream stream = ((ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext())
					.getResourceAsStream("/resources/password_reset.pdf");
			file = new DefaultStreamedContent(stream, "pdf",
					"VPN_password_reset.pdf");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return file;
	}

	public String login() {
		try {

			// Authentication result =
			// authenticationManager.authenticate(request);
			//
			// SecurityContextHolder.getContext().setAuthentication(result);
			//
			// List<Object> list = sessionRegistry.getAllPrincipals();
			//
			// for (Object object : list) {
			// System.out.println(object);
			// }

		} catch (AuthenticationException e) {
			// Context.addErrorMessageFromMSG(e.getMessage());
			// // save in the cookie failed login to deactivate the user after 5
			// String cName = "usr"
			// + (StringUtils.isNotBlank(name) ? name.trim().replace(" ",
			// "@@") : "");
			//
			// if (e.getMessage() != null
			// && e.getMessage().equals("bad_credential")) {
			//
			// Cookie c = Context.getCookie(cName);
			//
			// if (c == null) {
			// Context.addCookie(cName, Integer.toString(1),
			// SessionBean.session_expire);
			// } else {
			// Integer n = Integer.parseInt(c.getValue()) + 1;
			//
			// c.setValue(n.toString());
			// Context.addCookie(c);
			// }
			//
			// }
			// SessionBean.addIntentosFallidos(cName);
			// try {
			// userService.registerLoginError(name, Context.getIp(), cName);
			// } catch (Exception e2) {
			// }

			return null;
		}
		return sessionBean.getUser().isGoToBank() ? "bank" : "default";
	}

	//
	// public boolean isAppBlocked() {
	//
	// appBlocked = SessionBean.isBloqueada();
	// return appBlocked;
	// }
	//
	// public void setAppBlocked(boolean appBlocked) {
	// this.appBlocked = appBlocked;
	// }

	public String cancel() {
		return null;
	}

	public String logout() {
		SecurityContextHolder.clearContext();

		logSystemService.insertLogout(sessionBean.getUser());

		Context.getSession().invalidate();

		sessionBean.clear();

		return "loggedout";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public LogSystemService getLogSystemService() {
		return logSystemService;
	}

	public void setLogSystemService(LogSystemService logSystemService) {
		this.logSystemService = logSystemService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setFile(StreamedContent file) {

		this.file = file;
	}

	@Override
	public void afterPhase(PhaseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforePhase(PhaseEvent event) {
		Exception e = (Exception) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap()
				.get(WebAttributes.AUTHENTICATION_EXCEPTION);

		if (e instanceof BadCredentialsException) {

			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap()
					.put(WebAttributes.AUTHENTICATION_EXCEPTION, null);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Username or password not valid.",
							"Username or password not valid"));
		}

	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

	/**
	 * 
	 * Redirects the login request directly to spring security check. Leave this
	 * method as it is to properly support spring security.
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String doLogin() throws ServletException, IOException {
		ExternalContext context = FacesContext.getCurrentInstance()
				.getExternalContext();

//		RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
//				.getRequestDispatcher("/j_spring_security_check");
//
//		dispatcher.forward((ServletRequest) context.getRequest(),
//				(ServletResponse) context.getResponse());
//
//		FacesContext.getCurrentInstance().responseComplete();
		
//		FacesUtils.getExternalContext().dispatch("/j_spring_security_check");
//	    FacesUtils.getFacesContext().responseComplete();

		return null;
	}

}