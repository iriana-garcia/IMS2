package com.ghw.datamodel;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.math.util.MathUtils;

import com.ghw.filter.AgentStateDetailFilter;
import com.ghw.filter.FilterBase;
import com.ghw.model.AgentStateDetail;
import com.ghw.service.AgentStateDetailService;

@ManagedBean
@ViewScoped
public class AgentStateDetailDataModel extends
		GenericDataModel<String, AgentStateDetail, AgentStateDetailService> {

	@ManagedProperty(value = "#{agentStateDetailService}")
	private AgentStateDetailService service;

	@ManagedProperty(value = "#{agentStateDetailFilter}")
	private AgentStateDetailFilter filterBase;

	private Double totalHours;

	public AgentStateDetailService getService() {
		return service;
	}

	public void setService(AgentStateDetailService service) {
		this.service = service;
		super.setService(service);
	}

	@Override
	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = (AgentStateDetailFilter) filterBase;
		super.setFilterBase(filterBase);

	}

	@Override
	public FilterBase getFilterBase() {
		return filterBase;
	}

	@Override
	public void calculateTotals() {
		totalHours = 0.0;
		// get only list in page
		List<AgentStateDetail> listPage = getDatasource();
		for (AgentStateDetail asd : listPage) {
			totalHours += asd.getDuration();
		}

		totalHours = MathUtils
				.round(totalHours / new Double(60 * 60 * 1000), 2);
	}

	public Double getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(Double totalHours) {
		this.totalHours = totalHours;
	}

}
