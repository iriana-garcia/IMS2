package com.ghw.dao.impl;

import java.util.Date;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.ghw.dao.CategoryDAO;
import com.ghw.model.Category;
import com.ghw.model.User;

@Repository("categoryDAO")
public class CategoryDAOImpl extends GenericHibernateDAO<Category, String>
		implements CategoryDAO {

	/**
	 * Change the state and save the date and the user that make the action
	 */
	@Override
	public void changeState(Category entity, User user) {
		Query query = getSession()
				.createQuery(
						"update Category set state =:state, updateDate=:dateUpdate, userUpdated=:user where id=:id");
		query.setString("id", entity.getId());
		query.setBoolean("state", !entity.isState());
		query.setTimestamp("dateUpdate", new Date());
		query.setEntity("user", user);

		query.executeUpdate();

	}

}