package com.ghw.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.component.api.UIColumn;

import com.ghw.filter.InvoiceFilter;
import com.ghw.model.Invoice;

/**
 * Utilitary class for store in session the Invoice value
 * 
 * @author ifernandez
 * 
 */
@SessionScoped
@ManagedBean
public class InvoiceUtil implements Serializable {

	private Invoice invoice;

	private InvoiceFilter invoiceFilter;

	private List<UIColumn> columns;

	private Integer edit;

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Integer getEdit() {
		return edit;
	}

	public void setEdit(Integer edit) {
		this.edit = edit;
	}

	public InvoiceFilter getInvoiceFilter() {
		return invoiceFilter;
	}

	public void setInvoiceFilter(InvoiceFilter invoiceFilter) {
		this.invoiceFilter = invoiceFilter;
	}

	public List<UIColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<UIColumn> columns) {
		this.columns = columns;
	}

}
