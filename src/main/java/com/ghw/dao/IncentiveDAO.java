package com.ghw.dao;

import java.util.List;

import com.ghw.model.Incentive;
import com.ghw.model.Invoice;

public interface IncentiveDAO extends GenericDAO<Incentive, String> {
	
	public List<Incentive> getDataByInvoice(Invoice invoice);

	public List<Incentive> getDataByInvoice(List<Invoice> invoices);
}