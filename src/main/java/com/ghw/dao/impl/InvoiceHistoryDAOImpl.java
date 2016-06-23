package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.InvoiceHistoryDAO;
import com.ghw.model.InvoiceHistory;

@Repository("invoiceHistoryDAO")
public class InvoiceHistoryDAOImpl extends
		GenericHibernateDAO<InvoiceHistory, String> implements
		InvoiceHistoryDAO {
}