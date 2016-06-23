package com.ghw.service;

import java.io.Serializable;
import java.util.List;

import com.ghw.filter.FilterBase;

/**
 * Common interface for all Service
 * 
 * @author ifernandez
 * 
 * @param <T>
 *            class
 * @param <ID>
 *            class id
 */
public interface Service<T, ID extends Serializable> {

	public T getById(ID id) throws Exception;

	public T loadById(ID id);

	public List<T> getDataOrder();

	public List<T> getDataActive();

	public T makePersistent(T entity) throws Exception;

	public T update(T entity) throws Exception;

	public void makeTransient(T entity) throws Exception;

	public List<T> getData();

	public List<T> getData(FilterBase filter) throws Exception;

	public Long count(FilterBase filter) throws Exception;

	public T merge(T entity) throws Exception;
}
