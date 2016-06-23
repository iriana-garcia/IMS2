package com.ghw.dao.impl;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.RolDAO;
import com.ghw.model.Rol;
import com.ghw.model.User;

@Repository("rolDAO")
public class RolDAOImpl extends GenericHibernateDAO<Rol, String> implements
		RolDAO {

	/**
	 * validate if the rol has an users associate
	 */
	@Override
	public boolean validateUserAssociate(Rol rol) {
		Criteria criteria = getSession().createCriteria(User.class).add(
				Restrictions.eq("rol.id", rol.getId()));
		criteria.setProjection(Projections.rowCount());

		return (Long) criteria.list().get(0) > 0 ? true : false;
	}

	/**
	 * Change the Rol state and save the date and the user that make the action
	 */

	@Override
	public void changeState(Rol rol, User user) {
		Query query = getSession()
				.createQuery(
						"update Rol set state =:state, updateDate=:dateUpdate, userUpdated=:user where id=:id");
		query.setString("id", rol.getId());
		query.setBoolean("state", !rol.isState());
		query.setTimestamp("dateUpdate", new Date());
		query.setEntity("user", user);

		query.executeUpdate();
	}

}