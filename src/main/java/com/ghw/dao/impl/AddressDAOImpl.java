package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;

import com.ghw.dao.AddressDAO;
import com.ghw.model.Address;
import com.ghw.model.Corporation;

@Repository("addressDAO")
public class AddressDAOImpl extends GenericHibernateDAO<Address, String>
		implements AddressDAO {

	/**
	 * Get all the User's address
	 */
	@Override
	public List<Address> getDataByCorporation(Corporation corporation) {

		Criteria criteria = getSession().createCriteria(Address.class, "a")
				.createAlias("country", "cou")
				.createAlias("states", "st", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("a.corporation.id", corporation.getId()));

		return criteria.list();
	}

	/**
	 * Get all the profile to export to Oracle, all need to updated and the
	 * profile does not have number
	 * 
	 * @return
	 */
	@Override
	public List<Address> getDataActiveOracle() {

		Criteria criteria = getSession().createCriteria(Address.class)
				.createAlias("corporation", "c")
				.createAlias("states", "states");

		// with the supplier number is null (need to be inserted in oracle), or
		// need to be updated
		criteria.add(Restrictions.or(Restrictions.isNull("c.supplierNumber"),
				Restrictions.eq("needUpdate", true)));

		// I need only the adddress form the United states
		criteria.add(Restrictions.eq("country.id",
				"52f03556-162b-4963-8ea1-62fe2c373ede"));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("c.name"), "corporationName");
		projection.add(Property.forName("description"), "description");
		projection.add(Property.forName("zipCode"), "zipCode");
		projection.add(Property.forName("city"), "city");
		projection.add(Property.forName("states.name"), "state");
		projection.add(Property.forName("needUpdate"), "needUpdate");

		criteria.setProjection(projection);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				Address.class));

		return criteria.list();

	}

	/**
	 * update the field need updated to false when is exported
	 */
	@Override
	public void updateNeedUpdatedFalse(List<Address> addresses) {
		if (addresses != null && addresses.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Address s : addresses)
				ids.add(s.getId());

			Query query = getSession()
					.createQuery(
							"update Address p set p.needUpdate=false where p.id IN(:ids)");

			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	@Override
	public List<Address> getDataByCorporation(List<Corporation> corporations) {

		if (corporations != null && corporations.size() > 0) {
			Criteria criteria = getSession().createCriteria(Address.class, "a")
					.createAlias("country", "cou")
					.createAlias("states", "st", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.in("a.corporation", corporations));

			return criteria.list();
		}
		return new ArrayList<Address>();
	}
	// @Override
	// public List<Address> getDataByCorporation() {
	//
	// Criteria criteria = getSession().createCriteria(Address.class, "a")
	// .createAlias("country", "cou")
	// .createAlias("states", "st", JoinType.LEFT_OUTER_JOIN);
	//
	// return criteria.list();
	// }

}