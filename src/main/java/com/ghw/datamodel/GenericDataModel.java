package com.ghw.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ghw.filter.FilterBase;
import com.ghw.model.IEntity;
import com.ghw.service.Service;
import com.ghw.util.Context;

/**
 * Common class for pull the data for dataTable lazy
 * 
 * @author ifernandez
 * 
 * @param <ID>
 * @param <T>
 * @param <S>
 */
public abstract class GenericDataModel<ID extends Serializable, T extends IEntity, S extends Service<T, ID>>
		extends LazyDataModel<T> {

	// save the list get in method load
	private List<T> datasource;

	// class service necessary for get data and count
	private S service;

	// filter class, is pass the method getData and count in service class
	private FilterBase filterBase;

	public GenericDataModel() {
	}

	public GenericDataModel(List<T> datasource) {
		this.datasource = datasource;
	}

	/**
	 * Get value from the list
	 */
	@Override
	public T getRowData(String rowKey) {
		try {

			if (datasource != null) {
				for (T t : datasource) {
					if (t.getIdentity().equals(rowKey))
						return t;
				}
			}
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
		return null;
	}

	/**
	 * Get class id
	 */
	@Override
	public Object getRowKey(T t) {
		return t.getIdentity();
	}

	/**
	 * Used to fill the dataTable in UI, call the method getData and count in
	 * class service
	 */
	@Override
	public List<T> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {

		List<T> data = new ArrayList<T>();

		try {

			// Fill the class Filter
			FilterBase filter = getFilterBase();
			filter.setFirstRow(first);
			filter.setNumberOfRows(pageSize);
			filter.setSortField(sortField);
			filter.setSortOrder(sortOrder);
			filter.setFilters(filters);

			// get the data, depends of filter class
			data = service.getData(filter);

			datasource = data;

			// get the count
			this.setRowCount(service.count(filter).intValue());

			calculateTotals();

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

		return data;

	}

	public List<T> getDatasource() {
		return datasource;
	}

	public void setDatasource(List<T> datasource) {
		this.datasource = datasource;
	}

	public S getService() {
		return service;
	}

	public void setService(S service) {
		this.service = service;
	}

	public FilterBase getFilterBase() {
		return filterBase;
	}

	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = filterBase;
	}

	/**
	 * calculate the total to show in datatable
	 */
	public void calculateTotals() {
	}

}
