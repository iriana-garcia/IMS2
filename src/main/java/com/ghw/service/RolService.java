package com.ghw.service;

import java.util.List;

import com.ghw.model.Rol;
import com.ghw.model.RolPermission;

public interface RolService extends Service<Rol, String> {

	public Rol update(Rol entity, List<RolPermission> selectedPerm)
			throws Exception;

	public Rol makePersistent(Rol entity, List<RolPermission> selectedPerm)
			throws Exception;

	public Rol changeState(Rol entity) throws Exception;
	
	public boolean validateUserAssociate(Rol rol);
}