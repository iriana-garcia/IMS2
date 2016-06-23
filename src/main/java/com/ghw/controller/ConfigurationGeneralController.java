package com.ghw.controller;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.focus.Focus;
import org.primefaces.context.RequestContext;

import com.ghw.model.ConfigurationGeneral;
import com.ghw.report.service.OracleService;
import com.ghw.service.ConfigurationGeneralService;
import com.ghw.service.EmailService;
import com.ghw.util.ActiveDirectory;
import com.ghw.util.Context;
import com.ghw.util.FTPUtil;
import com.ghw.util.IboUtil;
import com.ghw.util.RestUtil;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class ConfigurationGeneralController implements Serializable {

	private ConfigurationGeneral entity;

	@ManagedProperty(value = "#{configurationGeneralService}")
	private ConfigurationGeneralService service;

	@ManagedProperty(value = "#{emailService}")
	private EmailService emailService;

	@ManagedProperty(value = "#{applicationBean}")
	private ApplicationBean applicationBean;

	@ManagedProperty(value = "#{restUtil}")
	private RestUtil restUtil;

	private Focus focus;

	@PostConstruct
	public void init() {
		try {

			entity = service.getDataConfiguration();

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	public String edit() {
		try {

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
		return "edit";
	}

	private static String removeSemiColon(String dn) {
		if (dn.endsWith(";")) {
			dn = dn.substring(0, dn.length() - 1);
		}
		if (dn.startsWith(";")) {
			dn = dn.substring(1, dn.length());
		}
		return dn.trim();
	}

	public Focus getFocus() {
		return focus;
	}

	public void setFocus(Focus focus) {
		this.focus = focus;
	}

	public String update() {
		ActiveDirectory activeDirectory = new ActiveDirectory();

		// validate the ldap configuration
		try {

			List<SystemException> e = new ArrayList<SystemException>();

			// remove all the semicolon begin and finish
			entity.setLdapDn(removeSemiColon(entity.getLdapDn()));
			entity.setLdapDnIbo(removeSemiColon(entity.getLdapDnIbo()));
			entity.setLdapDnIdc(removeSemiColon(entity.getLdapDnIdc()));
			entity.setLdapDnInternational(removeSemiColon(entity
					.getLdapDnInternational()));
			entity.setLdapDnSme(removeSemiColon(entity.getLdapDnSme()));

			// the DN base can not contain semicolon
			if (entity.getLdapDn().contains(";")) {
				e.add(new SystemException("ldap_base_dn_semicolon",
						"form:tabConf:txtBaseDn"));
			}
			// the DN can't repeated
			Set<String> mapDn = new LinkedHashSet<String>();
			Set<String> repeated = new LinkedHashSet<String>();

			// DN General
			mapDn.add(entity.getLdapDn().toLowerCase());
			// DN IBO
			List<String> dnIbo = IboUtil.getAllDN(entity.getLdapDnIbo());
			String lc;
			for (String s : dnIbo) {
				lc = s.toLowerCase();
				if (mapDn.contains(lc)) {
					repeated.add(s);
				} else {
					mapDn.add(lc);
				}
			}

			// DN IBO International New
			List<String> dnIboInternatN = IboUtil.getAllDN(entity
					.getLdapDnIdc());
			for (String s : dnIboInternatN) {
				lc = s.toLowerCase();
				if (mapDn.contains(lc)) {
					repeated.add(s);
				} else {
					mapDn.add(lc);
				}
			}

			// DN IBO International
			List<String> dnIboInternat = IboUtil.getAllDN(entity
					.getLdapDnInternational());
			for (String s : dnIboInternat) {
				lc = s.toLowerCase();
				if (mapDn.contains(lc)) {
					repeated.add(s);
				} else {
					mapDn.add(lc);
				}
			}
			// DN IBO PA
			List<String> dnPa = IboUtil.getAllDN(entity.getLdapDnSme());
			for (String s : dnPa) {
				lc = s.toLowerCase();
				if (mapDn.contains(lc)) {
					repeated.add(s);
				} else {
					mapDn.add(lc);
				}
			}

			if (repeated.size() > 0) {
				String stringRepeated = "";
				for (String s : repeated) {
					stringRepeated += s + ";";
				}

				e.add(new SystemException("DN can't be repeated: ["
						+ stringRepeated.substring(0,
								stringRepeated.length() - 1) + "]"));

			}

			if (e.size() > 0) {
				throw new SystemException(e);
			}
			// fist test AD connection
			activeDirectory.testConnection(entity);

		} catch (SystemException e) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(0);");
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		} catch (CommunicationException ce) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(0);");
			String msg = Context.getUIMsg("error_connection_ldap") + " ["
					+ ce.getRootCause() + "]" + " ."
					+ Context.getUIMsg("helpdesk_help");
			Context.addMessage(msg, FacesMessage.SEVERITY_ERROR);

			return null;
		} catch (NamingException ne) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(0);");
			String msg = Context.getUIMsg("error_connection_ldap")
					+ (ne.getRootCause() == null ? " " + ne.getMessage() : " ["
							+ ne.getRootCause() + "]") + " ."
					+ Context.getUIMsg("helpdesk_help");
			Context.addMessage(msg, FacesMessage.SEVERITY_ERROR);
			// Context.addErrorMessageFromMSG("naming_exception");

			return null;
		} catch (Exception e) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(0);");
			Context.addErrorMessageFromMSG(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		}

		// validation email configuration
		try {

			// validate email connection
			emailService.confirmSMTP(entity);

		} catch (SystemException e) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(1);");
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		} catch (AuthenticationNotSupportedException an) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(1);");
			Context.addErrorMessageFromMSG("authentication_not_supported_exception");
			return null;
		} catch (AuthenticationFailedException e) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(1);");
			String msg = Context.getUIMsg("email_authentication_invalid")
					+ (" [" + e.getMessage() + "]");
			Context.addMessage(msg, FacesMessage.SEVERITY_ERROR);

			return null;

		} catch (MessagingException e) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(1);");
			String msg = Context.getUIMsg("email_configuration_invalid")
					+ (" [" + e.getMessage() + "]");
			Context.addMessage(msg, FacesMessage.SEVERITY_ERROR);

			return null;
		} catch (Exception e) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(1);");
			Context.addErrorMessageFromMSG(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		}

		// validation FTP configuration
		try {

			FTPUtil ftpUtil = new FTPUtil();
			ftpUtil.validateConnectFTP(entity);

		} catch (UnknownHostException e) {
			Context.addErrorMessageFromMSG(e.getMessage(),
					"form:tabConf:txtFtpOra");
			RequestContext.getCurrentInstance().execute(
					"PF('txtFtpOraW').focus();");
			return null;
		} catch (Exception e) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(2);");
			focus.setContext("form:tabConf:txtFtpOra");
			focus.setFor("form:tabConf:txtFtpOra");
			Context.addErrorMessageFromMSG(e.getMessage(),
					"form:tabConf:txtFtpOra");

			return null;
		}

		// validate connection with web services
		try {

			// validate connection with web services
			String code = restUtil.isRunningWebService(entity.getWebservice());

			if (code != null) {
				throw new SystemException("WebService URL incorrect [" + code
						+ "].", "form:tabConf:txtUrlWebService");
			}
		} catch (SystemException e) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(2);");
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		} catch (Exception e) {
			RequestContext.getCurrentInstance().execute(
					"PF('tabConfW').select(2);");
			Context.addErrorMessageFromMSG(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		}

		// if all ok insert or update
		try {

			if (StringUtils.isBlank(entity.getId())) {
				service.makePersistent(entity);
			} else
				service.update(entity);

			applicationBean.setConfiguration(entity);

			// } catch (AccessDeniedException an) {
			// Context.addErrorMessageFromMSG("permission_oracle",
			// "form:tabConf:txtUserFtp");
			// RequestContext.getCurrentInstance().execute(
			// "PF('txtUserFtpW').focus();");
			// return null;
			// } catch (InvalidPathException an) {
			// Context.addErrorMessageFromMSG("path_oracle");
			// return null;
			// } catch (SecurityException an) {
			// Context.addErrorMessageFromMSG("permission_oracle");
			// return null;
			// } catch (UnknownHostException an) {
			// Context.addErrorMessageFromMSG("host_incorrect");

			// return null;

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

			return null;
		}
		return "general_configuration";
	}

	public ConfigurationGeneral getEntity() {
		if (entity == null) {
			entity = new ConfigurationGeneral();
		}
		return entity;
	}

	public ConfigurationGeneralService getService() {
		return service;
	}

	public void setService(ConfigurationGeneralService service) {
		this.service = service;
	}

	public void setEntity(ConfigurationGeneral entity) {
		this.entity = entity;
	}

	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {

		this.emailService = emailService;
	}

	public RestUtil getRestUtil() {
		return restUtil;
	}

	public void setRestUtil(RestUtil restUtil) {
		this.restUtil = restUtil;
	}

	// ***********delete this is a test
	@ManagedProperty(value = "#{oracleService}")
	private OracleService oracleService;

	public OracleService getOracleService() {
		return oracleService;
	}

	public void setOracleService(OracleService oracleService) {
		this.oracleService = oracleService;
	}

	public void testSupplier() {
		oracleService.createSupplierFiles(null);
	}

}
