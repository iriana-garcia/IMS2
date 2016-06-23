package com.ghw.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.ghw.model.BankInformation;
import com.ghw.service.BankInformationService;
import com.ghw.service.RoutingNumbersService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class BankAccountController implements Serializable {

	private BankInformation entity;

	@ManagedProperty(value = "#{bankInformationService}")
	private BankInformationService service;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	@ManagedProperty(value = "#{routingNumbersService}")
	private RoutingNumbersService routingNumbersService;

	public String update() {
		try {

			// first search if exist the account number
			// if not show warning message
			if (routingNumbersService.validateIfExistRoutingNumber(entity
					.getRouting())) {
				saveOrUpdate();
			} else {
				RequestContext.getCurrentInstance().execute(
						"PF('dlgBankNotFound').show()");
				return null;
			}

		} catch (SystemException e) {
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "myaccount";
	}

	private void saveOrUpdate() throws Exception {
		service.saveOrUpdate(entity, sessionBean.getUser());
	}

	public String aceptBankInformation() {

		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			saveOrUpdate();

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}

		return "myaccount";
	}

	public BankInformation getEntity() {
		if (entity == null) {
			entity = new BankInformation();
		}
		return entity;
	}

	public void setEntity(BankInformation entity) {
		this.entity = entity;
	}

	public BankInformationService getService() {
		return service;
	}

	public void setService(BankInformationService service) {
		this.service = service;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public RoutingNumbersService getRoutingNumbersService() {
		return routingNumbersService;
	}

	public void setRoutingNumbersService(
			RoutingNumbersService routingNumbersService) {
		this.routingNumbersService = routingNumbersService;
	}

}
