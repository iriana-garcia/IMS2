package com.ghw.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import com.ghw.datamodel.AgentStateDetailDataModel;
import com.ghw.filter.AgentStateDetailFilter;
import com.ghw.model.AgentStateDetail;
import com.ghw.service.AgentStateDetailService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class AgentStateDetailController extends
		Controller<AgentStateDetail, String, AgentStateDetailService> {

	@ManagedProperty(value = "#{agentStateDetailDataModel}")
	private AgentStateDetailDataModel lazyModel;

	@ManagedProperty(value = "#{agentStateDetailFilter}")
	private AgentStateDetailFilter filter;

	@PostConstruct
	public void init() {
		try {

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

		} finally {
		}

	}

	/**
	 * Clear the datatable filter
	 */
	@Override
	public void clearFilter() {

		// super.clearFilter();

		filter.setEndDate(null);
		filter.setStartDate(null);
		filter.getStartDate();
		filter.getEndDate();

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').clearFilters()");
	}

	public void cleanDetailFilter() {

	}

	public void validateFilter() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			if (filter.getEndDate() != null && filter.getStartDate() != null
					&& filter.getEndDate().before(filter.getStartDate())) {
				throw new Exception("start_end_date_incorrect");
			}
			if (filter.getEndDate() != null && filter.getStartDate() == null) {
				throw new SystemException("date_end_without_start");
			}

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}
	}

	public AgentStateDetailDataModel getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(AgentStateDetailDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public AgentStateDetailFilter getFilter() {
		return filter;
	}

	public void setFilter(AgentStateDetailFilter filter) {
		this.filter = filter;
	}

}
