package com.ghw.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.ghw.dao.CorporationDAO;
import com.ghw.filter.FilterBase;
import com.ghw.model.Corporation;
import com.ghw.model.CorporationType;
import com.ghw.model.OwnerType;
import com.ghw.model.PayMethod;
import com.ghw.model.Region;

@Repository("corporationDAO")
public class CorporationDAOImpl extends
		GenericHibernateDAO<Corporation, String> implements CorporationDAO {

	@Override
	public List<Corporation> getData(FilterBase filter) throws Exception {
		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");
		projection.add(Property.forName("ein"), "ein");
		projection.add(Property.forName("supplierNumber"), "supplierNumber");
		projection
				.add(Projections.alias(
						Projections
								.sqlProjection(
										"case when isnull(pro_id) and isnull(ban_id) then 'AB' when isnull(pro_id)  then 'B'  else 'A' end as problemId",
										new String[] { "problemId" },
										new Type[] { new StringType() }),
						"problemId"), "problemId");

		criteria.setProjection(projection);

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				Corporation.class));

		// default filter
		if (StringUtils.isEmpty(filter.getSortField())) {
			criteria.addOrder(Order.desc("name"));
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
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		Criteria criteria = getSession().createCriteria(Corporation.class);
		criteria.createAlias("bankInformation", "bi", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("profiles", "pro", JoinType.LEFT_OUTER_JOIN,
				Restrictions.eq("pro.ownerType", OwnerType.PRINCIPAL));

		// DetachedCriteria ids = DetachedCriteria.forClass(Profile.class);
		// ids.add(Restrictions.eq("ownerType", OwnerType.PRINCIPAL));
		//
		// ProjectionList p = Projections.projectionList();
		// p.add(Property.forName("corporation.id"));
		// ids.setProjection(p);

		criteria.add(Restrictions.eq("payMethod", PayMethod.DIRECT_DEPOSIT));

		boolean needFilter = true;
		if (filter.getFilters() != null) {
			for (Iterator<String> it = filter.getFilters().keySet().iterator(); it
					.hasNext();) {

				String filterProperty = it.next();
				Object filterValue = filter.getFilters().get(filterProperty);

				// always exclude the field that included another field
				if (filterProperty.equals("problemId")) {
					if (filterValue.equals("A")) {
						criteria.add(Restrictions.isNull("bi.id"));
					} else if (filterValue.equals("B")) {
						criteria.add(Restrictions.isNull("pro.id"));
					} else if (filterValue.equals("AB")) {
						criteria.add(Restrictions.isNull("pro.id")).add(
								Restrictions.isNull("bi.id"));
					}
					needFilter = false;

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

		if (needFilter) {
			criteria.add(Restrictions.or(Restrictions.isNull("bi.id"),
					Restrictions.isNull("pro.id")));
		}

		return criteria;
	}

	/**
	 * Validate if the EIN number exists in system
	 */
	@Override
	public boolean validateIfExistsEin(String ein, String id) {
		Criteria criteria = getSession().createCriteria(Corporation.class).add(
				Restrictions.ilike("ein", ein));
		if (StringUtils.isNotBlank(id)) {
			criteria.add(Restrictions.ne("id", id));
		}

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.rowCount());

		criteria.setProjection(projection);

		return (Long) criteria.list().get(0) > 0 ? true : false;

	}

	public Corporation getDataByName(String name) {
		Criteria criteria = getSession().createCriteria(Corporation.class);
		criteria.add(Restrictions.eq("name", name));

		return (Corporation) criteria.uniqueResult();
	}

	@Override
	public Corporation getDataByEin(String ein) {
		Criteria criteria = getSession().createCriteria(Corporation.class);
		criteria.add(Restrictions.eq("ein", ein));

		return (Corporation) criteria.uniqueResult();
	}

	public List<Corporation> getDataWithPrincipalInformation(String region) {

		// Criteria criteria = getSession().createCriteria(Corporation.class);
		// criteria.addOrder(Order.asc("name"));

		Query query = getSession().createQuery(
				"select c, (select max(p.id) from c.profiles p where p.ownerType = '"
						+ OwnerType.PRINCIPAL.name() + "') "
						+ "from Corporation c "
						+ "where c.type =:type order by c.name ASC");

		query.setString(
				"type",
				region.equals(Region.D.name()) ? CorporationType.DOMESTIC
						.name() : CorporationType.INTERNATIONAL.name());

		List<Object[]> listObject = query.list();

		List<Corporation> corporations = new ArrayList<Corporation>();
		for (Object[] o : listObject) {
			Corporation c = (Corporation) o[0];
			String idPrincipal = (String) o[1];
			c.setIdPrincipalOwner(idPrincipal);

			corporations.add(c);
		}

		return corporations;

	}

	public String getIdPrincipalOwnerCorporation(Corporation corporation) {

		Query query = getSession()
				.createQuery(
						"select max(p.id) from Profile p where p.ownerType = 'PRINCIPAL' and p.corporation.id = :id ");
		query.setString("id", corporation.getId());

		return (String) query.uniqueResult();
	}

	/**
	 * Get all the corporation to export to Oracle, all need to updated and the
	 * profile does not have number
	 * 
	 * @return
	 */
	@Override
	public List<Corporation> getDataActiveOracle() {

		// I need only the adddress form the United states
		Criteria criteria = getSession().createCriteria(Corporation.class);

		// with the supplier number is null (need to be inserted in oracle), or
		// need to be updated
		criteria.add(Restrictions.or(Restrictions.isNull("supplierNumber"),
				Restrictions.eq("needUpdate", true)));

		return criteria.list();

	}

	/**
	 * Assigned a supplier number to an Corporation
	 */
	@Override
	public String updateSupplierNumber(Corporation corporation) {
		// Get the current number
		Query query = getSession().createSQLQuery(
				"select nextval('sq_supplier_number')");
		Long numberActual = ((BigInteger) query.uniqueResult()).longValue();

		String supplierNumber = "IBO" + numberActual;

		// update the IBO with the supplier number
		query = getSession().createQuery(
				"update Corporation set supplierNumber =:sn where id=:id");
		query.setString("sn", supplierNumber);
		query.setString("id", corporation.getId());
		query.executeUpdate();

		return supplierNumber;
	}

	/**
	 * update the field need updated to false when is exported
	 */
	@Override
	public void updateNeedUpdatedFalse(List<Corporation> corporations) {
		if (corporations != null && corporations.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Corporation s : corporations)
				ids.add(s.getId());

			Query query = getSession()
					.createQuery(
							"update Corporation p set p.needUpdate=false where p.id IN(:ids)");

			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

}