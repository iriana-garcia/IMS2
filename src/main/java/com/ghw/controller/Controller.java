package com.ghw.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;

import com.ghw.filter.FilterBase;
import com.ghw.service.Service;
import com.ghw.util.Context;
import com.ghw.util.EntityConverter;

/**
 * Commom class for the Controller
 * 
 * @author ifernandez
 * 
 * @param <T>
 * @param <D>
 * @param <S>
 */
public class Controller<T, D extends Serializable, S extends Service<T, D>>
		implements Serializable {

	protected T entity;

	protected S service;

	// save if is editing or adding
	protected boolean edit = false;

	@ManagedProperty(value = "#{objectUtil}")
	protected ObjectUtil objectUtil;

	// save if the entity can be deactivate in the Edit page
	protected boolean deactiveState = false;

	@ManagedProperty(value = "#{filterBase}")
	protected FilterBase filterBase;

	public List<T> listOrder;
	public List<T> listActive;

	@ManagedProperty(value = "#{converter}")
	public EntityConverter converter;

	/**
	 * Init the filter value
	 */
	protected void initFilter() {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put("state", true);

		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent("form:uniTable");

		if (dataTable != null) {
			dataTable.setFilters(filters);
		}
		SelectOneMenu selectOne = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbError");
		if (selectOne != null) {
			selectOne.setValue(true);
		}
	}

	/**
	 * clear the datatable filter
	 */
	public void clearFilter() {

		try {

			RequestContext.getCurrentInstance().execute(
					"PF('uniTableFil').clearFilters()");

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Go to the add page
	 * 
	 * @return
	 */
	public String add() {
		try {

			// clean the entities
			EntityConverter.entities = new WeakHashMap<Object, String>();

			objectUtil.setEdit(1);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "add";
	}

	/**
	 * Go to edit page
	 * 
	 * @return
	 */
	public String edit() {
		try {

			// clean the entities
			EntityConverter.entities = new WeakHashMap<Object, String>();

			objectUtil.setEdit(2);
			objectUtil.setObject(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "edit";
	}

	/**
	 * Fill the p:selectOneMenu, get the data ordered
	 * 
	 * @return
	 */
	public List<T> getListOrder() {

		if (listOrder == null) {
			listOrder = new ArrayList<T>();
		}

		try {
			if (listOrder == null || listOrder.size() == 0) {
				listOrder = getService().getDataOrder();
			}
		} catch (Exception e) {

			Context.addErrorMessageFromMSG(e.getMessage());
		}

		return listOrder;
	}

	/**
	 * Fill the p:selectOneMenu, get the data ordered and active
	 * 
	 * @return
	 */
	public List<T> getListActive() {

		if (listActive == null) {
			listActive = new ArrayList<T>();
		}

		try {
			if (listActive == null || listActive.size() == 0) {
				listActive = getService().getDataActive();
			}
		} catch (Exception e) {

			Context.addErrorMessageFromMSG(e.getMessage());
		}

		return listActive;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public ObjectUtil getObjectUtil() {
		return objectUtil;
	}

	public void setObjectUtil(ObjectUtil objectUtil) {
		this.objectUtil = objectUtil;
	}

	public boolean isDeactiveState() {
		return deactiveState;
	}

	public void setDeactiveState(boolean deactiveState) {
		this.deactiveState = deactiveState;
	}

	public FilterBase getFilterBase() {
		return filterBase;
	}

	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = filterBase;
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public S getService() {
		return service;
	}

	public void setService(S service) {
		this.service = service;
	}

	public EntityConverter getConverter() {
		return converter;
	}

	public void setConverter(EntityConverter converter) {
		this.converter = converter;
	}

}
