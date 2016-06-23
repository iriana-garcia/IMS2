package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.EventDAO;
import com.ghw.model.ClientApplication;
import com.ghw.model.Event;

@Repository("eventDAO")
public class EventDAOImpl extends GenericHibernateDAO<Event, String> implements
		EventDAO {

	/**
	 * Update the client application associate to events
	 */
	@Override
	public void update(List<Event> events, ClientApplication ca) {
		if (events != null && events.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Event s : events)
				ids.add(s.getId());

			Query query = getSession().createQuery(
					"update Event set clientApplication=:ca where id IN(:ids)");
			query.setEntity("ca", ca);
			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	/**
	 * remove all the client application from the list of events
	 */
	@Override
	public void clearClientApplication(List<Event> events) {
		if (events != null && events.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Event s : events)
				ids.add(s.getId());

			Query query = getSession()
					.createQuery(
							"update Event set clientApplication=null where id IN(:ids)");

			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	/**
	 * remove all the client application from a CA
	 */
	@Override
	public void clearClientApplication(ClientApplication ca) {

		Query query = getSession()
				.createQuery(
						"update Event s set s.clientApplication=null where s.clientApplication.id =:id");

		query.setString("id", ca.getId());

		query.executeUpdate();

	}

	/**
	 * Get all the Event without client application associate
	 */
	@Override
	public List<Event> getDataWithoutCA() {
		Criteria criteria = getSession().createCriteria(Event.class).addOrder(
				Order.asc("name"));

		criteria.add(Restrictions.isNull("clientApplication"));
		return criteria.list();
	}

	/**
	 * Get all the Event without client application associate or with the client
	 * application passed to a method
	 */
	@Override
	public List<Event> getDataWithoutCA(ClientApplication ca) {
		Criteria criteria = getSession().createCriteria(Event.class).addOrder(
				Order.asc("name"));

		criteria.add(Restrictions.or(
				Restrictions.isNull("clientApplication.id"),
				Restrictions.eqOrIsNull("clientApplication.id", ca.getId())));

		return criteria.list();
	}

	/**
	 * Get all the Event associate to a client application
	 */
	@Override
	public List<Event> getDataByCA(ClientApplication ca) {
		Criteria criteria = getSession().createCriteria(Event.class);

		criteria.add(Restrictions.eq("clientApplication.id", ca.getId()));

		criteria.addOrder(Order.asc("name"));

		return criteria.list();
	}

}