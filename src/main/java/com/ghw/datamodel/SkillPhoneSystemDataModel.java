package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.model.SkillPhoneSystem;
import com.ghw.service.SkillPhoneSystemService;

@ManagedBean
@ViewScoped
public class SkillPhoneSystemDataModel extends
		GenericDataModel<String, SkillPhoneSystem, SkillPhoneSystemService> {

	@ManagedProperty(value = "#{skillPhoneSystemService}")
	private SkillPhoneSystemService service;

	@ManagedProperty(value = "#{filterBase}")
	private FilterBase filterBase;

	public SkillPhoneSystemService getService() {
		return service;
	}

	public void setService(SkillPhoneSystemService service) {
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
