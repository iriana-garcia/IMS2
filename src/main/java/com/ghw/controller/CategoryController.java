package com.ghw.controller;

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

import com.ghw.datamodel.CategoryDataModel;
import com.ghw.model.Category;
import com.ghw.service.CategoryService;
import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class CategoryController extends
		Controller<Category, String, CategoryService> {

	@ManagedProperty(value = "#{categoryDataModel}")
	private CategoryDataModel lazyModel;

	private Category entity;

	private List<Category> list;

	@ManagedProperty(value = "#{categoryService}")
	private CategoryService service;

	private Boolean state = true;

	@PostConstruct
	public void init() {
		initFilter();
	}

	private boolean prueba = true;

	public boolean isPrueba() {
		return prueba;
	}

	public void setPrueba(boolean prueba) {
		this.prueba = prueba;
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
		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').clearFilters()");
	}

	@Override
	public CategoryService getService() {
		return service;
	}

	public void setService(CategoryService service) {
		this.service = service;
	}

	public void onRowEdit(RowEditEvent event) {

		try {

			entity = (Category) event.getObject();

			service.merge(entity);

			FacesMessage msg = new FacesMessage("Category Edited");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
		}

	}

	/**
	 * Change the state and save the date and the user that make the action
	 */
	public void changeState() {
		try {

			service.changeState(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	public void saveCategory() {

		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			service.makePersistent(entity);

			list = null;

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}

	}

	public void cleanCategory() {

		entity = new Category();

	}

	public void onRowCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Edit Cancelled");
		FacesContext.getCurrentInstance().addMessage(null, msg);

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').filter()");
	}

	public Category getEntity() {
		if (entity == null) {
			entity = new Category();
		}
		return entity;
	}

	public void setEntity(Category entity) {
		this.entity = entity;
	}

	public List<Category> getList() {

		if (list == null) {
			list = service.getData();
		}
		return list;
	}

	public void setList(List<Category> list) {
		this.list = list;
	}

	public CategoryDataModel getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(CategoryDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

}
