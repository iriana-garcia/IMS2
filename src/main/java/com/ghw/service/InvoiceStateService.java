package com.ghw.service;

import java.util.List;

import com.ghw.model.InvoiceState;

public interface InvoiceStateService extends Service<InvoiceState, String> {

	public List<InvoiceState> getDataOrderExceptCancel();
}