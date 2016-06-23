package com.ghw.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.model.User;
import com.ghw.report.service.OracleService;

//@Service("oracleService")
@Transactional(readOnly = false)
public class CopyOfOracleServiceImpl implements OracleService {

	@Override
	public String createSupplierFiles(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createPayables(User user) {
		// TODO Auto-generated method stub
		return null;
	}

}