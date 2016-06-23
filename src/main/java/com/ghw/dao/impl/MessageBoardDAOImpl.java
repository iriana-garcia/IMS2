package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.ghw.dao.MessageBoardDAO;
import com.ghw.filter.FilterBase;
import com.ghw.filter.MessageBoardFilter;
import com.ghw.model.Corporation;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceState;
import com.ghw.model.MessageBoard;
import com.ghw.model.Profile;
import com.ghw.model.User;

@Repository("messageBoardDAO")
public class MessageBoardDAOImpl extends
		GenericHibernateDAO<MessageBoard, String> implements MessageBoardDAO {

	/**
	 * Get the data for the table with filter
	 */
	@Override
	public List<MessageBoard> getData(FilterBase filter) throws Exception {

		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();

		projection.add(Property.forName("id"));
		projection.add(Property.forName("dateMessage"));
		projection.add(Property.forName("message"));
		projection.add(Property.forName("dateReplyMessage"));
		projection.add(Property.forName("replyMessage"));
		projection.add(Property.forName("userReply.id"));
		projection.add(Property.forName("userReply.name"));
		projection.add(Property.forName("invoice.id"));
		projection.add(Property.forName("invoice.number"));
		projection.add(Property.forName("ibo.id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("ibo.number"));
		projection.add(Property.forName("user.id"));
		projection.add(Property.forName("user.email"));
		projection.add(Property.forName("invoice.id"));
		projection.add(Property.forName("invoice.state.id"));
		projection.add(Property.forName("user.type.id"));
		projection.add(Property.forName("user.firstName"));
		projection.add(Property.forName("user.lastName"));
		projection.add(Property.forName("corporation.id"));

		criteria.setProjection(projection);

		if (StringUtils.isEmpty(filter.getSortField())) {
			criteria.addOrder(Order.asc("ibo.number"));
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

		List<Object[]> list = criteria.list();
		List<MessageBoard> messageBoard = new ArrayList<MessageBoard>();
		for (Object[] o : list) {
			MessageBoard m = new MessageBoard();
			m.setId((String) o[0]);
			m.setDateMessage((Date) o[1]);
			m.setMessage((String) o[2]);
			m.setDateReplyMessage((Date) o[3]);
			m.setReplyMessage((String) o[4]);
			m.setUserReply(new User((String) o[5], (String) o[6]));
			Invoice invoice = new Invoice();
			invoice.setId((String) o[7]);
			invoice.setNumber((String) o[8]);
			invoice.setUser(new Profile((String) o[9], new Corporation(
					(String) o[19], (String) o[10]), (String) o[11],
					(String) o[12], (String) o[13], (String) o[16],
					(String) o[17], (String) o[18]));
			invoice.setState(new InvoiceState((String) o[15]));
			// invoice.setHaveQuestion(m.getDateReplyMessage() == null ? 2 : 1);
			m.setInvoice(invoice);

			messageBoard.add(m);
		}

		return messageBoard;

	}

	@Override
	public Long count(FilterBase filter) throws Exception {
		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();

		projection.add(Projections.countDistinct("invoice.id"));
		// projection.add(Projections.groupProperty("invoice.id"));

		criteria.setProjection(projection);

		return (Long) criteria.list().get(0);
	}

	@Override
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		Criteria criteria = getSession().createCriteria(MessageBoard.class);
		criteria.createAlias("userReply", "userReply", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("invoice", "invoice");
		criteria.createAlias("invoice.user", "ibo");
		criteria.createAlias("ibo.user", "user");
		criteria.createAlias("ibo.corporation", "corporation");
		criteria.createAlias("messageBoardGroups", "group");

		MessageBoardFilter messageFilter = (MessageBoardFilter) filter;

		// DetachedCriteria messSubquery = DetachedCriteria.forClass(
		// MessageBoard.class, "mess");
		// ProjectionList projection = Projections.projectionList();
		//
		// projection.add(Property.forName("mess.id"));
		// projection.add(Projections.max("mess.dateMessage"));
		// projection.add(Projections.groupProperty("mess.invoice.id"));
		//
		// messSubquery.setProjection(projection);
		//
		// // criteria.createAlias("issueTracker", "it", Criteria.INNER_JOIN,
		// // Subqueries.eqProperty("it.id", messSubquery));
		//
		// criteria.add(Subqueries.in("id", messSubquery));

		if (filter.getFilters() != null) {
			for (Iterator<String> it = filter.getFilters().keySet().iterator(); it
					.hasNext();) {

				String filterProperty = it.next();
				Object filterValue = filter.getFilters().get(filterProperty);

				if (!filterProperty.equals("dateReplyMessage")) {
					if (filterValue instanceof String) {
						criteria.add(Restrictions.ilike(filterProperty,
								(String) filterValue, MatchMode.ANYWHERE));
					} else if (filterValue instanceof Integer) {
						criteria.add(Restrictions.eq(filterProperty,
								(Integer) filterValue));
					} else if (filterValue instanceof Date) {
						criteria.add(Restrictions.eq(filterProperty,
								(Date) filterValue));
					} else if (filterValue instanceof Boolean) {
						criteria.add(Restrictions.eq(filterProperty,
								(Boolean) filterValue));
					}
				}
			}
		}

		if (messageFilter.getStartDate() != null
				&& messageFilter.getEndDate() == null) {
			criteria.add(Restrictions.sqlRestriction("date(dateMessage) = ? ",
					new Date[] { messageFilter.getStartDate() },
					new Type[] { StandardBasicTypes.DATE }));
		}

		if (messageFilter.getStartDate() != null
				&& messageFilter.getEndDate() != null) {
			criteria.add(Restrictions.sqlRestriction(
					"date(dateMessage) between ? and ?",
					new Date[] { messageFilter.getStartDate(),
							messageFilter.getEndDate() }, new Type[] {
							StandardBasicTypes.DATE, StandardBasicTypes.DATE }));
		}

		if (messageFilter.getStatus() != null
				&& messageFilter.getStatus() != -1) {
			criteria.add(messageFilter.getStatus().equals(1) ? Restrictions
					.isNull("dateReplyMessage") : Restrictions
					.isNotNull("dateReplyMessage"));
		}

		return criteria;
	}

	public List<MessageBoard> getDataByInvoice(Invoice invoice) {

		Criteria criteria = getSession().createCriteria(MessageBoard.class);

		criteria.createAlias("userReply", "userReply", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("invoice", "invoice");
		criteria.createAlias("invoice.user", "ibo");
		criteria.createAlias("ibo.user", "user");

		criteria.add(Restrictions.eq("invoice.id", invoice.getId()));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("dateMessage"));
		projection.add(Property.forName("message"));
		projection.add(Property.forName("dateReplyMessage"));
		projection.add(Property.forName("replyMessage"));
		projection.add(Property.forName("userReply.id"));
		projection.add(Property.forName("userReply.name"));
		projection.add(Property.forName("invoice.id"));
		projection.add(Property.forName("invoice.number"));
		projection.add(Property.forName("ibo.id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("ibo.number"));
		projection.add(Property.forName("user.id"));
		projection.add(Property.forName("corporation.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("dateMessage"));

		List<Object[]> list = criteria.list();
		List<MessageBoard> messageBoard = new ArrayList<MessageBoard>();
		for (Object[] o : list) {
			MessageBoard m = new MessageBoard();
			m.setId((String) o[0]);
			m.setDateMessage((Date) o[1]);
			m.setMessage((String) o[2]);
			m.setDateReplyMessage((Date) o[3]);
			m.setReplyMessage((String) o[4]);
			m.setUserReply(new User((String) o[5], (String) o[6]));
			Invoice i = new Invoice();
			i.setId((String) o[7]);
			i.setNumber((String) o[8]);
			i.setUser(new Profile((String) o[9], new Corporation(
					(String) o[13], (String) o[10]), (String) o[11],
					(String) o[12], null));
			m.setInvoice(i);

			messageBoard.add(m);
		}

		return messageBoard;
	}

}