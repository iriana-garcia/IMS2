package com.ghw.dao;

import java.util.List;

import com.ghw.model.RolPermission;

public interface RolPermissionDAO extends GenericDAO<RolPermission, String> {

	public List<RolPermission> getListByRol(String rolId);

	public void deleteByListId(List<String> ids);

	public List<String> getPermissionSpringByUser(String idUser);

}