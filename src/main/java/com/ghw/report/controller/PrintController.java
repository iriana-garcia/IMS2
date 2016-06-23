package com.ghw.report.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;

import com.ghw.model.Invoice;
import com.ghw.report.ReportApp;
import com.ghw.report.model.PrintFilter;
import com.ghw.report.service.ReportService;
import com.ghw.service.InvoiceService;
import com.ghw.util.Context;

@ManagedBean
@RequestScoped
public class PrintController {

	protected String listMethod = "getData";
	protected String detailMethod = "loadAllById";

	private boolean error;

	@ManagedProperty(value = "#{reportService}")
	private ReportService reportService;
	
	@ManagedProperty(value = "#{invoiceService}")
	private InvoiceService invoiceService;


	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	

	public InvoiceService getInvoiceService() {
		return invoiceService;
	}

	public void setInvoiceService(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	private List data;

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}

	/**
	 * To print from the pages
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void imprimirListado(ActionEvent event) {

		setError(true);

		try {

			String sdetail = (String) Context
					.getEventAttribute(event, "detail");

			String title = (String) Context.getEventAttribute(event, "title");

			List<Object[]> param = new ArrayList<Object[]>();
			param.add(new Object[] {
					"TITLE",
					StringUtils.isNotBlank(title) ? Context.getMSGText(title)
							: null });

			PrintFilter iFilter = new PrintFilter();
			iFilter.setTypeDefault(sdetail != null
					&& sdetail.equalsIgnoreCase("true") ? 2 : 1);
			iFilter.setIdGroup(Context.getEventAttribute(event, "report")
					.toString());

			ReportApp reportApp = new ReportApp(param);
			reportApp.setReportService(reportService);

			reportApp.getJasperPrint(iFilter);
			setError(false);

		} catch (Exception e) {
			setError(true);
		}
	}

	@SuppressWarnings("unchecked")
	public void printInvoice(ActionEvent event) {

		setError(true);

		try {

			Invoice invoice = (Invoice) Context.getEventAttribute(event,
					"invoice");

			List<Object[]> param = new ArrayList<Object[]>();

			PrintFilter iFilter = new PrintFilter();
			iFilter.setTypeDefault(2);
			iFilter.setIdGroup(Context.getEventAttribute(event, "report")
					.toString());

			ReportApp reportApp = new ReportApp(param);
			reportApp.setReportService(reportService);

			reportApp.getJasperPrint(iFilter, new Object[] { invoice });

			setError(false);

		} catch (Exception e) {
			setError(true);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void printInvoiceSearchDetail(ActionEvent event) {

		setError(true);

		try {

			Invoice invoice = (Invoice) Context.getEventAttribute(event,
					"invoice");
			
			invoice = invoiceService.loadAllById(invoice);

			List<Object[]> param = new ArrayList<Object[]>();

			PrintFilter iFilter = new PrintFilter();
			iFilter.setTypeDefault(2);
			iFilter.setIdGroup(Context.getEventAttribute(event, "report")
					.toString());

			ReportApp reportApp = new ReportApp(param);
			reportApp.setReportService(reportService);

			reportApp.getJasperPrint(iFilter, new Object[] { invoice });

			setError(false);

		} catch (Exception e) {
			setError(true);
		}
	}

	public void printListTable(ActionEvent event) {

		setError(true);

		try {

			String sdetail = (String) Context
					.getEventAttribute(event, "detail");

			boolean detail = (sdetail != null && sdetail
					.equalsIgnoreCase("true"));

			String title = (String) Context.getEventAttribute(event, "title");

			List<Object[]> param = new ArrayList<Object[]>();
			param.add(new Object[] {
					"TITLE",
					StringUtils.isNotBlank(title) ? Context.getMSGText(title)
							: null });

			String idGroup = Context.getEventAttribute(event, "report")
					.toString();

			report(detail, idGroup, param);

		} catch (Exception e) {
			setError(true);
			e.printStackTrace();
		}
	}

	private void report(boolean detail, String idGroup, List<Object[]> param) {
		try {

			PrintFilter iFilter = new PrintFilter();
			iFilter.setTypeDefault((detail) ? 2 : 1);
			iFilter.setIdGroup(idGroup);

			ReportApp reportApp = new ReportApp(param);
			reportApp.setReportService(reportService);
			reportApp.getJasperPrintTabla(iFilter);
			setError(false);
		} catch (Exception e) {
			setError(true);
			e.printStackTrace();
		}
	}

	private void reporteWithOutTable(boolean detail, String idGroup,
			List<Object[]> param) {
		try {
			PrintFilter iFilter = new PrintFilter();
			iFilter.setTypeDefault((detail) ? 2 : 1);
			iFilter.setIdGroup(idGroup);

			ReportApp reportApp = new ReportApp(param);
			reportApp.setReportService(reportService);
			reportApp.getJasperPrint(iFilter);
			setError(false);
		} catch (Exception e) {
			setError(true);
			e.printStackTrace();
		}
	}

}
