package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import com.ghw.dao.SkillPhoneSystemDAO;
import com.ghw.filter.FilterBase;
import com.ghw.model.Skill;
import com.ghw.model.SkillPhoneSystem;

@Repository("skillPhoneSystemDAO")
public class SkillPhoneSystemDAOImpl extends
		GenericHibernateDAO<SkillPhoneSystem, String> implements
		SkillPhoneSystemDAO {

	@Override
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		Criteria criteria = getSession().createCriteria(SkillPhoneSystem.class);

		if (filter.getFilters() != null) {
			for (Iterator<String> it = filter.getFilters().keySet().iterator(); it
					.hasNext();) {

				String filterProperty = it.next();
				Object filterValue = filter.getFilters().get(filterProperty);

				// always exclude the field that included another field
				if (filterProperty.equals("phoneSystemId")) {
					criteria.add(Restrictions.eq(filterProperty, new Integer(
							(String) filterValue)));
				} else if (filterProperty.equals("type")) {
					criteria.add(Restrictions.eq(filterProperty, new Short(
							(String) filterValue)));
				} else if (filterValue instanceof String) {
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

		criteria.createAlias("skill", "skill", JoinType.LEFT_OUTER_JOIN);

		return criteria;
	}

	/**
	 * Get the data for the table with filter
	 */
	@Override
	public List<SkillPhoneSystem> getData(FilterBase filter) throws Exception {
		Criteria criteria = formCriteriaByListAndCount(filter);
		criteria.setFetchMode("skill", FetchMode.JOIN);

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

	@Override
	public void update(List<SkillPhoneSystem> skills, Skill s) {
		if (skills != null && skills.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (SkillPhoneSystem sp : skills)
				ids.add(sp.getId());

			Query query = getSession().createQuery(
					"update SkillPhoneSystem set skill=:ski where id IN(:ids)");
			query.setEntity("ski", s);
			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	@Override
	public List<SkillPhoneSystem> getDataWithSkillId() {

		Query query = getSession()
				.createQuery(
						"select new SkillPhoneSystem(id, name, skill.id, place,phoneSystemId) from SkillPhoneSystem where state = true order by name ASC ");

		return query.list();
	}

	@Override
	public List<SkillPhoneSystem> getDataWithoutSkills(Skill skill) {

		Query query = getSession()
				.createQuery(
						"select new SkillPhoneSystem(id, name, skill.id, place,phoneSystemId) from SkillPhoneSystem where (skill.id is null or skill.id =:idSkill) and state = true order by name ASC ");
		query.setString("idSkill", skill.getId());

		return query.list();
	}

	@Override
	public List<SkillPhoneSystem> getDataWithoutSkills() {
		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.addOrder(Order.asc("name"));
		// ProjectionList projection = Projections.projectionList();
		// projection.add(Property.forName("id"), "id");
		// projection.add(Property.forName("name"), "name");
		//
		// criteria.setProjection(projection);
		//
		// criteria.setResultTransformer(new AliasToBeanResultTransformer(
		// getPersistentClass()));

		criteria.add(Restrictions.isNull("skill"));
		criteria.add(Restrictions.eq("state", true));

		return criteria.list();
	}

	/**
	 * Get all the skills associate to a client application
	 */
	@Override
	public List<SkillPhoneSystem> getDataBySkill(Skill skill) {
		Criteria criteria = getSession().createCriteria(SkillPhoneSystem.class);

		criteria.add(Restrictions.eq("skill.id", skill.getId()));

		criteria.addOrder(Order.asc("name"));

		return criteria.list();
	}

	/**
	 * remove all the client application from the list of skills
	 */
	@Override
	public void clearSkills(List<SkillPhoneSystem> skills) {
		if (skills != null && skills.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (SkillPhoneSystem s : skills)
				ids.add(s.getId());

			Query query = getSession().createQuery(
					"update SkillPhoneSystem set skill=null where id IN(:ids)");

			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	@Override
	public void clearSkills(Skill skill) {

		Query query = getSession()
				.createQuery(
						"update SkillPhoneSystem s set s.skill=null where s.skill.id =:id");

		query.setString("id", skill.getId());

		query.executeUpdate();

	}

}