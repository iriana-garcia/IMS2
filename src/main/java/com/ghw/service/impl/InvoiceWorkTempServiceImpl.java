package com.ghw.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.ghw.dao.InvoiceWorkTempDAO;
import com.ghw.model.InvoiceWorkTemp;
import com.ghw.service.InvoiceWorkTempService;

@Service("invoiceWorkTempService")
@Transactional(readOnly = false)
public class InvoiceWorkTempServiceImpl extends
		ServiceImpl<InvoiceWorkTemp, String, InvoiceWorkTempDAO> implements
		InvoiceWorkTempService {
	private InvoiceWorkTempDAO dao;

	@Autowired
	public void setDAO(InvoiceWorkTempDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
}