package com.ghw.service;

import java.util.Date;
import java.util.List;

import com.ghw.filter.FilterBase;
import com.ghw.model.ClientApplication;
import com.ghw.model.Event;
import com.ghw.model.Profile;
import com.ghw.model.Skill;

public interface ClientApplicationService extends
		Service<ClientApplication, String> {

	public ClientApplication update(ClientApplication entity,
			List<Skill> skills, List<Event> events, List<Profile> ibos,
			Date modification) throws Exception;

	public ClientApplication makePersistent(ClientApplication entity,
			List<Skill> skills, List<Event> events, List<Profile> ibos)
			throws Exception;

	public ClientApplication changeState(ClientApplication entity)
			throws Exception;

	public boolean validateIfDeactivate(ClientApplication entity);

	public List<ClientApplication> loadAllById(FilterBase filter)
			throws Exception;

	public ClientApplication loadAllById(ClientApplication ca) throws Exception;

	public List<ClientApplication> getDataByIbo(Profile profile);

	public List<ClientApplication> getDataByNotIbo(Profile profile);

	public List<ClientApplication> getDataByEditIbo(Profile profile);
}