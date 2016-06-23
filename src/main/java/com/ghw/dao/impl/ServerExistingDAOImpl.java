package com.ghw.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.ServerExistingDAO;
import com.ghw.model.ServerExisting;

@Repository("serverExistingDAO")
public class ServerExistingDAOImpl extends
		GenericHibernateDAO<ServerExisting, String> implements
		ServerExistingDAO {

	public ServerExisting getDataByHost(String host) {
		Criteria criteria = getSession().createCriteria(ServerExisting.class);
		criteria.add(Restrictions.eq("host", host));

		return (ServerExisting) criteria.uniqueResult();
	}

	public List<ServerExisting> getPrincipalHost(Integer priority) {

		Criteria criteria = getSession().createCriteria(ServerExisting.class);

		criteria.add(Restrictions.lt("priority", priority));

		return criteria.list();
	}
}