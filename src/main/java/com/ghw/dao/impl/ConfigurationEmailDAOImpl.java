package com.ghw.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.ConfigurationEmailDAO;
import com.ghw.model.ConfigurationEmail;

@Repository("configurationEmailDAO")
public class ConfigurationEmailDAOImpl extends
		GenericHibernateDAO<ConfigurationEmail, String> implements
		ConfigurationEmailDAO {

	/**
	 * Get the configuration mail by Type, the type can be W: welcome email, F:
	 * finance email, I: invoice email
	 */
	@Override
	public ConfigurationEmail getDataByType(String type) {
		Criteria criteria = getSession().createCriteria(
				ConfigurationEmail.class);
		criteria.add(Restrictions.eq("type", type));
		List<ConfigurationEmail> list = criteria.list();
		return list != null && list.size() > 0 ? list.get(0) : null;
	}
}