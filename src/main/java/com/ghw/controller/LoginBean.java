package com.ghw.controller;

import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ghw.service.LogSystemService;
import com.ghw.service.UserService;
import com.ghw.util.Context;

@ManagedBean(name = "loginBean")
@RequestScoped
public class LoginBean {

	private String name;
	private String password;

	public static final String bdProperties = "file:///C:\\db.properties";

	public static String getBdproperties() {
		return bdProperties;
	}

	@ManagedProperty(value = "#{authenticationManager}")
	private AuthenticationManager authenticationManager = null;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	@ManagedProperty(value = "#{logSystemService}")
	private LogSystemService logSystemService;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	private StreamedContent file;

	private boolean appBlocked;

	@PostConstruct
	public void init() {

		try {

			Exception e = (Exception) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap()
					.get("SPRING_SECURITY_LAST_EXCEPTION");

			// get the parameter
			String error = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap().get("error");

			if (error != null && e != null) {

				name = (String) Context
						.getSessionAttribute("LAST_USERNAME_KEY");

				Context.removeSessionAttribute("LAST_USERNAME_KEY");

				Context.addErrorMessageFromMSG(e.getMessage());
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

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

	public void login() {
		try {

			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			RequestDispatcher dispatcher = ((ServletRequest) context
					.getRequest()).getRequestDispatcher("/login");

			dispatcher.forward((ServletRequest) context.getRequest(),
					(ServletResponse) context.getResponse());
			FacesContext.getCurrentInstance().responseComplete();

			// Authentication request = new UsernamePasswordAuthenticationToken(
			// getName(), getPassword());
			// Authentication result =
			// authenticationManager.authenticate(request);
			// SecurityContextHolder.getContext().setAuthentication(result);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			// return null;
		}
		// return sessionBean.getUser().isGoToBank() ? "bank" : "default";
	}


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

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
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

}