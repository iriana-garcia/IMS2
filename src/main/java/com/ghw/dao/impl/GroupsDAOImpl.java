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

import com.ghw.dao.GroupsDAO;
import com.ghw.filter.FilterBase;
import com.ghw.model.Groups;
import com.ghw.model.User;

@Repository("groupsDAO")
public class GroupsDAOImpl extends GenericHibernateDAO<Groups, String>
		implements GroupsDAO {

	/**
	 * Get the data for the table with filter
	 */
	@Override
	public List<Groups> getData(FilterBase filter) throws Exception {
		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("name"));
		projection.add(Property.forName("state"));
		projection.add(Property.forName("u.id"));
		projection.add(Property.forName("u.name"));

		projection
				.add(Projections.alias(
						Projections
								.sqlProjection(
										"(select count(*) from profile s where s.grp_id={alias}.grp_id) as totalIbo",
										new String[] { "totalIbo" },
										new Type[] { new IntegerType() }),
						"totalIbo"), "totalIbo");
		projection.add(Property.forName("u.firstName"));
		projection.add(Property.forName("u.middleName"));
		projection.add(Property.forName("u.lastName"));

		criteria.setProjection(projection);

		if (filter.getSortField().equals("user.nameTable")) {

			if (StringUtils.equalsIgnoreCase(filter.getSortOrder().name(),
					"ascending")) {
				criteria.addOrder(Order.asc("u.lastName"))
						.addOrder(Order.asc("u.middleName"))
						.addOrder(Order.asc("u.firstName"));
			} else {
				criteria.addOrder(Order.desc("u.lastName"))
						.addOrder(Order.desc("u.middleName"))
						.addOrder(Order.desc("u.firstName"));
			}

		} else if (StringUtils.isEmpty(filter.getSortField())) {
			criteria.addOrder(Order.asc("name"));
		} else {

			if (StringUtils.equalsIgnoreCase(filter.getSortOrder().name(),
					"ascending"))
				criteria.addOrder(Order.asc(filter.getSortField()));
			else
				criteria.addOrder(Order.desc(filter.getSortField()));
		}

		if (filter.getNumberOfRows() > 0) {
			criteria.setMaxResults(filter.getNumberOfRows());
			criteria.setFirstResult(filter.getFirstRow());
		}

		List<Object[]> list = criteria.list();
		List<Groups> groups = new ArrayList<Groups>();
		for (Object[] o : list) {
			Groups g = new Groups();
			g.setId((String) o[0]);
			g.setName((String) o[1]);
			g.setState((Boolean) o[2]);
			User user = new User();
			user.setId((String) o[3]);
			user.setName((String) o[4]);
			user.setFirstName((String) o[6]);
			user.setMiddleName((String) o[7]);
			user.setLastName((String) o[8]);
			g.setUser(user);
			g.setTotalIbo((Integer) o[5]);
			groups.add(g);
		}
		return groups;
	}

	@Override
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		return super.formCriteriaByListAndCount(filter).createAlias("user",
				"u", JoinType.LEFT_OUTER_JOIN);
	}

	/**
	 * Change the state and save the date and the user that make the action
	 */
	@Override
	public void changeState(Groups entity, User user) {
		Query query = getSession()
				.createQuery(
						"update Groups set state =:state, updateDate=:dateUpdate, userUpdated=:user, user.id=null where id=:id");
		query.setString("id", entity.getId());
		query.setBoolean("state", !entity.isState());
		query.setTimestamp("dateUpdate", new Date());
		query.setEntity("user", user);

		query.executeUpdate();

	}

	/**
	 * Load all the Groups data
	 */
	@Override
	public Groups loadAllById(Groups entity) throws Exception {

		Query query = getSession().createQuery(
				"select g from Groups g left join fetch g.user u "
						+ " left join fetch g.userCreated uc "
						+ " left join fetch g.userUpdated uu where g.id =:id");
		query.setString("id", entity.getId());

		return (Groups) query.uniqueResult();
	}

	/**
	 * Get all the groups that does not have an user associate
	 */
	@Override
	public List<Groups> getDataWithoutUser(User user) {
		Criteria criteria = getSession().createCriteria(Groups.class)
				.createAlias("user", "u", JoinType.LEFT_OUTER_JOIN)
				.addOrder(Order.asc("name"));

		if (user != null) {
			criteria.add(Restrictions.or(Restrictions.isNull("u.id"),
					Restrictions.ne("u.id", user.getId())));
		}

		criteria.add(Restrictions.eq("state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("name"));

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				Groups.class));

		return criteria.list();
	}

	/**
	 * Get all the groups that does have an user associate
	 */
	@Override
	public List<Groups> getDataWithUser(User user) {
		Criteria criteria = getSession().createCriteria(Groups.class)
				.createAlias("user", "u", JoinType.LEFT_OUTER_JOIN)
				.addOrder(Order.asc("name"));

		if (user != null) {
			criteria.add(Restrictions.or(Restrictions.isNull("u.id"),
					Restrictions.eqOrIsNull("u.id", user.getId())));
		} else {
			criteria.add(Restrictions.isNull("u.id"));
		}

		criteria.add(Restrictions.eq("state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");
		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("name"));

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				Groups.class));

		return criteria.list();
	}

	/**
	 * update the group's user
	 */
	@Override
	public void updateUser(Groups group, User user) {
		String queryString = user == null ? "update Groups set user.id=null where id=:id"
				: "update Groups set user.id=:user where id=:id";
		Query query = getSession().createQuery(queryString).setString("id",
				group.getId());
		if (user != null) {
			query.setString("user", user.getId());
		}
		query.executeUpdate();
	}

	/**
	 * Get the group associate to an user
	 */
	@Override
	public Groups getDataByUser(User user) {
		Criteria criteria = getSession().createCriteria(Groups.class);
		criteria.add(Restrictions.eq("user.id", user.getId()));

		criteria.addOrder(Order.asc("name"));

		List<Groups> list = criteria.list();

		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	/**
	 * Remove the group in the IBO
	 */
	@Override
	public void clearUser(User user) {

		if (user != null) {

			Query query = getSession().createQuery(
					"update Groups set user.id=null where user.id=:id");
			query.setString("id", user.getId());

			query.executeUpdate();
		}

	}

	/**
	 * get the data ordered by name ascending in active state, only get the
	 * field id and name
	 * 
	 * @return
	 */
	@Override
	public List<Groups> getDataActive() {

		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.addOrder(Order.asc("name"))
				.add(Restrictions.eq("state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");
		projection.add(Property.forName("user.id"), "userId");

		criteria.setProjection(projection);

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				getPersistentClass()));

		return criteria.list();

	}
}