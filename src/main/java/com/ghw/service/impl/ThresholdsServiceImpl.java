package com.ghw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.ThresholdsDAO;
import com.ghw.model.Thresholds;
import com.ghw.service.ThresholdsService;
import com.ghw.util.SystemException;

@Service("thresholdsService")
@Transactional(readOnly = false)
public class ThresholdsServiceImpl extends
		ServiceImpl<Thresholds, String, ThresholdsDAO> implements
		ThresholdsService {
	private ThresholdsDAO dao;

	@Autowired
	public void setDao(ThresholdsDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	public void isValidate(Thresholds entity) throws Exception {
		if (entity.getMax() < entity.getMin())
			throw new SystemException("max_min_incorrect");
		super.isValidate(entity);
	}
}