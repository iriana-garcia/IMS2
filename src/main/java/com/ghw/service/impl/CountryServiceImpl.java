package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.CountryDAO;
import com.ghw.model.Country;
import com.ghw.service.CountryService;
import com.googlecode.ehcache.annotations.Cacheable;

@Service("countryService")
@Transactional(readOnly = false)
public class CountryServiceImpl extends
		ServiceImpl<Country, String, CountryDAO> implements CountryService {
	private CountryDAO dao;

	@Autowired
	public void setDAO(CountryDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	@Cacheable(cacheName = "countryCache")
	public List<Country> getDataOrder() {
		return super.getDataOrder();
	}
}