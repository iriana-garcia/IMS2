package com.ghw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.dao.ConfigurationEmailDAO;
import com.ghw.model.ConfigurationEmail;
import com.ghw.service.ConfigurationEmailService;

@Service("configurationEmailService")
@Transactional(readOnly = false)
public class ConfigurationEmailServiceImpl extends
		ServiceImpl<ConfigurationEmail, String, ConfigurationEmailDAO>
		implements ConfigurationEmailService {
	private ConfigurationEmailDAO dao;

	@Autowired
	public void setDao(ConfigurationEmailDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA_M')")
	public ConfigurationEmail saveOrUpdate(ConfigurationEmail entityWelcome,
			ConfigurationEmail entityInvoice, ConfigurationEmail entityFinance) {

		entityWelcome.setType("W");
		entityInvoice.setType("I");
		entityFinance.setType("F");

		dao.saveOrUpdate(entityWelcome);
		dao.saveOrUpdate(entityInvoice);
		dao.saveOrUpdate(entityFinance);
		
		return entityWelcome;
	}

}