package com.ghw.filter;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

/**
 * Commom Filter
 * 
 * @author ifernandez
 * 
 */
@ManagedBean
@ViewScoped
public class FilterBase implements Serializable {

	private int firstRow = 0;
	private String sortField;
	private SortOrder sortOrder;
	private Map<String, Object> filters;
	private int numberOfRows;

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Map<String, Object> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	
	@Override
	public String toString() {
		String filter = " ";

		if (filters != null)
			for (Iterator<String> it = getFilters().keySet().iterator(); it
					.hasNext();) {
				String filterProperty = it.next();
				Object filterValue = getFilters().get(filterProperty);
				filter += filterProperty + ": " + filterValue + " ";
			}

		return filter;
	}

}
