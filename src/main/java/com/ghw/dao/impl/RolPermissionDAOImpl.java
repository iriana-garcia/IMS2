package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import com.ghw.dao.RolPermissionDAO;
import com.ghw.model.Permission;
import com.ghw.model.RolPermission;

@Repository("rolPermissionDAO")
public class RolPermissionDAOImpl extends
		GenericHibernateDAO<RolPermission, String> implements RolPermissionDAO {

	/**
	 * Get the permission associate to a rol
	 */
	@Override
	public List<RolPermission> getListByRol(String rolId) {
		Criteria criteria = getSession().createCriteria(Permission.class)
				.createAlias("rolPermissions", "rp", JoinType.LEFT_OUTER_JOIN,
						Restrictions.eq("rp.rol.id", rolId));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("name"));
		projection.add(Property.forName("descripcion"));
		projection.add(Property.forName("rp.id"));
		projection.add(Property.forName("rp.access"));

		criteria.setProjection(projection);

		List<Object[]> list = criteria.list();
		List<RolPermission> rpList = new ArrayList<RolPermission>();

		for (Object[] o : list) {
			RolPermission rp = new RolPermission();
			rp.setPermission(new Permission((Integer) o[0], (String) o[1],
					(String) o[2]));
			rp.setId((String) o[3]);
			rp.setAccess((String) o[4]);

			rpList.add(rp);

		}

		return rpList;
	}

	/**
	 * delete all the rol-permissions included in the list of id
	 */
	@Override
	public void deleteByListId(List<String> ids) {
		if (ids != null && ids.size() > 0) {
			Query query = getSession().createQuery(
					"delete from RolPermission where id IN(:ids)");
			query.setParameterList("ids", ids);

			query.executeUpdate();
		}
	}

	/**
	 * Get all the permission by user (used to spring security)
	 */
	@Override
	public List<String> getPermissionSpringByUser(String idUser) {
		Query query = getSession()
				.createQuery(
						"select p.constSpring, rp.access from RolPermission rp join rp.permission p join rp.rol r join r.users u where u.id =:id");
		query.setString("id", idUser);

		List<String> permissions = new ArrayList<String>();
		List<Object[]> list = query.list();
		for (Object[] o : list) {
			String permSpring = (String) o[0];
			permissions.add(permSpring);
			String access = (String) o[1];
			if (StringUtils.isNotBlank(access)) {
				// process to other permission
				String[] a = access.split("");
				for (String string : a) {
					permissions.add(permSpring + "_" + string);
				}
			}
		}
		return permissions;
	}
}