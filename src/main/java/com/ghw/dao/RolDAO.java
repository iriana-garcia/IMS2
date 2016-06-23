package com.ghw.dao;

import com.ghw.model.Rol;
import com.ghw.model.User;

public interface RolDAO extends GenericDAO<Rol, String> {

	public boolean validateIfExistsName(String name, String id);
	
	public boolean validateUserAssociate(Rol rol);
	
	public void changeState(Rol rol, User user);
}
