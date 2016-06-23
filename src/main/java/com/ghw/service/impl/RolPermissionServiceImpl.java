package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.RolPermissionDAO;
import com.ghw.model.RolPermission;
import com.ghw.service.RolPermissionService;

@Service("rolPermissionService")
@Transactional(readOnly = false)
public class RolPermissionServiceImpl extends
		ServiceImpl<RolPermission, String, RolPermissionDAO> implements
		RolPermissionService {

	private RolPermissionDAO dao;

	@Autowired
	public void setDao(RolPermissionDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	public List<RolPermission> getListByRol(String rolId) {
		return dao.getListByRol(rolId);
	}

}