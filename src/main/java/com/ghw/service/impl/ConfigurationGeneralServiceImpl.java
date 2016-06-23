package com.ghw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.ConfigurationGeneralDAO;
import com.ghw.model.ConfigurationGeneral;
import com.ghw.service.ConfigurationGeneralService;

@Service("configurationGeneralService")
@Transactional(readOnly = false)
public class ConfigurationGeneralServiceImpl extends
		ServiceImpl<ConfigurationGeneral, String, ConfigurationGeneralDAO> implements
		ConfigurationGeneralService {

	private ConfigurationGeneralDAO dao;

	@Autowired
	public void setDao(ConfigurationGeneralDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	public ConfigurationGeneral getDataConfiguration() {
		return dao.getData();
	}

}