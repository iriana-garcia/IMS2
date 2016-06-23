package com.ghw.dao;

import java.util.List;

import com.ghw.model.Groups;
import com.ghw.model.User;

public interface GroupsDAO extends GenericDAO<Groups, String> {

	public boolean validateIfExistsName(String name, String id);

	public void changeState(Groups entity, User user);

	public Groups loadAllById(Groups entity) throws Exception;

	public List<Groups> getDataWithoutUser(User user);
	
	public List<Groups> getDataWithUser(User user);

	public void updateUser(Groups group, User user);
	
	public Groups getDataByUser(User user);
	
	public void clearUser(User user);
}