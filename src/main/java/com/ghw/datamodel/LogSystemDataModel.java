package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.filter.LogSystemFilter;
import com.ghw.model.LogSystem;
import com.ghw.service.LogSystemService;

@ManagedBean
@ViewScoped
public class LogSystemDataModel extends
		GenericDataModel<String, LogSystem, LogSystemService> {

	@ManagedProperty(value = "#{logSystemService}")
	private LogSystemService service;

	@ManagedProperty(value = "#{logSystemFilter}")
	private LogSystemFilter filterBase;

	public LogSystemService getService() {
		return service;
	}

	public void setService(LogSystemService service) {
		this.service = service;
		super.setService(service);
	}

	@Override
	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = (LogSystemFilter) filterBase;
		super.setFilterBase(filterBase);

	}

	@Override
	public FilterBase getFilterBase() {
		return filterBase;
	}

}
