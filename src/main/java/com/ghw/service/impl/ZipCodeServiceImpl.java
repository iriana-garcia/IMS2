package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.ZipCodeDAO;
import com.ghw.model.ZipCode;
import com.ghw.service.ZipCodeService;
import com.googlecode.ehcache.annotations.Cacheable;

@Service("zipCodeService")
@Transactional(readOnly = false)
public class ZipCodeServiceImpl extends
		ServiceImpl<ZipCode, String, ZipCodeDAO> implements ZipCodeService {
	private ZipCodeDAO dao;

	@Autowired
	public void setDAO(ZipCodeDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	@Cacheable(cacheName = "zipCodeCache")
	public List<ZipCode> getDataOrder() {
		return super.getDataOrder();
	}

	@Override
	public ZipCode getDataByCode(String code) {
		return dao.getDataByCode(code);
	}
}