package com.ghw.controller;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.ghw.model.IboType;
import com.ghw.service.IboTypeService;

@ManagedBean
@RequestScoped
public class IboTypeController extends
		Controller<IboType, String, IboTypeService> {

	@ManagedProperty(value = "#{iboTypeService}")
	private IboTypeService service;

	@Override
	public IboTypeService getService() {
		return service;
	}

	public void setService(IboTypeService service) {
		this.service = service;
	}
	

	public List<IboType> getListOrderType() {
		return super.getListOrder();
	}

}
