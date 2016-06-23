package com.ghw.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.ghw.model.IboState;
import com.ghw.service.IboStateService;

@ManagedBean
@RequestScoped
public class IboStateController extends
		Controller<IboState, String, IboStateService> {

	@ManagedProperty(value = "#{iboStateService}")
	private IboStateService service;

	@Override
	public IboStateService getService() {
		return service;
	}

	public void setService(IboStateService service) {
		this.service = service;
	}

}
