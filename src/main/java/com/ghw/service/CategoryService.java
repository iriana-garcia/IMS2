package com.ghw.service;

import com.ghw.model.Category;

public interface CategoryService extends Service<Category, String> {

	public Category changeState(Category entity) throws Exception;
}