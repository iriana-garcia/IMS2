package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.MenuDAO;
import com.ghw.model.Menu;
import com.ghw.model.User;
import com.ghw.service.MenuService;

@Service("menuService")
@Transactional(readOnly = false)
public class MenuServiceImpl extends ServiceImpl<Menu, Integer, MenuDAO>
		implements MenuService {

	private MenuDAO dao;

	@Autowired
	public void setDao(MenuDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	public List<Menu> getDataByUser(User user) {
		return dao.getDataByUser(user);
	}

}
