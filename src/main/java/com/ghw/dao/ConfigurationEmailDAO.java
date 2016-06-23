package com.ghw.dao;

import com.ghw.model.ConfigurationEmail;

public interface ConfigurationEmailDAO extends
		GenericDAO<ConfigurationEmail, String> {
	
	public ConfigurationEmail getDataByType(String type);
}