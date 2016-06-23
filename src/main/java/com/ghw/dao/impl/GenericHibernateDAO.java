package com.ghw.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.filter.FilterBase;

/**
 * Common class for all DAO implementation
 * 
 * @author ifernandez
 * 
 * @param <T>
 * @param <ID>
 */
@Repository
@Transactional
public abstract class GenericHibernateDAO<T, ID extends Serializable>
		implements Serializable {

	@Autowired
	public HibernateTemplate hibernateTemplate;

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	private Class<T> persistentClass;

	@SuppressWarnings("unchecked")
	public GenericHibernateDAO() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];

	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	/**
	 * Get an entity by id
	 * 
	 * @param id
	 * @param lock
	 * @return
	 */
	public T loadById(ID id, boolean lock) {

		T entity;
		if (lock)
			entity = getHibernateTemplate().load(getPersistentClass(),
					(Serializable) id, LockMode.UPGRADE);
		else
			entity = (T) getHibernateTemplate().load(getPersistentClass(), id);

		return entity;
	}

	/**
	 * Get an entity by id
	 * 
	 * @param id
	 * @return
	 */
	public T loadById(ID id) {

		return loadById(id, false);
	}

	/**
	 * Get an entity by id
	 * 
	 * @param id
	 * @param lock
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public T getById(ID id, boolean lock) {

		T entity;
		if (lock)
			entity = (T) getHibernateTemplate().get(getPersistentClass(),
					(Serializable) id, LockMode.UPGRADE);
		else
			entity = (T) getHibernateTemplate().get(getPersistentClass(), id);

		return entity;
	}

	/**
	 * Get an entity by id
	 * 
	 * @param id
	 * @return
	 */
	public T getById(ID id) {

		return getById(id, false);
	}

	public List<T> findAll() {
		return findByCriteria();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByExample(T exampleInstance, String[] excludeProperty) {

		Criteria crit = getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createCriteria(getPersistentClass());
		Example example = Example.create(exampleInstance);
		for (String exclude : excludeProperty) {
			example.excludeProperty(exclude);
		}
		crit.add(example);

		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public T getObject(Criteria criteria) {
		return (T) criteria.uniqueResult();
	}

	public Object getValue(Query query) {
		return query.uniqueResult();
	}

	/**
	 * save an entity
	 * 
	 * @param entity
	 * @return
	 */
	public T makePersistent(T entity) {

		getHibernateTemplate().save(entity);

		return entity;
	}

	public T makePersistentStateless(T entity) {

		getStatelessSession().insert(entity);

		return entity;
	}

	/**
	 * update an entity
	 * 
	 * @param entity
	 * @return
	 */
	public T update(T entity) {

		getHibernateTemplate().update(entity);
		return entity;
	}

	/**
	 * merge an entity
	 * 
	 * @param entity
	 * @return
	 */
	public T merge(T entity) {

		getHibernateTemplate().merge(entity);
		return entity;
	}

	/**
	 * delete an entity
	 * 
	 * @param entity
	 */
	public void makeTransient(T entity) {

		getHibernateTemplate().delete(entity);

	}

	/**
	 * save or update entity
	 * 
	 * @param entity
	 * @return
	 */
	public T saveOrUpdate(T entity) {

		getHibernateTemplate().saveOrUpdate(entity);

		return entity;
	}

	public void flush() {
		getHibernateTemplate().flush();
	}

	public void clear() {
		getHibernateTemplate().clear();
	}

	/**
	 * Use this inside subclasses as a convenience method.
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(Criterion... criterion) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		for (Criterion c : criterion) {
			crit.add(c);
		}
		List<T> list = crit.list();

		return list;

	}

	@SuppressWarnings("unchecked")
	protected List<T> findByQuery(Query query) {

		List<T> list = query.list();

		return list;
	}

	public Session getSession() {

		return hibernateTemplate.getSessionFactory().getCurrentSession();
	}

	public StatelessSession getStatelessSession() {

		return hibernateTemplate.getSessionFactory().openStatelessSession();
	}

	/**
	 * get the data ordered by name ascending, only get the field id and name
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> getDataOrder() {

		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.addOrder(Order.asc("name"));
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");

		criteria.setProjection(projection);

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				getPersistentClass()));

		return criteria.list();

	}

	/**
	 * get the data ordered by name ascending in active state, only get the
	 * field id and name
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> getDataActive() {

		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.addOrder(Order.asc("name"))
				.add(Restrictions.eq("state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("name"), "name");

		criteria.setProjection(projection);

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				getPersistentClass()));

		return criteria.list();

	}

	/**
	 * Get the data for the table with filter
	 * 
	 * @param filter
	 * @return
	 */
	public List<T> getData(FilterBase filter) throws Exception {

		Criteria criteria = formCriteriaByListAndCount(filter);

		// default filter
		if (StringUtils.isEmpty(filter.getSortField())) {
			criteria.addOrder(Order.desc("id"));
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

	/**
	 * Get the number of record by filter to make pagination
	 * 
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public Long count(FilterBase filter) throws Exception {

		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.rowCount());

		criteria.setProjection(projection);

		return (Long) criteria.list().get(0);
	}

	/**
	 * common method used in getData(FilterBase filter) and count(FilterBase
	 * filter) to construct the criteria query
	 * 
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		Criteria criteria = getSession().createCriteria(persistentClass);

		if (filter.getFilters() != null) {
			for (Iterator<String> it = filter.getFilters().keySet().iterator(); it
					.hasNext();) {

				String filterProperty = it.next();
				Object filterValue = filter.getFilters().get(filterProperty);

				// always exclude the field that included another field
				if (filterProperty.equals("user.nameTable")) {
					criteria.add(Restrictions
							.disjunction()
							.add(Restrictions.ilike("u.firstName",
									(String) filterValue, MatchMode.ANYWHERE))
							.add(Restrictions.ilike("u.middleName",
									(String) filterValue, MatchMode.ANYWHERE))
							.add(Restrictions.ilike("u.lastName",
									(String) filterValue, MatchMode.ANYWHERE)));
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

		return criteria;

	}

	/**
	 * Validate if exists the name in BD before save or update
	 * 
	 * @param name
	 * @param id
	 * @return
	 */
	public boolean validateIfExistsName(String name, String id) {
		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.add(Restrictions.ilike("name", name));
		if (StringUtils.isNotBlank(id)) {
			criteria.add(Restrictions.ne("id", id));
		}

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.rowCount());

		criteria.setProjection(projection);

		return (Long) criteria.list().get(0) > 0 ? true : false;
	}

}
