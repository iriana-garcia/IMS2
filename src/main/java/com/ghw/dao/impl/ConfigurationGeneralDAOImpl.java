package com.ghw.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.ghw.dao.ConfigurationGeneralDAO;
import com.ghw.model.ConfigurationGeneral;

@Repository("configurationGeneralDAO")
public class ConfigurationGeneralDAOImpl extends
		GenericHibernateDAO<ConfigurationGeneral, String> implements
		ConfigurationGeneralDAO {

	@Override
	public ConfigurationGeneral getData() {

		Query query = getSession().createQuery(
				"select c from ConfigurationGeneral c");

		List<ConfigurationGeneral> list = query.list();

		return list.size() > 0 ? list.get(0) : null;
	}
}