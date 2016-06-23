package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.model.ProblemInvoice;
import com.ghw.service.ProblemInvoiceService;

@ManagedBean
@ViewScoped
public class ProblemInvoiceDataModel extends
		GenericDataModel<String, ProblemInvoice, ProblemInvoiceService> {

	@ManagedProperty(value = "#{problemInvoiceService}")
	private ProblemInvoiceService service;

	@ManagedProperty(value = "#{filterBase}")
	private FilterBase filterBase;

	public ProblemInvoiceService getService() {
		return service;
	}

	public void setService(ProblemInvoiceService service) {
		this.service = service;
		super.setService(service);
	}

	@Override
	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = filterBase;
		super.setFilterBase(filterBase);

	}

	@Override
	public FilterBase getFilterBase() {
		return filterBase;
	}

}
