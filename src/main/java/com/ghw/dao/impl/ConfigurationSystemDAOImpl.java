package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.ConfigurationSystemDAO;
import com.ghw.model.ConfigurationSystem;

@Repository("configurationSystemDAO")
public class ConfigurationSystemDAOImpl extends
		GenericHibernateDAO<ConfigurationSystem, String> implements
		ConfigurationSystemDAO {
}