package com.ghw.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.ghw.dao.MenuDAO;
import com.ghw.model.Menu;
import com.ghw.model.User;

@Repository("menuDAO")
public class MenuDAOImpl extends GenericHibernateDAO<Menu, Integer> implements
		MenuDAO {

	/**
	 * Get all the user's menu
	 */
	@Override
	public List<Menu> getDataByUser(User user) {

		Query query = null;

		if (user.isSuperAdmin()) {
			query = getSession()
					.createQuery(
							"select m from Menu m left join fetch m.menuFather order by m.id");
		} else {
			query = getSession()
					.createQuery(
							"select m from Menu m left join fetch m.menuFather left join m.permissions p left join p.rolPermissions rp where m.action=null or rp.rol.id =:id  order by m.id");
			query.setString("id", user.getRol().getId().toString());
		}

		return query.list();

	}
}