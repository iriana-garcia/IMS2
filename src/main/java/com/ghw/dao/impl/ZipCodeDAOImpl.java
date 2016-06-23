package com.ghw.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.ZipCodeDAO;
import com.ghw.model.ZipCode;

@Repository("zipCodeDAO")
public class ZipCodeDAOImpl extends GenericHibernateDAO<ZipCode, String>
		implements ZipCodeDAO {

	public ZipCode getDataByCode(String code) {
		Criteria criteria = getSession().createCriteria(ZipCode.class);

		criteria.add(Restrictions.eq("number", code));

		return (ZipCode) criteria.uniqueResult();
	}

	@Override
	public List<ZipCode> getDataOrder() {
		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.addOrder(Order.asc("number")).createAlias("states", "states")
				.setFetchMode("states", FetchMode.JOIN);

		return criteria.list();
	}

}