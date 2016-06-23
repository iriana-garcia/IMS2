package com.ghw.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.model.Profile;
import com.ghw.service.ProfileService;

@ViewScoped
@ManagedBean
public class ProfileController implements Serializable {

	@ManagedProperty(value = "#{profileService}")
	private ProfileService service;

	// used to fill selectOneMenu with all the IBO active
	private List<Profile> listActiveIbo;

	public ProfileService getService() {
		return service;
	}

	public void setService(ProfileService service) {
		this.service = service;
	}

	public List<Profile> getListActiveIbo() {
		if (listActiveIbo == null) {

			listActiveIbo = service.getDataActiveIbo();
		}
		return listActiveIbo;
	}

	public void setListActiveIbo(List<Profile> listActiveIbo) {
		this.listActiveIbo = listActiveIbo;
	}

}
