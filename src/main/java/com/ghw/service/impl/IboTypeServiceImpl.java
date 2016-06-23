package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.IboTypeDAO;
import com.ghw.model.IboType;
import com.ghw.service.IboTypeService;
import com.googlecode.ehcache.annotations.Cacheable;

@Service("iboTypeService")
@Transactional(readOnly = false)
public class IboTypeServiceImpl extends
		ServiceImpl<IboType, String, IboTypeDAO> implements IboTypeService {
	private IboTypeDAO dao;

	@Autowired
	public void setDAO(IboTypeDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
	
	@Override
	@Cacheable(cacheName = "iboTypeCache")
	public List<IboType> getDataOrder() {
		return super.getDataOrder();
	}
}