package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.InvoiceStateDAO;
import com.ghw.model.InvoiceState;
import com.ghw.service.InvoiceStateService;
import com.googlecode.ehcache.annotations.Cacheable;

@Service("invoiceStateService")
@Transactional(readOnly = false)
public class InvoiceStateServiceImpl extends
		ServiceImpl<InvoiceState, String, InvoiceStateDAO> implements
		InvoiceStateService {
	private InvoiceStateDAO dao;

	@Autowired
	public void setDAO(InvoiceStateDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	@Cacheable(cacheName = "invoiceStateCache")
	public List<InvoiceState> getDataOrder() {
		return super.getDataOrder();
	}

	@Override
	public List<InvoiceState> getDataOrderExceptCancel() {
		return dao.getDataOrderExceptCancel();
	}
}