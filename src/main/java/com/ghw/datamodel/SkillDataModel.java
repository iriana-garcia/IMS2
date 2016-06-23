package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.model.Skill;
import com.ghw.service.SkillService;

@ManagedBean
@ViewScoped
public class SkillDataModel extends
		GenericDataModel<String, Skill, SkillService> {

	@ManagedProperty(value = "#{skillService}")
	private SkillService service;

	@ManagedProperty(value = "#{filterBase}")
	private FilterBase filterBase;

	public SkillService getService() {
		return service;
	}

	public void setService(SkillService service) {
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
