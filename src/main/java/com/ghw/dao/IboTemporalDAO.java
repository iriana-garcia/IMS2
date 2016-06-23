package com.ghw.dao;

import java.util.List;

import com.ghw.model.IboTemporal;
import com.ghw.model.User;

public interface IboTemporalDAO extends GenericDAO<IboTemporal, String> {

	public List<String> getDataName();

	public IboTemporal getIboByName(String name);

	public void delete(List<String> names);
	
	public void changeState(IboTemporal ibo, User userModify);
	
	public void delete(String name);
}