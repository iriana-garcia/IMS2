package com.ghw.service;

import java.util.List;

import com.ghw.model.Menu;
import com.ghw.model.User;

public interface MenuService extends Service<Menu, Integer> {

	public List<Menu> getDataByUser(User user);
}