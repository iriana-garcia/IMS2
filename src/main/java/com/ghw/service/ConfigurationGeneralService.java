package com.ghw.service;

import com.ghw.model.ConfigurationGeneral;

public interface ConfigurationGeneralService extends Service<ConfigurationGeneral, String> {
	
	public ConfigurationGeneral getDataConfiguration();
}