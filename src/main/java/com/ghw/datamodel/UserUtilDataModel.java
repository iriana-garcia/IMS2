package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.springframework.beans.factory.annotation.Autowired;

import com.ghw.filter.FilterBase;
import com.ghw.model.UserUtil;
import com.ghw.service.UserUtilService;

@ManagedBean
@ViewScoped
public class UserUtilDataModel extends
		GenericDataModel<String, UserUtil, UserUtilService> {

	@ManagedProperty(value = "#{userUtilService}")
	private UserUtilService service;

	@ManagedProperty(value = "#{filterBase}")
	private FilterBase filterBase;

	public UserUtilService getService() {
		return service;
	}

	
	public void setService(UserUtilService service) {
		this.service = service;
		super.setService(service);
	}

	@Override
	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = filterBase;
		super.setFilterBase(filterBase);

	}
	@Override
	public FilterBase getFilterBase() {
		return filterBase;
	}

}
