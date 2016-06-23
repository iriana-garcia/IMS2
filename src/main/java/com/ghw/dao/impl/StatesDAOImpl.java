package com.ghw.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.StatesDAO;
import com.ghw.model.Country;
import com.ghw.model.States;

@Repository("statesDAO")
public class StatesDAOImpl extends GenericHibernateDAO<States, String>
		implements StatesDAO {

	/**
	 * Get all the states associate to a country
	 */
	public List<States> getDataByCountry(Country country) {

		Criteria criteria = getSession().createCriteria(States.class);
		criteria.add(Restrictions.eq("country.id", country.getId())).addOrder(
				Order.asc("name"));

		return criteria.list();

	}

	public States getStateByAbbreviation(String abbreviation) {

		Criteria criteria = getSession().createCriteria(States.class);
		criteria.add(Restrictions.eq("abbreviation", abbreviation));

		List<States> states = criteria.list();

		return states != null && states.size() > 0 ? states.get(0) : null;
	}
}