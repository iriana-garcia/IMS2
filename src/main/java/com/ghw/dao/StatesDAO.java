package com.ghw.dao;

import java.util.List;

import com.ghw.model.Country;
import com.ghw.model.States;

public interface StatesDAO extends GenericDAO<States, String> {

	public List<States> getDataByCountry(Country country);

	public States getStateByAbbreviation(String abbreviation);
}