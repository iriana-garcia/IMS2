package com.ghw.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.ghw.model.ConfigurationEmail;
import com.ghw.service.ConfigurationEmailService;
import com.ghw.service.EmailService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class ConfigurationEmailController implements Serializable {

	private ConfigurationEmail entityWelcome;

	private ConfigurationEmail entityInvoice;

	private ConfigurationEmail entityFinance;

	@ManagedProperty(value = "#{configurationEmailService}")
	private ConfigurationEmailService service;

	private List<String[]> keys = new ArrayList<String[]>();

	@ManagedProperty(value = "#{emailService}")
	private EmailService emailService;

	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@PostConstruct
	public void init() {
		try {

			List<ConfigurationEmail> list = service.getData();
			for (ConfigurationEmail ce : list) {
				switch (ce.getType()) {
				case "W":
					entityWelcome = ce;
					break;
				case "I":
					entityInvoice = ce;
					break;
				case "F":
					entityFinance = ce;
					break;
				}
			}
			keys.add(new String[] { "@@userid", "For user ID" });
			keys.add(new String[] { "@@username", "For user name" });
			keys.add(new String[] { "@@firstname", "For first name" });
			keys.add(new String[] { "@@middlename", "For middle name" });
			keys.add(new String[] { "@@lastname", "For last name" });
			keys.add(new String[] { "@@companyname", "For company name" });
			keys.add(new String[] { "@@linksystem",
					"For add a link to the system" });
			keys.add(new String[] { "@@paydateinvoice", "For add a pay date " });
			keys.add(new String[] { "@@invoicenumber",
					"For add an invoice number" });
			keys.add(new String[] { "@@invoiceImporttotal",
					"For add the invoice's total" });

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	public String edit() {
		try {

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "edit";
	}

	public String update() {
		try {

			service.saveOrUpdate(entityWelcome, entityInvoice, entityFinance);

			// } catch (SystemException e) {
			// Context.addErrorMessageFromMSG(e);
			// FacesContext.getCurrentInstance().validationFailed();
			// return null;
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "email_configuration";
	}

	public ConfigurationEmail getEntityWelcome() {
		if (entityWelcome == null) {
			entityWelcome = new ConfigurationEmail();
		}
		return entityWelcome;
	}

	public void setEntityWelcome(ConfigurationEmail entityWelcome) {
		this.entityWelcome = entityWelcome;
	}

	public ConfigurationEmail getEntityInvoice() {
		if (entityInvoice == null) {
			entityInvoice = new ConfigurationEmail();
		}
		return entityInvoice;
	}

	public void setEntityInvoice(ConfigurationEmail entityInvoice) {
		this.entityInvoice = entityInvoice;
	}

	public ConfigurationEmail getEntityFinance() {
		if (entityFinance == null) {
			entityFinance = new ConfigurationEmail();
		}
		return entityFinance;
	}

	public void setEntityFinance(ConfigurationEmail entityFinance) {
		this.entityFinance = entityFinance;
	}

	public ConfigurationEmailService getService() {
		return service;
	}

	public void setService(ConfigurationEmailService service) {
		this.service = service;
	}

	public List<String[]> getKeys() {
		return keys;
	}

	public void setKeys(List<String[]> keys) {
		this.keys = keys;
	}

}
