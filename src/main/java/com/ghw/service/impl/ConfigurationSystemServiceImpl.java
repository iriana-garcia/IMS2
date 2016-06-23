package com.ghw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.ConfigurationSystemDAO;
import com.ghw.model.ConfigurationSystem;
import com.ghw.service.ConfigurationSystemService;

@Service("configurationSystemService")
@Transactional(readOnly = false)
public class ConfigurationSystemServiceImpl extends
		ServiceImpl<ConfigurationSystem, String, ConfigurationSystemDAO>
		implements ConfigurationSystemService {
	
	private ConfigurationSystemDAO dao;

	@Autowired
	public void setDao(ConfigurationSystemDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
}