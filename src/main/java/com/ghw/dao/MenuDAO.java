package com.ghw.dao;

import java.util.List;

import com.ghw.model.Menu;
import com.ghw.model.User;

public interface MenuDAO extends GenericDAO<Menu, Integer> {

	public List<Menu> getDataByUser(User user);
}
