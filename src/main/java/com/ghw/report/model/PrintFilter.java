package com.ghw.report.model;

import java.io.Serializable;

import com.ghw.filter.FilterBase;
import com.ghw.util.Context;

public class PrintFilter extends FilterBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private String idGroup;

	private String name;
	private String clazz;
	private String filterBean;

	/**
	 * 1 : Default list 2 : Default detail
	 */
	private Integer typeDefault;

	private Integer currentPage;

	public String getIdGroup() {
		return idGroup;
	}

	public void setIdGroup(String idGroup) {
		this.idGroup = idGroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getFilterBean() {
		return filterBean;
	}

	public void setFilterBean(String filterBean) {
		this.filterBean = filterBean;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getTypeDefault() {
		return typeDefault;
	}

	public void setTypeDefault(Integer typeDefault) {
		this.typeDefault = typeDefault;
	}

	public FilterBase getFiltro() {
		return (FilterBase) Context.getBean(getFilterBean());
	}

}
