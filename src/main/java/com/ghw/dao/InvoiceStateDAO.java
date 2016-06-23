package com.ghw.dao;

import java.util.List;

import com.ghw.model.InvoiceState;

public interface InvoiceStateDAO extends GenericDAO<InvoiceState, String> {

	public List<InvoiceState> getDataOrderExceptCancel();
}