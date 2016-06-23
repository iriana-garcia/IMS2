package com.ghw.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import com.ghw.model.OwnerType;
import com.ghw.model.PayMethod;
import com.ghw.model.User;
import com.ghw.service.UserService;
import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class MyAccountController implements Serializable {

	private User entity;

	@ManagedProperty(value = "#{userService}")
	private UserService service;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	private String oldPassword;

	@PostConstruct
	public void init() {
		try {

			entity = service.loadAllById(sessionBean.getUser());

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	public boolean isShowBank() {
		return entity != null
				&& entity.getIbo() != null
				&& entity.getIbo().getPayMethod()
						.equals(PayMethod.DIRECT_DEPOSIT)
				&& entity.getIbo().getOwnerType() != null
				&& entity.getIbo().getOwnerType().equals(OwnerType.PRINCIPAL) ? true
				: false;
	}

	/**
	 * Return if the user is an IBO, is an IBO when user type is 2
	 * 
	 * @return
	 */
	public boolean isIbo() {
		return (entity != null && entity.getType() != null
				&& StringUtils.isNotBlank(entity.getType().getId()) ? entity
				.getType().getId().equals("2") : false);
	}

	/**
	 * Return if the user is an PA, is an IBO when user type is 3
	 * 
	 * @return
	 */
	public boolean isPa() {
		return (entity != null && entity.getType() != null
				&& StringUtils.isNotBlank(entity.getType().getId()) ? entity
				.getType().getId().equals("3") : false);
	}

	public String editBank() {
		try {

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "edit";
	}

	public String updatePassword() {
		try {

			service.updatePassword(entity, oldPassword);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return null;
	}

	public User getEntity() {
		return entity;
	}

	public void setEntity(User entity) {
		this.entity = entity;
	}

	public UserService getService() {
		return service;
	}

	public void setService(UserService service) {
		this.service = service;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

}
