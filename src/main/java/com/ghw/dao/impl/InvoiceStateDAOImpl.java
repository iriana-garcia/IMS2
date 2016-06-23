package com.ghw.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;

import com.ghw.dao.InvoiceStateDAO;
import com.ghw.model.InvoiceState;

@Repository("invoiceStateDAO")
public class InvoiceStateDAOImpl extends
		GenericHibernateDAO<InvoiceState, String> implements InvoiceStateDAO {

	@Override
	public List<InvoiceState> getDataOrder() {
		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.addOrder(Order.asc("name"));
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");

		criteria.setProjection(projection);

		// criteria.add(Restrictions.ne("id", "4"));

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				getPersistentClass()));

		return criteria.list();

	}

	@Override
	public List<InvoiceState> getDataOrderExceptCancel() {
		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.addOrder(Order.asc("name"));
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");

		criteria.setProjection(projection);

		criteria.add(Restrictions.ne("id", "4"));

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				getPersistentClass()));

		return criteria.list();

	}
}