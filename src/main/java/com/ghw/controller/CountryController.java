package com.ghw.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;

import com.ghw.model.Country;
import com.ghw.service.CountryService;

@ManagedBean
@ViewScoped
public class CountryController extends
		Controller<Country, String, CountryService> {

	@ManagedProperty(value = "#{countryService}")
	private CountryService service;

	@Override
	public CountryService getService() {
		return service;
	}

	public void setService(CountryService service) {
		this.service = service;
	}

	@Override
	public Country getEntity() {
		if (entity == null) {
			entity = new Country();
		}
		return entity;
	}
}
