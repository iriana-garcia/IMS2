package com.ghw.service;

import java.util.List;

import com.ghw.model.RolPermission;

public interface RolPermissionService extends Service<RolPermission, String> {

	public List<RolPermission> getListByRol(String rolId);
}