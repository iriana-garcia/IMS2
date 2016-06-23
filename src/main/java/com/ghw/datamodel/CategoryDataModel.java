package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.model.Category;
import com.ghw.service.CategoryService;

@ManagedBean
@ViewScoped
public class CategoryDataModel extends
		GenericDataModel<String, Category, CategoryService> {

	@ManagedProperty(value = "#{categoryService}")
	private CategoryService service;

	@ManagedProperty(value = "#{filterBase}")
	private FilterBase filterBase;

	public CategoryService getService() {
		return service;
	}

	public void setService(CategoryService service) {
		this.service = service;
		super.setService(service);
	}

	@Override
	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = filterBase;
		super.setFilterBase(filterBase);

	}

	@Override
	public FilterBase getFilterBase() {
		return filterBase;
	}

	
}
