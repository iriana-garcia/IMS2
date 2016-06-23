package com.ghw.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.ghw.dao.RoutingNumbersDAO;
import com.ghw.model.RoutingNumbers;
import com.ghw.service.RoutingNumbersService;

@Service("routingNumbersService")
@Transactional(readOnly = false)
public class RoutingNumbersServiceImpl extends
		ServiceImpl<RoutingNumbers, String, RoutingNumbersDAO> implements
		RoutingNumbersService {
	
	private RoutingNumbersDAO dao;

	@Autowired
	public void setDAO(RoutingNumbersDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
	
	public boolean validateIfExistRoutingNumber(String routingNumber){
		return dao.validateIfExistRoutingNumber(routingNumber);
	}
}