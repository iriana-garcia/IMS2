package com.ghw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.ghw.model.InvoiceState;
import com.ghw.service.InvoiceStateService;
import com.ghw.util.Context;

@ManagedBean
@RequestScoped
public class InvoiceStateController extends
		Controller<InvoiceState, String, InvoiceStateService> {

	@ManagedProperty(value = "#{invoiceStateService}")
	private InvoiceStateService service;

	@Override
	public InvoiceStateService getService() {
		return service;
	}

	public void setService(InvoiceStateService service) {
		this.service = service;
	}

	/**
	 * Fill the p:selectOneMenu, get the data ordered
	 * 
	 * @return
	 */
	public List<InvoiceState> getListOrderExceptCancel() {

		if (listOrder == null) {
			listOrder = new ArrayList<InvoiceState>();
		}

		try {
			if (listOrder == null || listOrder.size() == 0) {
				listOrder = service.getDataOrderExceptCancel();
			}
		} catch (Exception e) {

			Context.addErrorMessageFromMSG(e.getMessage());
		}

		return listOrder;
	}

}
