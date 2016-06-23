package com.ghw.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import com.ghw.dao.IncentiveDAO;
import com.ghw.model.Incentive;
import com.ghw.model.Invoice;

@Repository("incentiveDAO")
public class IncentiveDAOImpl extends GenericHibernateDAO<Incentive, String>
		implements IncentiveDAO {

	/**
	 * Get the list invoice work
	 * 
	 */
	@Override
	public List<Incentive> getDataByInvoice(Invoice invoice) {

		List<Incentive> incentives = null;
		if (invoice != null && StringUtils.isNotBlank(invoice.getId())) {

			Criteria criteria = getSession().createCriteria(Incentive.class)
					.setFetchMode("skill", FetchMode.JOIN)
					.createAlias("skill", "skill", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("invoice.id", invoice.getId()));
			criteria.addOrder(Order.desc("date"));

			incentives = criteria.list();

		}

		return incentives;

	}

	/**
	 * Get the list invoice work
	 * 
	 */
	@Override
	public List<Incentive> getDataByInvoice(List<Invoice> invoices) {

		List<Incentive> incentives = null;
		if (invoices != null && invoices.size() > 0) {

			Criteria criteria = getSession().createCriteria(Incentive.class);
			criteria.add(Restrictions.in("invoice", invoices));
			criteria.addOrder(Order.desc("date"));

			incentives = criteria.list();

		}

		return incentives;

	}
}