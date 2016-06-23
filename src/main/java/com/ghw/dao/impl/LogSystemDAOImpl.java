package com.ghw.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.ghw.dao.LogSystemDAO;
import com.ghw.filter.FilterBase;
import com.ghw.filter.LogSystemFilter;
import com.ghw.model.LogSystem;
import com.ghw.model.User;

@Repository("logSystemDAO")
public class LogSystemDAOImpl extends GenericHibernateDAO<LogSystem, String>
		implements LogSystemDAO {

	/**
	 * Get the data for the table with filter
	 */
	@Override
	public List<LogSystem> getData(FilterBase filter) throws Exception {

		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("date"), "date");
		projection.add(Property.forName("className"), "className");
		projection.add(Property.forName("method"), "method");
		projection.add(Property.forName("detail"), "detail");
		projection.add(Property.forName("error"), "error");
		projection.add(Property.forName("ip"), "ip");
		// projection.add(Property.forName("user.id"), "user.id");
		projection.add(Property.forName("user.name"), "userName");

		criteria.setProjection(projection);

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				LogSystem.class));

		// default filter
		if (StringUtils.isEmpty(filter.getSortField())) {
			criteria.addOrder(Order.desc("date"));
		} else {
			if (StringUtils.equalsIgnoreCase(filter.getSortOrder().name(),
					"ascending")) {
				criteria.addOrder(Order.asc(filter.getSortField()));
			} else {
				criteria.addOrder(Order.desc(filter.getSortField()));
			}
		}

		if (filter.getNumberOfRows() > 0) {
			criteria.setMaxResults(filter.getNumberOfRows());
			criteria.setFirstResult(filter.getFirstRow());
		}

		// List<LogSystem> li = criteria.list();

		return criteria.list();

	}

	@Override
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		Criteria criteria = super.formCriteriaByListAndCount(filter)
				.createAlias("user", "user", JoinType.LEFT_OUTER_JOIN);

		LogSystemFilter logFilter = (LogSystemFilter) filter;

		if (logFilter.getTypeReport() == 2) {
			criteria.add(Restrictions.eq("processed", true));
		}

		if (logFilter.getStartDate() != null && logFilter.getEndDate() == null) {
			criteria.add(Restrictions.sqlRestriction("date(log_date) = ? ",
					new Date[] { logFilter.getStartDate() },
					new Type[] { StandardBasicTypes.DATE }));
		}

		if (logFilter.getStartDate() != null && logFilter.getEndDate() != null) {
			criteria.add(Restrictions.sqlRestriction(
					"date(log_date) between ? and ?",
					new Date[] { logFilter.getStartDate(),
							logFilter.getEndDate() }, new Type[] {
							StandardBasicTypes.DATE, StandardBasicTypes.DATE }));
		}

		return criteria;
	}

	/**
	 * insert in log system that the user make logout
	 */
	@Override
	public void insertLogout(User user) {

		LogSystem log = new LogSystem();
		log.setClassName(user.getClass().getSimpleName());
		log.setDetail(user.toString());
		log.setMethod("logout");
		log.setClassId(user.getId().toString());
		log.setUser(null);
		log.setIp(user.getIp());

		makePersistent(log);

	}

	/**
	 * insert in log system an error
	 */
	@Override
	public void insertError(User user, String method, String detail,
			String className) {

		LogSystem log = new LogSystem();
		log.setClassName(className);
		log.setDetail(detail);
		log.setMethod(method);
		log.setError(true);

		if (user != null) {
			log.setUser(user);
			log.setIp(user.getIp());
		}

		makePersistent(log);

	}

}