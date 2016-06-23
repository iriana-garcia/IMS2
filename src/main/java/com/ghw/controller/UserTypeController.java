package com.ghw.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.ghw.model.UserType;
import com.ghw.service.UserTypeService;

@ManagedBean
@RequestScoped
public class UserTypeController extends
		Controller<UserType, String, UserTypeService> {

	@ManagedProperty(value = "#{userTypeService}")
	private UserTypeService service;

	@Override
	public UserTypeService getService() {
		return service;
	}

	public void setService(UserTypeService service) {
		this.service = service;
	}

}
