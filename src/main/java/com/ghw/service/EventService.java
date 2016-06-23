package com.ghw.service;

import java.util.List;

import com.ghw.model.ClientApplication;
import com.ghw.model.Event;
import com.ghw.model.Skill;

public interface EventService extends Service<Event, String> {
	public List<Event> getDataWithoutCA();

	public List<Event> getDataWithoutCA(ClientApplication ca);
	
	public String saverOrUpdate(List<Event> events);
}