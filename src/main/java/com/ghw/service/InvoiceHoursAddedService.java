package com.ghw.service;

import java.util.List;

import com.ghw.model.Invoice;
import com.ghw.model.InvoiceHoursAdded;
import com.ghw.model.InvoiceWork;

public interface InvoiceHoursAddedService extends
		Service<InvoiceHoursAdded, String> {

	public String saverOrUpdate(List<InvoiceHoursAdded> hoursAddeds,
			List<InvoiceHoursAdded> hoursAddedOld, Invoice invoice,
			InvoiceWork invoiceWork) throws Exception;
}