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
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.ghw.dao.SkillDAO;
import com.ghw.filter.FilterBase;
import com.ghw.model.ClientApplication;
import com.ghw.model.Skill;
import com.ghw.model.User;

@Repository("skillDAO")
public class SkillDAOImpl extends GenericHibernateDAO<Skill, String> implements
		SkillDAO {

	@Override
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		Criteria criteria = super.formCriteriaByListAndCount(filter);
		criteria.createAlias("clientApplication", "ca",
				JoinType.LEFT_OUTER_JOIN);

		return criteria;
	}

	/**
	 * Get the data for the table with filter
	 */
	@Override
	public List<Skill> getData(FilterBase filter) throws Exception {
		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("name"));
		projection.add(Property.forName("state"));
		projection.add(Property.forName("ca.id"));
		projection.add(Property.forName("ca.name"));
		projection
				.add(Projections.alias(
						Projections
								.sqlProjection(
										"(select count(*) from skill_phone_system s where s.ski_id={alias}.ski_id) as totalSkills",
										new String[] { "totalSkills" },
										new Type[] { new IntegerType() }),
						"totalSkills"), "totalSkills");

		criteria.setProjection(projection);

		// criteria.setResultTransformer(new AliasToBeanResultTransformer(
		// Skill.class));

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

		List<Object[]> list = criteria.list();
		List<Skill> skills = new ArrayList<Skill>();
		for (Object[] o : list) {
			Skill skill = new Skill();
			skill.setId((String) o[0]);
			skill.setName((String) o[1]);
			skill.setState((Boolean) o[2]);
			skill.setClientApplication(new ClientApplication((String) o[3],
					(String) o[4]));
			skill.setTotalSkills((Integer) o[5]);

			skills.add(skill);
		}

		return skills;
	}

	/**
	 * Update the client application associate to skills
	 */
	@Override
	public void update(List<Skill> skills, ClientApplication ca) {
		if (skills != null && skills.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Skill s : skills)
				ids.add(s.getId());

			Query query = getSession().createQuery(
					"update Skill set clientApplication=:ca where id IN(:ids)");
			query.setEntity("ca", ca);
			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	/**
	 * remove all the client application from the list of skills
	 */
	@Override
	public void clearClientApplication(List<Skill> skills) {
		if (skills != null && skills.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Skill s : skills)
				ids.add(s.getId());

			Query query = getSession()
					.createQuery(
							"update Skill set clientApplication=null where id IN(:ids)");

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
						"update Skill s set s.clientApplication=null where s.clientApplication.id =:id");

		query.setString("id", ca.getId());

		query.executeUpdate();

	}

	/**
	 * Get all the skills without client application associate
	 */
	@Override
	public List<Skill> getDataWithoutCA() {
		Criteria criteria = getSession().createCriteria(Skill.class).addOrder(
				Order.asc("name"));

		criteria.add(Restrictions.isNull("clientApplication"));
		return criteria.list();
	}

	/**
	 * Get all the skills without client application associate or with the
	 * client application passed to a method
	 */
	@Override
	public List<Skill> getDataWithoutCA(ClientApplication ca) {
		Criteria criteria = getSession().createCriteria(Skill.class).addOrder(
				Order.asc("name"));

		criteria.add(Restrictions.or(
				Restrictions.isNull("clientApplication.id"),
				Restrictions.eqOrIsNull("clientApplication.id", ca.getId())));

		return criteria.list();
	}

	/**
	 * Get all the skills associate to a client application
	 */
	@Override
	public List<Skill> getDataByCA(ClientApplication ca) {
		Criteria criteria = getSession().createCriteria(Skill.class);

		criteria.add(Restrictions.eq("clientApplication.id", ca.getId()));

		criteria.addOrder(Order.asc("name"));

		return criteria.list();
	}

	@Override
	public void changeState(Skill entity, User user) {
		Query query = getSession()
				.createQuery(
						"update Skill set state =:state, updateDate=:dateUpdate, userUpdated=:user, clientApplication = null where id=:id");
		query.setString("id", entity.getId());
		query.setBoolean("state", !entity.isState());
		query.setTimestamp("dateUpdate", new Date());
		query.setEntity("user", user);

		query.executeUpdate();

	}

	/**
	 * Load all the client application data
	 */
	@Override
	public Skill loadAllById(Skill skill) throws Exception {

		Query query = getSession().createQuery(
				"select g from Skill g  "
						+ " left join fetch g.clientApplication ca "
						+ " left join fetch g.userCreated uc "
						+ " left join fetch g.userUpdated uu where g.id =:id");
		query.setString("id", skill.getId());

		return (Skill) query.uniqueResult();

	}
	// /**
	// * Get all the skills associate to a client application
	// */
	// @Override
	// public List<Skill> getDataByInvoice(Invoice invoice) {
	//
	// if (invoice != null && StringUtils.isNotBlank(invoice.getId())) {
	// Criteria criteria = getSession().createCriteria(Skill.class)
	// .createAlias("invoiceWorks", "i");
	// criteria.add(Restrictions.eq("i.invoice.id", invoice.getId()));
	//
	// criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
	//
	//
	// criteria.addOrder(Order.asc("name"));
	//
	// return criteria.list();
	// }
	// return null;
	// }
}