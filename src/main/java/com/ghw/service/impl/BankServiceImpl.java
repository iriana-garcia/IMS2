package com.ghw.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.ghw.dao.BankDAO;
import com.ghw.model.Bank;
import com.ghw.service.BankService;

@Service("bankService")
@Transactional(readOnly = false)
public class BankServiceImpl extends ServiceImpl<Bank, String, BankDAO>
		implements BankService {
	private BankDAO dao;

	@Autowired
	public void setDAO(BankDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
}