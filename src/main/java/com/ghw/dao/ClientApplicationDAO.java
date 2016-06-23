package com.ghw.dao;

import java.util.List;

import com.ghw.filter.FilterBase;
import com.ghw.model.ClientApplication;
import com.ghw.model.Profile;
import com.ghw.model.User;

public interface ClientApplicationDAO extends
		GenericDAO<ClientApplication, String> {

	public boolean validateIfExistsName(String name, String id);

	public void changeState(ClientApplication entity, User user);

	public List<ClientApplication> loadAllById(FilterBase filter)
			throws Exception;

	public ClientApplication loadAllById(ClientApplication ca) throws Exception;

	public List<ClientApplication> getDataByIbo(Profile profile);

	public List<ClientApplication> getDataByNotIbo(Profile profile);

	public Long getCountByIbo(Profile profile);

	public List<ClientApplication> getDataByEditIbo(Profile profile);
}