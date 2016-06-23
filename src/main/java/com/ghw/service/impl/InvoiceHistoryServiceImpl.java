package com.ghw.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.ghw.dao.InvoiceHistoryDAO;
import com.ghw.model.InvoiceHistory;
import com.ghw.service.InvoiceHistoryService;

@Service("invoiceHistoryService")
@Transactional(readOnly = false)
public class InvoiceHistoryServiceImpl extends
		ServiceImpl<InvoiceHistory, String, InvoiceHistoryDAO> implements
		InvoiceHistoryService {
	private InvoiceHistoryDAO dao;

	@Autowired
	public void setDAO(InvoiceHistoryDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
}