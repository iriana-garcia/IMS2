package com.ghw.service;

import java.util.List;

import com.ghw.model.Groups;
import com.ghw.model.Profile;
import com.ghw.model.User;

public interface GroupsService extends Service<Groups, String> {

	public Groups update(Groups entity, List<Profile> ibos, User user)
			throws Exception;

	public Groups makePersistent(Groups entity, List<Profile> ibos, User user)
			throws Exception;

	public Groups changeState(Groups entity) throws Exception;

	public boolean validateIfDeactivate(Groups entity);

	public Groups loadAllById(Groups ca) throws Exception;
	
	public List<Groups> getDataWithoutUser(User user);
	
	public List<Groups> getDataWithUser(User user);
}