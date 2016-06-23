package com.ghw.dao;

import com.ghw.model.ConfigurationGeneral;

public interface ConfigurationGeneralDAO extends
		GenericDAO<ConfigurationGeneral, String> {

	public ConfigurationGeneral getData();
}
