package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.model.Rol;
import com.ghw.service.RolService;

@ManagedBean
@ViewScoped
public class RolDataModel extends GenericDataModel<String, Rol, RolService> {

	@ManagedProperty(value = "#{rolService}")
	private RolService service;
	
	@ManagedProperty(value = "#{filterBase}")
	private FilterBase filterBase;

	public RolService getService() {
		return service;
	}

	public void setService(RolService service) {
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
