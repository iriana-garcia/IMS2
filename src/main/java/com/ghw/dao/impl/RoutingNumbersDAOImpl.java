package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.RoutingNumbersDAO;
import com.ghw.model.RoutingNumbers;

@Repository("routingNumbersDAO")
public class RoutingNumbersDAOImpl extends
		GenericHibernateDAO<RoutingNumbers, String> implements
		RoutingNumbersDAO {

	public boolean validateIfExistRoutingNumber(String routingNumber) {
		Criteria criteria = getSession().createCriteria(RoutingNumbers.class)
				.add(Restrictions.eq("number", routingNumber));

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.rowCount());

		criteria.setProjection(projection);

		return (Long) criteria.list().get(0) > 0 ? true : false;

	}

	public List<RoutingNumbers> getDataActiveOracle() {
		Query query = getSession()
				.createQuery(
						"select r from RoutingNumbers r left join fetch r.bank where r.needUpdate = true");

		return query.list();
		// Criteria criteria = getSession().createCriteria(RoutingNumbers.class)
		// .createAlias("bank", "bank");
		// criteria.setFetchMode("bank", FetchMode.JOIN);
		// criteria.add(Restrictions.eq("needUpdate", true));
		// return criteria.list();
	}

	/**
	 * update the field need updated to false when is exported
	 */
	@Override
	public void updateNeedUpdatedFalse(List<RoutingNumbers> routingNumbers) {
		if (routingNumbers != null && routingNumbers.size() > 0) {
			List<String> ids = new ArrayList<String>();
			Integer count = 0;

			Query query = getSession()
					.createQuery(
							"update RoutingNumbers p set p.needUpdate=false where p.id IN(:ids)");

			for (RoutingNumbers s : routingNumbers) {

				ids.add(s.getId());

				if (++count % 1000 == 0) { // 50, same as the JDBC batch

					query.setParameterList("ids", ids);
					query.executeUpdate();

					ids.clear();
				}

			}

			if (ids.size() > 0) {
				query.setParameterList("ids", ids);
				query.executeUpdate();

			}

		}

	}
}