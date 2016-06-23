package com.ghw.dao;

import java.io.Serializable;
import java.util.List;

import com.ghw.filter.FilterBase;

/**
 * Common class for all the DAO interfaz
 * 
 * @author ifernandez
 * 
 * @param <T>
 *            class
 * @param <ID>
 *            class id
 */
public interface GenericDAO<T, ID extends Serializable> {

	public T getById(ID id);

	public T loadById(ID id);

	public List<T> getDataOrder();
	
	public List<T> getDataActive();

	public T makePersistent(T entity);

	public T update(T entity);
	
	public T saveOrUpdate(T entity);

	public T merge(T entity);

	public void makeTransient(T entity);

	public List<T> findAll();

	public List<T> getData(FilterBase filter) throws Exception;

	public Long count(FilterBase filter) throws Exception;

	public void flush();
	
	public void clear();


}
