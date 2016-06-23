package com.ghw.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.IboTemporalDAO;
import com.ghw.model.IboTemporal;
import com.ghw.model.User;

@Repository("iboTemporalDAO")
public class IboTemporalDAOImpl extends
		GenericHibernateDAO<IboTemporal, String> implements IboTemporalDAO {

	/**
	 * Get all the IBO temporal's name
	 */
	@Override
	public List<String> getDataName() {

		Criteria criteria = getSession().createCriteria(IboTemporal.class);
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("userName"));

		criteria.setProjection(projection);

		return criteria.list();
	}

	/**
	 * Get the IBO temporal by name
	 */
	@Override
	public IboTemporal getIboByName(String name) {
		Criteria criteria = getSession().createCriteria(IboTemporal.class);
		criteria.add(Restrictions.eq("userName", name));

		return (IboTemporal) criteria.uniqueResult();
	}

	/**
	 * delete all the Ibos temporal included in the list of names
	 */
	@Override
	public void delete(List<String> names) {
		if (names != null && names.size() > 0) {
			Query query = getSession().createQuery(
					"delete from IboTemporal where userName IN (:names)");
			query.setParameterList("names", names);

			query.executeUpdate();
		}

	}

	/**
	 * Change the state and save the date and the user that make the action
	 */
	@Override
	public void changeState(IboTemporal ibo, User userModify) {
		Query query = getSession().createQuery(
				"update IboTemporal set state =:state, updateDate=:dateUpdate,"
						+ (userModify == null ? "userUpdated=null"
								: "userUpdated=:user") + "  where id=:id");
		query.setString("id", ibo.getId());
		query.setBoolean("state", !ibo.isState());
		query.setTimestamp("dateUpdate", new Date());
		if (userModify != null) {
			query.setEntity("user", userModify);
		}

		query.executeUpdate();

	}

	/**
	 * delete an Ibo temporal by name
	 */
	@Override
	public void delete(String name) {
		Query query = getSession().createQuery(
				"delete from IboTemporal where userName=:name");
		query.setString("name", name);

		query.executeUpdate();

	}
}