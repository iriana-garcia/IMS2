package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.model.Corporation;
import com.ghw.service.CorporationService;

@ManagedBean
@ViewScoped
public class CorporationDataModel extends
		GenericDataModel<String, Corporation, CorporationService> {

	@ManagedProperty(value = "#{corporationService}")
	private CorporationService service;

	@ManagedProperty(value = "#{filterBase}")
	private FilterBase filterBase;

	public CorporationService getService() {
		return service;
	}

	public void setService(CorporationService service) {
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
