package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.ghw.dao.ClientApplicationDAO;
import com.ghw.filter.FilterBase;
import com.ghw.model.ClientApplication;
import com.ghw.model.Profile;
import com.ghw.model.User;

@Repository("clientApplicationDAO")
public class ClientApplicationDAOImpl extends
		GenericHibernateDAO<ClientApplication, String> implements
		ClientApplicationDAO {

	/**
	 * Get the data for the table with filter
	 */
	@Override
	public List<ClientApplication> getData(FilterBase filter) throws Exception {
		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");
		projection.add(Property.forName("amount"), "amount");
		projection.add(Property.forName("state"), "state");

		projection
				.add(Projections.alias(
						Projections
								.sqlProjection(
										"(select count(*) from skill s where s.cli_id={alias}.cli_id) as totalSkills",
										new String[] { "totalSkills" },
										new Type[] { new IntegerType() }),
						"totalSkills"), "totalSkills");

		projection
				.add(Projections.alias(
						Projections
								.sqlProjection(
										"(select count(*) from event s where s.cli_id={alias}.cli_id) as totalEvent",
										new String[] { "totalEvent" },
										new Type[] { new IntegerType() }),
						"totalEvent"), "totalEvent");

		projection
				.add(Projections.alias(
						Projections
								.sqlProjection(
										"(select count(*) from ibos_client_applications s where s.cli_id={alias}.cli_id) as totalIbo",
										new String[] { "totalIbo" },
										new Type[] { new IntegerType() }),
						"totalIbo"), "totalIbo");

		criteria.setProjection(projection);

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				ClientApplication.class));

		// default filter
		if (StringUtils.isEmpty(filter.getSortField())) {
			criteria.addOrder(Order.asc("name"));
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

		return criteria.list();
	}

	/**
	 * Change the Client application state and save the date and the user that
	 * make the action
	 */
	@Override
	public void changeState(ClientApplication entity, User user) {
		Query query = getSession()
				.createQuery(
						"update ClientApplication set state =:state, updateDate=:dateUpdate, userUpdated=:user where id=:id");
		query.setString("id", entity.getId());
		query.setBoolean("state", !entity.isState());
		query.setTimestamp("dateUpdate", new Date());
		query.setEntity("user", user);

		query.executeUpdate();

	}

	/**
	 * Load all the clients applications data
	 */
	@Override
	public List<ClientApplication> loadAllById(FilterBase filter)
			throws Exception {

		Criteria criteria = formCriteriaByListAndCount(filter);

		criteria.createCriteria("userCreated", JoinType.INNER_JOIN)
				.createCriteria("userUpdated", JoinType.LEFT_OUTER_JOIN);

		// criteria.add(Restrictions.eq("id", ca.getId()));

		if (StringUtils.equalsIgnoreCase(filter.getSortOrder().name(),
				"ascending")) {
			criteria.addOrder(Order.asc(filter.getSortField()));
		} else {
			criteria.addOrder(Order.desc(filter.getSortField()));
		}
		if (filter.getNumberOfRows() > 0) {
			criteria.setMaxResults(filter.getNumberOfRows());
			criteria.setFirstResult(filter.getFirstRow());
		}

		return criteria.list();
	}

	/**
	 * Load all the client application data
	 */
	@Override
	public ClientApplication loadAllById(ClientApplication ca) throws Exception {

		// Criteria criteria = getSession()
		// .createCriteria(ClientApplication.class);
		//
		// criteria.createCriteria("userCreated", JoinType.INNER_JOIN)
		// .createCriteria("userUpdated", JoinType.LEFT_OUTER_JOIN);
		//
		// criteria.add(Restrictions.eq("id", ca.getId()));
		//
		// return (ClientApplication) criteria.uniqueResult();

		Query query = getSession().createQuery(
				"select g from ClientApplication g  "
						+ " left join fetch g.userCreated uc "
						+ " left join fetch g.userUpdated uu where g.id =:id");
		query.setString("id", ca.getId());

		return (ClientApplication) query.uniqueResult();

	}

	/**
	 * Get all the client application associate to an IBO
	 */
	@Override
	public List<ClientApplication> getDataByIbo(Profile profile) {

		Criteria criteria = getSession()
				.createCriteria(ClientApplication.class).createAlias("ibos",
						"i");
		criteria.add(Restrictions.eq("i.user.id", profile.getId()));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");
		projection.add(Property.forName("amount"), "amount");

		criteria.setProjection(projection);

		criteria.add(Restrictions.eq("state", true));

		criteria.addOrder(Order.asc("name"));

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				ClientApplication.class));

		return criteria.list();
	}

	/**
	 * Get all the client application NOT associate to an IBO
	 */
	@Override
	public List<ClientApplication> getDataByNotIbo(Profile profile) {

		Criteria criteria = getSession()
				.createCriteria(ClientApplication.class).createAlias("ibos",
						"i", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.or(
				Restrictions.ne("i.user.id", profile.getId()),
				Restrictions.isNull("i.user.id")));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");

		criteria.setProjection(Projections.distinct(projection));

		criteria.addOrder(Order.asc("name"));

		criteria.add(Restrictions.eq("state", true));

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				ClientApplication.class));

		return criteria.list();
	}

	@Override
	public List<ClientApplication> getDataByEditIbo(Profile profile) {

		Criteria criteria = getSession()
				.createCriteria(ClientApplication.class).createAlias("ibos",
						"i", JoinType.LEFT_OUTER_JOIN,
						Restrictions.eq("i.user.id", profile.getId()));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("name"));
		projection.add(Property.forName("i.id"));

		criteria.setProjection(Projections.distinct(projection));

		criteria.add(Restrictions.eq("state", true));

		criteria.addOrder(Order.asc("name"));

		// criteria.setResultTransformer(new AliasToBeanResultTransformer(
		// ClientApplication.class));

		List<Object[]> objects = criteria.list();
		List<ClientApplication> list = new ArrayList<ClientApplication>();
		for (Object[] o : objects) {
			ClientApplication ca = new ClientApplication();
			ca.setId((String) o[0]);
			ca.setName((String) o[1]);
			ca.setIboAssociate(o[2] != null);
			list.add(ca);
		}

		return list;
	}

	/**
	 * Get count CA by IBO
	 */
	@Override
	public Long getCountByIbo(Profile profile) {

		Criteria criteria = getSession()
				.createCriteria(ClientApplication.class).createAlias("ibos",
						"i");
		criteria.add(Restrictions.eq("i.user.id", profile.getId()));

		criteria.setProjection(Projections.rowCount());

		return (Long) criteria.list().get(0);
	}
}