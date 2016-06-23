package com.ghw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

import com.ghw.datamodel.SkillPhoneSystemDataModel;
import com.ghw.model.SkillPhoneSystem;
import com.ghw.model.TypeSkill;
import com.ghw.service.SkillPhoneSystemService;
import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class SkillPhoneSystemController extends
		Controller<SkillPhoneSystem, String, SkillPhoneSystemService> {

	@ManagedProperty(value = "#{skillPhoneSystemDataModel}")
	private SkillPhoneSystemDataModel lazyModel;

	private SkillPhoneSystem entity;

	@ManagedProperty(value = "#{skillPhoneSystemService}")
	private SkillPhoneSystemService service;

	private List<Object[]> listTypeSkill = new ArrayList<Object[]>();

	@PostConstruct
	public void init() {
		initFilter();
	}

	/**
	 * Clear the datatable filter
	 */
	@Override
	public void clearFilter() {

		// super.clearFilter();

		SelectOneMenu selectOne = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbError");
		if (selectOne != null) {
			selectOne.resetValue();
		}

		SelectOneMenu selectOneType = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbType");
		if (selectOneType != null) {
			selectOneType.resetValue();
		}

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').clearFilters()");
	}

	@Override
	public SkillPhoneSystemService getService() {
		return service;
	}

	public void setService(SkillPhoneSystemService service) {
		this.service = service;
	}

	public void onRowEdit(RowEditEvent event) {

		try {

			entity = (SkillPhoneSystem) event.getObject();

			service.merge(entity);

			FacesMessage msg = new FacesMessage("Skill Edited");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
		}

	}

	public void onRowCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Edit Cancelled");
		FacesContext.getCurrentInstance().addMessage(null, msg);

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').filter()");
	}

	public SkillPhoneSystem getEntity() {
		if (entity == null) {
			entity = new SkillPhoneSystem();
		}
		return entity;
	}

	public void setEntity(SkillPhoneSystem entity) {
		this.entity = entity;
	}

	public SkillPhoneSystemDataModel getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(SkillPhoneSystemDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public List<Object[]> getListTypeSkill() {
		if (listTypeSkill == null || listTypeSkill.size() == 0) {
			listTypeSkill = TypeSkill.getList();
		}
		return listTypeSkill;
	}

	public void setListTypeSkill(List<Object[]> listTypeSkill) {
		this.listTypeSkill = listTypeSkill;
	}

}
