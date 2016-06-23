package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.InvoiceHoursAddedDAO;
import com.ghw.model.InvoiceHoursAdded;

@Repository("invoiceHoursAddedDAO")
public class InvoiceHoursAddedDAOImpl extends
		GenericHibernateDAO<InvoiceHoursAdded, String> implements
		InvoiceHoursAddedDAO {
}