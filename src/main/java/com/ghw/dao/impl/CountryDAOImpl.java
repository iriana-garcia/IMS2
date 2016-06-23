package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.CountryDAO;
import com.ghw.model.Country;

@Repository("countryDAO")
public class CountryDAOImpl extends GenericHibernateDAO<Country, String>
		implements CountryDAO {
	// @Override
	// public List<Country> getDataOrder() {
	// List<Country> countries = new ArrayList<Country>();
	// countries.add(getById("52f03556-162b-4963-8ea1-62fe2c373ede"));
	// return countries;
	// }
}