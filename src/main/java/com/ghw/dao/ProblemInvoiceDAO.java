package com.ghw.dao;

import java.util.List;

import com.ghw.model.Invoice;
import com.ghw.model.ProblemInvoice;

public interface ProblemInvoiceDAO extends GenericDAO<ProblemInvoice, String> {

	public List<ProblemInvoice> getDataByListInvoices(List<Invoice> invoices);
}