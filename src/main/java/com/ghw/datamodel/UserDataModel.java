package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.filter.UserFilter;
import com.ghw.model.User;
import com.ghw.service.UserService;

@ManagedBean
@ViewScoped
public class UserDataModel extends GenericDataModel<String, User, UserService> {

	@ManagedProperty(value = "#{userService}")
	private UserService service;

	@ManagedProperty(value = "#{userFilter}")
	private UserFilter filterBase;

	public UserService getService() {
		return service;
	}

	public void setService(UserService service) {
		this.service = service;
		super.setService(service);
	}

	@Override
	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = (UserFilter) filterBase;
		super.setFilterBase(filterBase);

	}

	@Override
	public FilterBase getFilterBase() {
		return filterBase;
	}

}
