package com.ghw.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;

import com.ghw.datamodel.LogSystemDataModel;
import com.ghw.filter.LogSystemFilter;
import com.ghw.model.LogSystem;
import com.ghw.service.LogSystemService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class LogSystemController extends
		Controller<LogSystem, String, LogSystemService> {

	@ManagedProperty(value = "#{logSystemDataModel}")
	private LogSystemDataModel lazyModel;

	@ManagedProperty(value = "#{logSystemService}")
	private LogSystemService logSystemService;

	@ManagedProperty(value = "#{logSystemFilter}")
	private LogSystemFilter filter;

	public LogSystemController() {

	}

	private LogSystem entity;

	@PostConstruct
	public void init() {
		try {

			String request = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestServletPath();

			if (request != null && request.contains("pay_processed")) {

				Context.setSessionAttribute("idReportLogSystem", "2");
			}
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

		}
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

		cleanDetailFilter();

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').clearFilters()");

	}

	public void cleanDetailFilter() {

		filter.setEndDate(null);
		filter.setStartDate(null);
	}

	public void validateFilter() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			if (filter.getEndDate() != null && filter.getStartDate() != null
					&& filter.getEndDate().before(filter.getStartDate())) {
				throw new SystemException("start_end_date_incorrect");
			}

			if (filter.getEndDate() != null && filter.getStartDate() == null) {
				throw new SystemException("date_end_without_start");
			}

		} catch (SystemException e) {
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			error = true;
		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}
	}

	public LogSystemDataModel getLazyModel() {
		return lazyModel;
	}

	public LogSystemService getLogSystemService() {
		return logSystemService;
	}

	public void setLogSystemService(LogSystemService logSystemService) {
		this.logSystemService = logSystemService;
	}

	public void setLazyModel(LogSystemDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public LogSystemFilter getFilter() {
		return filter;
	}

	public void setFilter(LogSystemFilter filter) {
		this.filter = filter;
	}

	public LogSystem getEntity() {
		return entity;
	}

	public void setEntity(LogSystem entity) {
		this.entity = entity;
	}

}