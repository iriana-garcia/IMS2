package com.ghw.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;

import com.ghw.dao.BankInformationDAO;
import com.ghw.model.BankInformation;
import com.ghw.model.Corporation;
import com.ghw.model.Profile;

@Repository("bankInformationDAO")
public class BankInformationDAOImpl extends
		GenericHibernateDAO<BankInformation, String> implements
		BankInformationDAO {

	@Override
	public String getIdByCorporation(String corporationId) {

		Criteria criteria = getSession().createCriteria(BankInformation.class);
		criteria.createAlias("corporation", "c");
		// criteria.createAlias("c.profiles", "user");
		// criteria.createAlias("user", "user");
		criteria.add(Restrictions.eq("c.id", corporationId));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");

		criteria.setProjection(projection);

		return (String) criteria.uniqueResult();
	}

	@Override
	public boolean validateIfExists(BankInformation entity) {

		Query query = getSession()
				.createQuery(
						"select b.number, b.routing, b.corporation.id from BankInformation b ");

		List<Object[]> list = query.list();
		for (Object[] o : list) {
			String number = (String) o[0];
			String routing = (String) o[1];
			String corporationId = (String) o[2];
			if (!corporationId.equals(entity.getCorporation().getId())
					&& number.equals(entity.getNumber())
					&& routing.equalsIgnoreCase(entity.getRouting()))
				return true;
		}

		return false;
	}

	/**
	 * Get all the Bank account to export to Oracle, all need to updated
	 * 
	 * @return
	 */
	@Override
	public List<BankInformation> getDataActiveOracle() {

		Criteria criteria = getSession().createCriteria(BankInformation.class)
				.createAlias("corporation", "c");

		// need to be updated
		criteria.add(Restrictions.eq("needUpdate", true));

		criteria.add(Restrictions.isNotNull("c.supplierNumber"));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("number"), "number");
		projection.add(Property.forName("routing"), "routing");
		projection.add(Property.forName("c.supplierNumber"), "supplierNumber");

		criteria.setProjection(projection);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				BankInformation.class));

		return criteria.list();

	}

	@Override
	public Long getNextValue(String secuence) {
		// Get the current number
		Query query = getSession().createSQLQuery(
				"select nextval('" + secuence + "')");

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	/**
	 * update the field need updated to false when is exported
	 */
	@Override
	public void updateNeedUpdatedFalse(List<BankInformation> banks) {
		if (banks != null && banks.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (BankInformation s : banks)
				ids.add(s.getId());

			Query query = getSession()
					.createQuery(
							"update BankInformation p set p.needUpdate=false where p.id IN(:ids)");

			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	/**
	 * Get count Bank by IBO
	 */
	@Override
	public Long getCountByIbo(Profile profile) {

		Criteria criteria = getSession().createCriteria(BankInformation.class);
		criteria.createAlias("corporation", "c");
		criteria.createAlias("c.profiles", "p");
		criteria.add(Restrictions.eq("p.id", profile.getId()));

		criteria.setProjection(Projections.rowCount());

		return (Long) criteria.list().get(0);
	}

	@Override
	public Long getCountByCorporation(Corporation corporation) {

		Criteria criteria = getSession().createCriteria(BankInformation.class);
		criteria.add(Restrictions.eq("corporation.id", corporation.getId()));

		criteria.setProjection(Projections.rowCount());

		return (Long) criteria.list().get(0);
	}

	@Override
	public boolean getExistByCorporation(Corporation corporation) {
		Long total = corporation != null
				&& StringUtils.isNotBlank(corporation.getId()) ? getCountByCorporation(corporation)
				: null;
		return total != null && total > 0;
	}

	@Override
	public boolean getExistByIbo(Profile profile) {
		Long total = profile != null && StringUtils.isNotBlank(profile.getId()) ? getCountByIbo(profile)
				: null;
		return total != null && total > 0;
	}

	@Override
	public BankInformation getDataByCorporation(Corporation corporation) {
		Criteria criteria = getSession().createCriteria(BankInformation.class);
		criteria.add(Restrictions.eq("corporation.id", corporation.getId()));

		return (BankInformation) criteria.uniqueResult();
	}

}