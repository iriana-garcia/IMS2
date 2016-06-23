package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.UserTypeDAO;
import com.ghw.model.UserType;
import com.ghw.service.UserTypeService;
import com.googlecode.ehcache.annotations.Cacheable;

@Service("userTypeService")
@Transactional(readOnly = false)
public class UserTypeServiceImpl extends
		ServiceImpl<UserType, String, UserTypeDAO> implements UserTypeService {
	private UserTypeDAO dao;

	@Autowired
	public void setDAO(UserTypeDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	@Cacheable(cacheName = "userTypeCache")
	public List<UserType> getDataOrder() {
		return super.getDataOrder();
	}
}