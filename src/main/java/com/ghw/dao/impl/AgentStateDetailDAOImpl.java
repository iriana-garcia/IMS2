package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import com.ghw.constant.EventType;
import com.ghw.dao.AgentStateDetailDAO;
import com.ghw.filter.AgentStateDetailFilter;
import com.ghw.filter.FilterBase;
import com.ghw.model.AgentStateDetail;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceWork;
import com.ghw.model.Profile;
import com.ghw.model.User;

@Repository("agentStateDetailDAO")
public class AgentStateDetailDAOImpl extends
		GenericHibernateDAO<AgentStateDetail, String> implements
		AgentStateDetailDAO {

	@Override
	public List<AgentStateDetail> getData(FilterBase filter) throws Exception {

		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("startDate"));
		projection.add(Property.forName("endDate"));
		projection.add(Property.forName("place"));
		projection.add(Property.forName("eventType"));
		projection.add(Property.forName("duration"));
		projection.add(Property.forName("needUpdated"));
		projection.add(Property.forName("invoiceWork.id"));
		projection.add(Property.forName("user.id"));
		projection.add(Property.forName("user.firstName"));
		projection.add(Property.forName("user.lastName"));
		projection.add(Property.forName("p.id"));
		projection.add(Property.forName("p.number"));
		projection.add(Property.forName("user.middleName"));
		projection.add(Property.forName("reasonCodeDescription"));

		criteria.setProjection(projection);

		// default filter
		if (StringUtils.isEmpty(filter.getSortField())) {
			criteria.addOrder(Order.desc("startDate"));
		} else if (filter.getSortField().equals("user.fullName")) {

			if (StringUtils.equalsIgnoreCase(filter.getSortOrder().name(),
					"ascending")) {
				criteria.addOrder(Order.asc("user.firstName"));
				criteria.addOrder(Order.asc("user.middleName"));
				criteria.addOrder(Order.asc("user.lastName"));

			} else {
				criteria.addOrder(Order.desc("user.firstName"));
				criteria.addOrder(Order.desc("user.middleName"));
				criteria.addOrder(Order.desc("user.lastName"));

			}

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

		List<AgentStateDetail> list = new ArrayList<AgentStateDetail>();
		List<Object[]> listObject = criteria.list();
		for (Object[] o : listObject) {
			AgentStateDetail ast = new AgentStateDetail((String) o[0],
					(Date) o[1], (Date) o[2], (Short) o[3], (Integer) o[4],
					null, (Integer) o[5], (Boolean) o[6], (String) o[7],
					(String) o[14]);
			ast.setUser(new User((String) o[8], (String) o[9], (String) o[13],
					(String) o[10]));
			ast.getUser().setIbo(new Profile((String) o[11], (String) o[12]));

			list.add(ast);
		}

		return list;

	}

	@Override
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		Criteria criteria = super.formCriteriaByListAndCount(filter)
				.createAlias("user", "user", JoinType.INNER_JOIN);
		criteria.createAlias("user.profile", "p");

		AgentStateDetailFilter asdFilter = (AgentStateDetailFilter) filter;

		if (asdFilter.getStartDate() != null && asdFilter.getEndDate() != null) {

			Calendar ci = Calendar.getInstance();
			ci.setTime(asdFilter.getStartDate());
			ci.set(Calendar.SECOND, 0);
			ci.set(Calendar.MILLISECOND, 0);

			Calendar ce = Calendar.getInstance();
			ce.setTime(asdFilter.getEndDate());
			ce.set(Calendar.SECOND, 59);
			ce.set(Calendar.MILLISECOND, 999);

			criteria.add(Restrictions.or(
					Restrictions.between("startDate", ci.getTime(),
							ce.getTime()),
					Restrictions.between("endDate", ci.getTime(), ce.getTime())));

			// criteria.add(Restrictions.between("startDate", ci.getTime(),
			// ce.getTime()));
		}
		if (StringUtils.isNotBlank(asdFilter.getIboNumber()))
			criteria.add(Restrictions.ilike("p.number",
					asdFilter.getIboNumber(), MatchMode.ANYWHERE));

		if (StringUtils.isNotBlank(asdFilter.getUserId()))
			criteria.add(Restrictions.eq("user.id", asdFilter.getUserId()));

		if (asdFilter.getEventId() != null
				&& asdFilter.getEventId().getTarget().size() > 0) {
			List<Object[]> events = asdFilter.getEventId().getTarget();
			List<Integer> eventId = new ArrayList<Integer>();
			for (Object[] o : events) {
				eventId.add((Integer) o[0]);
			}
			criteria.add(Restrictions.in("eventType", eventId));
		}

		return criteria;
	}

	@Override
	public Long getCountByDate(Date date) {

		Query query = getSession()
				.createQuery(
						"select count(*) from AgentStateDetail a where cast(a.startDate as date) = :date ");
		query.setDate("date", date);

		return (Long) query.uniqueResult();
	}

	@Override
	public List<String> getIdByDate(Date date) {

		Query query = getSession()
				.createQuery(
						"select id from AgentStateDetail a where cast(a.startDate as date) = :date ");
		query.setDate("date", date);

		return query.list();
	}

	public List<AgentStateDetail> getDataForSchedule(String userId,
			Date startDate, Date endDate) {

		Query query = getSession()
				.createQuery(
						"from AgentStateDetail  "
								+ "where user.id =:userId "
								+ "and (invoiceWork.id is null or durationPending > 0) "
								+ "and (startDate between :startDate and :endDate or endDate between :startDate and :endDate or (:startDate between startDate and endDate and :endDate between startDate and endDate)) "
								+ "order by user.id ASC, startDate ASC");

		query.setString("userId", userId);
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);

		return query.list();

		// Criteria criteria =
		// getSession().createCriteria(AgentStateDetail.class);
		// criteria.add(Restrictions.or(
		// Restrictions.between("startDate", startDate, endDate),
		// Restrictions.between("endDate", startDate, endDate),
		// Restrictions.isNotNull("durationPending")));
		//
		// criteria.add(Restrictions.eq("user.id", userId));
		// criteria.add(Restrictions.or(Restrictions.isNull("invoiceWork"),
		// Restrictions.isNotNull("durationPending")));
		// criteria.addOrder(Order.asc("user.id"));
		// criteria.addOrder(Order.asc("startDate"));
		//
		// return criteria.list();

	}

	public AgentStateDetail getWorkAfterTalking(String userId, Date endDate) {

		Criteria criteria = getSession().createCriteria(AgentStateDetail.class);
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("eventType", EventType.WORK));
		criteria.add(Restrictions.eq("startDate", endDate));

		return (AgentStateDetail) criteria.uniqueResult();

	}

	public int update(List<String> ids, InvoiceWork invoiceWork) {
		if (ids != null && ids.size() > 0) {
			Query query = getSession()
					.createQuery(
							"update AgentStateDetail set invoiceWork.id =:invoice where id IN (:ids)");
			query.setParameterList("ids", ids);
			query.setString("invoice", invoiceWork.getId());

			return query.executeUpdate();
		}
		return 0;
	}

	public void updatePendingDuration(Set<AgentStateDetail> asd) {

		for (AgentStateDetail a : asd) {

			Query query = getSession()
					.createQuery(
							"update AgentStateDetail set durationPending =:duration "
									+ (a.getInvoiceWork() != null ? ",invoiceWorkPending.id =:invoice"
											: "") + " where id =:id");
			query.setString("id", a.getId());
			query.setInteger("duration", a.getDurationPending());

			if (a.getInvoiceWork() != null)
				query.setString("invoice", a.getInvoiceWork().getId());

			query.executeUpdate();
		}
	}

	public List<AgentStateDetail> getDataByInvoice(Invoice invoice) {

		Query query = getSession()
				.createQuery(
						"select new AgentStateDetail(a.id,a.startDate,a.endDate,a.place,a.eventType,a.reasonCode,a.duration,a.needUpdated,a.invoiceWork.id, a.reasonCodeDescription) "
								+ "from AgentStateDetail a where (cast(a.startDate as date) between :dateIni and :dateEnd or cast(a.endDate as date) between :dateIni and :dateEnd)"
								+ " and a.user.id = :userId order by a.startDate ASC,a.endDate ASC");
		query.setDate("dateIni", invoice.getStart());
		query.setDate("dateEnd", invoice.getEnd());
		query.setString("userId", invoice.getUser().getUser().getId());

		return query.list();
	}

}