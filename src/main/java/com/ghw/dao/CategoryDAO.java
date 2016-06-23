package com.ghw.dao;

import com.ghw.model.Category;
import com.ghw.model.User;

public interface CategoryDAO extends GenericDAO<Category, String> {

	public boolean validateIfExistsName(String name, String id);

	public void changeState(Category entity, User user);
}