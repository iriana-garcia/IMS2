package com.ghw.service;

import java.util.List;

import com.ghw.model.Country;
import com.ghw.model.States;

public interface StatesService extends Service<States, String> {

	public List<States> getDataByCountry(Country country);

	public List<States> getDataByUS();

	public States getStateByAbbreviation(String abbreviation);
}