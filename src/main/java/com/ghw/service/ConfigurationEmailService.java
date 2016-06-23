package com.ghw.service;

import com.ghw.model.ConfigurationEmail;

public interface ConfigurationEmailService extends
		Service<ConfigurationEmail, String> {

	public ConfigurationEmail saveOrUpdate(ConfigurationEmail entityWelcome,
			ConfigurationEmail entityInvoice, ConfigurationEmail entityFinance);
}