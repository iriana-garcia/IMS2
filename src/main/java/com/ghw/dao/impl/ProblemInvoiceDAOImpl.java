package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.ghw.dao.ProblemInvoiceDAO;
import com.ghw.model.Invoice;
import com.ghw.model.ProblemInvoice;

@Repository("problemInvoiceDAO")
public class ProblemInvoiceDAOImpl extends
		GenericHibernateDAO<ProblemInvoice, String> implements
		ProblemInvoiceDAO {

	public List<ProblemInvoice> getDataByListInvoices(List<Invoice> invoices) {

		List<ProblemInvoice> problemInvoices = new ArrayList<ProblemInvoice>();
		if (invoices != null && invoices.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Invoice i : invoices) {
				ids.add(i.getId());
			}
			Query query = getSession()
					.createQuery(
							"from ProblemInvoice p where p.invId IN (:ids) order by p.invoiceNumber");
			query.setParameterList("ids", ids);
			problemInvoices = query.list();
		}
		return problemInvoices;
	}
}