package com.ghw.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import com.ghw.model.ConfigurationSystem;
import com.ghw.model.DayWeek;
import com.ghw.model.InvoiceFrequency;
import com.ghw.model.TypeYear;
import com.ghw.service.ConfigurationSystemService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class ConfigurationSystemController implements Serializable {

	private ConfigurationSystem entity;

	@ManagedProperty(value = "#{configurationSystemService}")
	private ConfigurationSystemService service;

	private List<SelectItem> listTypeYears;
	private List<SelectItem> listInvoiceFrecuency;
	private List<SelectItem> listDayWeek;

	@ManagedProperty(value = "#{applicationBean}")
	private ApplicationBean applicationBean;

	@PostConstruct
	public void init() {
		try {

			List<ConfigurationSystem> list = service.getData();
			entity = list != null && list.size() > 0 ? list.get(0) : null;

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	public String edit() {
		try {

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
		return "edit";
	}

	public String update() {
		try {

			if (StringUtils.isBlank(entity.getId())) {
				service.makePersistent(entity);
			} else {
				service.update(entity);
			}

			applicationBean.setSystemConf(entity);

		} catch (SystemException e) {
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "system_configuration";
	}

	public ConfigurationSystem getEntity() {
		if (entity == null) {
			entity = new ConfigurationSystem();
		}
		return entity;
	}

	public void setEntity(ConfigurationSystem entity) {
		this.entity = entity;
	}

	public ConfigurationSystemService getService() {
		return service;
	}

	public void setService(ConfigurationSystemService service) {
		this.service = service;
	}

	/**
	 * fill the type years picklist
	 * 
	 * @return
	 */
	public List<SelectItem> getListTypeYears() {
		if (listTypeYears == null) {
			listTypeYears = new ArrayList<SelectItem>();
			for (TypeYear t : TypeYear.values()) {
				listTypeYears.add(new SelectItem(t, t.getValor()));

			}
		}
		return listTypeYears;
	}

	public void setListTypeYears(List<SelectItem> listTypeYears) {
		this.listTypeYears = listTypeYears;
	}

	/**
	 * fill the invoice frecuency picklist
	 * 
	 * @return
	 */
	public List<SelectItem> getListInvoiceFrecuency() {

		if (listInvoiceFrecuency == null) {
			listInvoiceFrecuency = new ArrayList<SelectItem>();
			for (InvoiceFrequency t : InvoiceFrequency.values()) {
				listInvoiceFrecuency.add(new SelectItem(t, t.getValor()));

			}
		}
		return listInvoiceFrecuency;
	}

	public void setListInvoiceFrecuency(List<SelectItem> listInvoiceFrecuency) {
		this.listInvoiceFrecuency = listInvoiceFrecuency;
	}

	public List<SelectItem> getListDayWeek() {

		if (listDayWeek == null) {
			listDayWeek = new ArrayList<SelectItem>();
			for (DayWeek t : DayWeek.values()) {
				listDayWeek.add(new SelectItem(t, t.getValor()));

			}
		}
		return listDayWeek;
	}

	public void setListDayWeek(List<SelectItem> listDayWeek) {
		this.listDayWeek = listDayWeek;
	}

	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

}
