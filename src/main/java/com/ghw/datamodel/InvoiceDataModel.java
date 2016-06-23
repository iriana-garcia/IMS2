package com.ghw.datamodel;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.controller.InvoiceUtil;
import com.ghw.filter.FilterBase;
import com.ghw.filter.InvoiceFilter;
import com.ghw.model.Invoice;
import com.ghw.service.InvoiceService;
import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class InvoiceDataModel extends
		GenericDataModel<String, Invoice, InvoiceService> {

	@ManagedProperty(value = "#{invoiceService}")
	private InvoiceService service;

	@ManagedProperty(value = "#{invoiceFilter}")
	private InvoiceFilter filterBase;

	@ManagedProperty(value = "#{invoiceUtil}")
	public InvoiceUtil invoiceUtil;

	private Double totalPageIncentive, totalPageHours, totalPageActualService,
			totalPageAdminFee, totalPageServiceRevenue, totalPageServiceAmount;

	private Double totalIncentive, totalHours, totalActualService,
			totalAdminFee, totalServiceRevenue, totalServiceAmount;

	public InvoiceService getService() {
		return service;
	}

	public void setService(InvoiceService service) {
		this.service = service;
		super.setService(service);
	}

	@Override
	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = (InvoiceFilter) filterBase;
		super.setFilterBase(filterBase);

	}

	@Override
	public FilterBase getFilterBase() {
		return filterBase;
	}

	/**
	 * Calculate the total to show in invoices
	 */
	@Override
	public void calculateTotals() {

		try {

			// restore the value to 0.0
			totalPageIncentive = 0.0;
			totalPageHours = 0.0;
			totalPageActualService = 0.0;
			totalPageAdminFee = 0.0;
			totalPageServiceRevenue = 0.0;
			totalPageServiceAmount = 0.0;

			totalIncentive = 0.0;
			totalHours = 0.0;
			totalActualService = 0.0;
			totalAdminFee = 0.0;
			totalServiceRevenue = 0.0;
			totalServiceAmount = 0.0;

			// get only list in page
			List<Invoice> listInvoicesPage = getDatasource();

			for (Invoice invoice : listInvoicesPage) {
				totalPageIncentive += invoice.getTotalIncentive();
				totalPageHours += invoice.getHoursAdded();
				totalPageActualService += invoice.getActualService();
				totalPageAdminFee += invoice.getAdminFee();
				totalPageServiceRevenue += invoice.getImportTotal() > 0 ? invoice
						.getImportTotal() : 0.0;
				totalPageServiceAmount += invoice.getServiceRevenue() > 0 ? invoice
						.getServiceRevenue() : 0.0;
			}

			if (((InvoiceFilter) filterBase).getTypeReport() == 1) {

				// get total of all register
				Invoice invoice = service.getTotalInvoices(getFilterBase());
				if (invoice != null) {
					totalIncentive = invoice.getTotalIncentive();
					totalHours = invoice.getHoursAdded();
					totalActualService = invoice.getActualService();
					totalAdminFee = invoice.getAdminFee();
					totalServiceRevenue = invoice.getImportTotal();
					totalServiceAmount = invoice.getServiceRevenue();
				}
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	public Double getTotalPageIncentive() {
		return totalPageIncentive;
	}

	public void setTotalPageIncentive(Double totalPageIncentive) {
		this.totalPageIncentive = totalPageIncentive;
	}

	public Double getTotalPageHours() {
		return totalPageHours;
	}

	public void setTotalPageHours(Double totalPageHours) {
		this.totalPageHours = totalPageHours;
	}

	public Double getTotalPageActualService() {
		return totalPageActualService;
	}

	public void setTotalPageActualService(Double totalPageActualService) {
		this.totalPageActualService = totalPageActualService;
	}

	public Double getTotalPageAdminFee() {
		return totalPageAdminFee;
	}

	public void setTotalPageAdminFee(Double totalPageAdminFee) {
		this.totalPageAdminFee = totalPageAdminFee;
	}

	public Double getTotalPageServiceRevenue() {
		return totalPageServiceRevenue;
	}

	public void setTotalPageServiceRevenue(Double totalPageServiceRevenue) {
		this.totalPageServiceRevenue = totalPageServiceRevenue;
	}

	public Double getTotalPageServiceAmount() {
		return totalPageServiceAmount;
	}

	public void setTotalPageServiceAmount(Double totalPageServiceAmount) {
		this.totalPageServiceAmount = totalPageServiceAmount;
	}

	public Double getTotalIncentive() {
		return totalIncentive;
	}

	public void setTotalIncentive(Double totalIncentive) {
		this.totalIncentive = totalIncentive;
	}

	public Double getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(Double totalHours) {
		this.totalHours = totalHours;
	}

	public Double getTotalActualService() {
		return totalActualService;
	}

	public void setTotalActualService(Double totalActualService) {
		this.totalActualService = totalActualService;
	}

	public Double getTotalAdminFee() {
		return totalAdminFee;
	}

	public void setTotalAdminFee(Double totalAdminFee) {
		this.totalAdminFee = totalAdminFee;
	}

	public Double getTotalServiceRevenue() {
		return totalServiceRevenue;
	}

	public void setTotalServiceRevenue(Double totalServiceRevenue) {
		this.totalServiceRevenue = totalServiceRevenue;
	}

	public Double getTotalServiceAmount() {
		return totalServiceAmount;
	}

	public void setTotalServiceAmount(Double totalServiceAmount) {
		this.totalServiceAmount = totalServiceAmount;
	}

	public void setFilterBase(InvoiceFilter filterBase) {
		this.filterBase = filterBase;
	}

	public InvoiceUtil getInvoiceUtil() {
		return invoiceUtil;
	}

	public void setInvoiceUtil(InvoiceUtil invoiceUtil) {
		this.invoiceUtil = invoiceUtil;
	}

}
