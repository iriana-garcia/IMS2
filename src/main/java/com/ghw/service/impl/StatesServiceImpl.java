package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.constant.Constant;
import com.ghw.dao.StatesDAO;
import com.ghw.model.Country;
import com.ghw.model.States;
import com.ghw.service.StatesService;
import com.googlecode.ehcache.annotations.Cacheable;

@Service("statesService")
@Transactional(readOnly = false)
public class StatesServiceImpl extends ServiceImpl<States, String, StatesDAO>
		implements StatesService {
	private StatesDAO dao;

	@Autowired
	public void setDao(StatesDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	public List<States> getDataByCountry(Country country) {

		return dao.getDataByCountry(country);

	}

	@Override
	@Cacheable(cacheName = "statesCache")
	public List<States> getDataByUS() {

		return dao.getDataByCountry(new Country(Constant.usCountryId));

	}

	public States getStateByAbbreviation(String abbreviation) {
		return dao.getStateByAbbreviation(abbreviation);
	}
}