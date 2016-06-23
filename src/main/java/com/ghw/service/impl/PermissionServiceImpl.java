package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.PermissionDAO;
import com.ghw.model.Permission;
import com.ghw.service.PermissionService;
import com.googlecode.ehcache.annotations.Cacheable;

@Service("permissionService")
@Transactional(readOnly = false)
public class PermissionServiceImpl extends
		ServiceImpl<Permission, Integer, PermissionDAO> implements
		PermissionService {

	private PermissionDAO dao;

	@Autowired
	public void setDao(PermissionDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
	
	@Override
	@Cacheable(cacheName = "permissionCache")
	public List<Permission> getData() {
		return super.getData();
	}

}
