package com.ghw.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.dao.GenericDAO;
import com.ghw.filter.FilterBase;
import com.ghw.model.IEntity;
import com.ghw.model.IEntityEditable;

/**
 * Common class for all Service implementation
 * 
 * @author ifernandez
 * 
 * @param <T>
 *            class
 * @param <ID>
 *            id class
 * @param <D>
 *            DAO class
 */
@Transactional(readOnly = true)
public class ServiceImpl<T, ID extends Serializable, D extends GenericDAO<T, ID>>
		implements Serializable {

	private D dao;

	public void setDao(D dao) {
		this.dao = dao;
	}

	/**
	 * Validates data before inserting or modify
	 * 
	 * @param entity
	 * @return
	 */

	public void isValidate(T entity) throws Exception {

	}

	/**
	 * Valida los datos antes de eliminar
	 * 
	 * @param entity
	 * 
	 * @return
	 */
	public void validateDelete(T entity) throws Exception {

	}

	/**
	 * 
	 * Gets an entity
	 * 
	 * @param id
	 *            Entity ID
	 * @return Return an instance of the entity
	 * @throws Exception
	 * 
	 */
	public T loadById(ID id) {
		return dao.loadById(id);
	}

	/**
	 * Gets an entity
	 * 
	 * @param id
	 *            Entity ID
	 * @return Return an instance of the entity
	 * @throws Exception
	 */
	public T getById(ID id) throws Exception {
		return dao.getById(id);
	}

	/**
	 * Insert an entity
	 * 
	 * @param entity
	 *            Entity data
	 * @return inserted entity
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Loggable
	public T makePersistent(T entity) throws Exception {

		isValidate(entity);

		return dao.makePersistent(entity);
	}

	/**
	 * Updates the entity
	 * 
	 * @param entity
	 *            Entity data
	 * @return entity updated
	 * @throws Exception
	 */
	@Loggable
	@Transactional(readOnly = false)
	public T update(T entity) throws Exception {
		isValidate(entity);
		return dao.update(entity);
	}

	/**
	 * Updates the entity
	 * 
	 * @param entity
	 *            Entity data
	 * @return entity updated
	 * @throws Exception
	 */
	@Loggable
	@Transactional(readOnly = false)
	public T merge(T entity) throws Exception {
		isValidate(entity);
		// compare the modification
		if (entity instanceof IEntity) {

			IEntityEditable ientity = (IEntityEditable) entity;
			ID id = (ID) ientity.getIdentity();

			ientity.compare(dao.getById(id));
			return dao.merge(entity);
		} else
			return null;
	}

	/**
	 * Removes an entity
	 * 
	 * @param entity
	 *            Entity data
	 * @throws Exception
	 */
	@Loggable
	@Transactional(readOnly = false)
	public void makeTransient(T entity) throws Exception {

		dao.makeTransient(entity);
	}

	public List<T> getData() {
		return dao.findAll();
	}

	/**
	 * Gets a list of entities applying a filter
	 * 
	 * @param filter
	 *            Filter data
	 * @return List of entities
	 */
	public List<T> getData(FilterBase filter) throws Exception {

		return dao.getData(filter);
	}

	public List<T> getDataOrder() {

		return dao.getDataOrder();
	}

	public List<T> getDataActive() {

		return dao.getDataActive();
	}

	/**
	 * Gets the number of entities that meet a filter
	 * 
	 * @param filter
	 *            Filter data
	 * @return Number of entities
	 */
	public Long count(FilterBase filter) throws Exception {
		return dao.count(filter);
	}
}
