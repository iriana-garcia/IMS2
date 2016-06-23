package com.ghw.dao;

import java.util.List;

import com.ghw.model.ClientApplication;
import com.ghw.model.Event;
import com.ghw.model.Skill;

public interface EventDAO extends GenericDAO<Event, String> {

	public void update(List<Event> events, ClientApplication ca);

	public void clearClientApplication(List<Event> events);

	public void clearClientApplication(ClientApplication ca);

	public List<Event> getDataWithoutCA();

	public List<Event> getDataWithoutCA(ClientApplication ca);

	public List<Event> getDataByCA(ClientApplication ca);

}