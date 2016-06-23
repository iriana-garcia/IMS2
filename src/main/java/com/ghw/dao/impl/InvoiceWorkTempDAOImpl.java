package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.InvoiceWorkTempDAO;
import com.ghw.model.InvoiceWorkTemp;

@Repository("invoiceWorkTempDAO")
public class InvoiceWorkTempDAOImpl extends
		GenericHibernateDAO<InvoiceWorkTemp, String> implements
		InvoiceWorkTempDAO {
}