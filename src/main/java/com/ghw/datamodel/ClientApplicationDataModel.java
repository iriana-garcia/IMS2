package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.model.ClientApplication;
import com.ghw.service.ClientApplicationService;

@ManagedBean
@ViewScoped
public class ClientApplicationDataModel extends
		GenericDataModel<String, ClientApplication, ClientApplicationService> {

	@ManagedProperty(value = "#{clientApplicationService}")
	private ClientApplicationService service;

	@ManagedProperty(value = "#{filterBase}")
	private FilterBase filterBase;

	public ClientApplicationService getService() {
		return service;
	}

	public void setService(ClientApplicationService service) {
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
