package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.controller.SessionBean;
import com.ghw.dao.InvoiceHoursAddedDAO;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceHoursAdded;
import com.ghw.model.InvoiceWork;
import com.ghw.service.InvoiceHoursAddedService;

@Service("invoiceHoursAddedService")
@Transactional(readOnly = false)
public class InvoiceHoursAddedServiceImpl extends
		ServiceImpl<InvoiceHoursAdded, String, InvoiceHoursAddedDAO> implements
		InvoiceHoursAddedService {

	private InvoiceHoursAddedDAO dao;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	public void setDAO(InvoiceHoursAddedDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Transactional(readOnly = false)
	@PreAuthorize("hasAnyRole('SUPER','INVOICES_M')")
	@Override
	public String saverOrUpdate(List<InvoiceHoursAdded> hoursAddeds,
			List<InvoiceHoursAdded> hoursAddedOld, Invoice invoice,
			InvoiceWork invoiceWork) throws Exception {

		if (hoursAddeds != null || hoursAddedOld != null) {

			if (hoursAddeds == null)
				hoursAddeds = new ArrayList<InvoiceHoursAdded>();

			if (hoursAddedOld == null)
				hoursAddedOld = new ArrayList<InvoiceHoursAdded>();

			String modification = invoice.getUser().toString() + " "
					+ invoice.toString();

			// before insert the change I need to save the field modify
			List<InvoiceHoursAdded> newList = new ArrayList<InvoiceHoursAdded>();
			newList.addAll(hoursAddeds);
			newList.removeAll(hoursAddedOld);

			modification += " Added Hours:" + newList.toString();
			// see for the field modificated
			modification += " Modificated Hours:[";
			List<InvoiceHoursAdded> newUpdated = new ArrayList<InvoiceHoursAdded>();
			for (InvoiceHoursAdded im : hoursAddedOld) {
				for (InvoiceHoursAdded im2 : hoursAddeds) {
					if (im.equals(im2) && !im.equalsAll(im2)) {
						modification += " New hours: " + im2.toString()
								+ " Old hours: " + im.toString();
						newUpdated.add(im2);
						break;
					}
				}

			}
			modification += "]";

			// insert all the new hours
			for (InvoiceHoursAdded i : newList) {
				i.setInvoiceWork(invoiceWork);
				i.setCreatedDate(new Date());
				i.setUserCreated(sessionBean.getUser());
				dao.makePersistent(i);
				dao.flush();
			}
			// update all the new hours
			for (InvoiceHoursAdded i : newUpdated) {
				i.setInvoiceWork(invoiceWork);
				i.setUpdateDate(new Date());
				i.setUserUpdated(sessionBean.getUser());
				dao.update(i);
				dao.flush();
			}

			// insert into log system the action
			return modification;

		}
		return null;

	}
}