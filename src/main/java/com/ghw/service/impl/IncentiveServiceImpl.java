package com.ghw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.dao.IncentiveDAO;
import com.ghw.model.Incentive;
import com.ghw.service.IncentiveService;

@Service("incentiveService")
@Transactional(readOnly = false)
public class IncentiveServiceImpl extends
		ServiceImpl<Incentive, String, IncentiveDAO> implements
		IncentiveService {
	
	private IncentiveDAO dao;

	@Autowired
	public void setDAO(IncentiveDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	@Transactional(readOnly = false)
	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','INVOICES_M')")
	public Incentive makePersistent(Incentive entity) throws Exception {
		entity.setFieldAdicional(" User: "
				+ entity.getInvoice().getUser().toString() + " Invoice: "
				+ entity.getInvoice().toString());
		return super.makePersistent(entity);
	}

	@Override
	@Transactional(readOnly = false)
	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','INVOICES_M')")
	public Incentive merge(Incentive entity) throws Exception {
		entity.setFieldAdicional(" User: "
				+ entity.getInvoice().getUser().toString() + " Invoice: "
				+ entity.getInvoice().toString());
		return super.merge(entity);
	}
}