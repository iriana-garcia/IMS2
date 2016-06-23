package com.ghw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.UpdatedDateDAO;
import com.ghw.model.UpdatedDate;
import com.ghw.service.UpdatedDateService;

@Service("updatedDateService")
@Transactional(readOnly = false)
public class UpdatedDateServiceImpl extends
		ServiceImpl<UpdatedDate, Integer, UpdatedDateDAO> implements
		UpdatedDateService {
	private UpdatedDateDAO dao;

	@Autowired
	public void setDAO(UpdatedDateDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
}