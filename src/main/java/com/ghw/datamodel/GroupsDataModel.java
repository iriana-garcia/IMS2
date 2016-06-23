package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ghw.filter.FilterBase;
import com.ghw.model.Groups;
import com.ghw.service.GroupsService;

@ManagedBean
@ViewScoped
public class GroupsDataModel extends
		GenericDataModel<String, Groups, GroupsService> {

	@ManagedProperty(value = "#{groupsService}")
	private GroupsService service;

	@ManagedProperty(value = "#{filterBase}")
	private FilterBase filterBase;

	public GroupsService getService() {
		return service;
	}

	public void setService(GroupsService service) {
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
