package com.ghw.service;

import java.util.List;

import com.ghw.model.Invoice;
import com.ghw.model.ProblemInvoice;

public interface ProblemInvoiceService extends Service<ProblemInvoice, String> {
	public List<ProblemInvoice> getDataByListInvoices(List<Invoice> invoices);
}