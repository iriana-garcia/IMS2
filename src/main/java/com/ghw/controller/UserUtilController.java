package com.ghw.controller;

import java.io.Serializable;
import java.util.WeakHashMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;

import com.ghw.datamodel.UserUtilDataModel;
import com.ghw.model.UserUtil;
import com.ghw.service.IboTemporalService;
import com.ghw.service.ProfileService;
import com.ghw.service.UserUtilService;
import com.ghw.util.Context;
import com.ghw.util.EntityConverter;

@ManagedBean
@ViewScoped
public class UserUtilController extends
		Controller<UserUtil, String, UserUtilService> implements Serializable {

	@ManagedProperty(value = "#{userUtilDataModel}")
	private UserUtilDataModel lazyModel;

	@ManagedProperty(value = "#{userUtilService}")
	private UserUtilService userUtilService;

	@ManagedProperty(value = "#{iboTemporalService}")
	private IboTemporalService iboTemporalService;

	@ManagedProperty(value = "#{profileService}")
	private ProfileService profileService;

	@ManagedProperty(value = "#{userUtilService}")
	private UserUtilService service;

	public UserUtilController() {

	}

	@PostConstruct
	public void init() {
		try {
			// if is the list page call the method the create the default filter
			if (objectUtil.getEdit() == null) {
				initFilter();
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

		}
	}

	/**
	 * add the temporal IBO, go to user_edit
	 */
	@Override
	public String add() {
		try {

			iboTemporalService.validateIfExistsAd(entity);

			// clean the entities
			EntityConverter.entities = new WeakHashMap<Object, String>();

			objectUtil.setEdit(4);
			objectUtil.setObject(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "add";
	}

	/**
	 * send a welcome email
	 */
	public void sendEmail() {
		try {

			boolean send = profileService.sendWelcomeEmail(entity.getId());

			FacesMessage msg = new FacesMessage(send ? "Welcome email sending"
					: "Error sending email", entity.getUserName());
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Change the state and save the date and the user that make the action
	 */
	public void changeState() {
		try {

			service.changeState(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * Clear the datatable filter
	 */
	public void clearFilter() {
		// super.clearFilter();

		SelectOneMenu selectOne = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbError");
		if (selectOne != null) {
			selectOne.resetValue();
		}

		SelectOneMenu selectOneStatus = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbSta");
		if (selectOneStatus != null) {
			selectOneStatus.resetValue();
		}

		SelectOneMenu selectOneBank = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbBank");
		if (selectOneBank != null) {
			selectOneBank.resetValue();
		}

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').clearFilters()");

	}

	public UserUtilDataModel getLazyModel() {
		return lazyModel;
	}

	public UserUtilService getUserUtilService() {
		return userUtilService;
	}

	public void setUserUtilService(UserUtilService userUtilService) {
		this.userUtilService = userUtilService;
	}

	public void setLazyModel(UserUtilDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public IboTemporalService getIboTemporalService() {
		return iboTemporalService;
	}

	public void setIboTemporalService(IboTemporalService iboTemporalService) {
		this.iboTemporalService = iboTemporalService;
	}

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	public UserUtilService getService() {
		return service;
	}

	public void setService(UserUtilService service) {
		this.service = service;
	}

}