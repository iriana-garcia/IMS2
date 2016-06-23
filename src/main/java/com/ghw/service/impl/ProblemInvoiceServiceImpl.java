package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.ProblemInvoiceDAO;
import com.ghw.model.Invoice;
import com.ghw.model.ProblemInvoice;
import com.ghw.service.ProblemInvoiceService;

@Service("problemInvoiceService")
@Transactional(readOnly = false)
public class ProblemInvoiceServiceImpl extends
		ServiceImpl<ProblemInvoice, String, ProblemInvoiceDAO> implements
		ProblemInvoiceService {
	private ProblemInvoiceDAO dao;

	@Autowired
	public void setDAO(ProblemInvoiceDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	public List<ProblemInvoice> getDataByListInvoices(List<Invoice> invoices) {
		return dao.getDataByListInvoices(invoices);
	}
}