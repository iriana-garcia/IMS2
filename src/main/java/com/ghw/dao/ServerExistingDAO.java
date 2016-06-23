package com.ghw.dao;

import java.util.List;

import com.ghw.model.ServerExisting;

public interface ServerExistingDAO extends GenericDAO<ServerExisting, String> {

	public ServerExisting getDataByHost(String host);

	public List<ServerExisting> getPrincipalHost(Integer priority);
}