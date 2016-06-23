package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.IboStateDAO;
import com.ghw.model.IboState;
import com.ghw.model.IboType;
import com.ghw.service.IboStateService;
import com.googlecode.ehcache.annotations.Cacheable;

@Service("iboStateService")
@Transactional(readOnly = false)
public class IboStateServiceImpl extends
		ServiceImpl<IboState, String, IboStateDAO> implements IboStateService {
	private IboStateDAO dao;

	@Autowired
	public void setDAO(IboStateDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	@Cacheable(cacheName = "iboStateCache")
	public List<IboState> getDataOrder() {
		return super.getDataOrder();
	}
}