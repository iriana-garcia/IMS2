package com.ghw.dao;

import java.util.List;

import com.ghw.model.ClientApplication;
import com.ghw.model.Skill;
import com.ghw.model.User;

public interface SkillDAO extends GenericDAO<Skill, String> {

	public boolean validateIfExistsName(String name, String id);

	public void update(List<Skill> skills, ClientApplication ca);

	public void clearClientApplication(List<Skill> skills);

	public void clearClientApplication(ClientApplication ca);

	public List<Skill> getDataWithoutCA();

	public List<Skill> getDataWithoutCA(ClientApplication ca);

	public List<Skill> getDataByCA(ClientApplication ca);

	public void changeState(Skill entity, User user);

	public Skill loadAllById(Skill skill) throws Exception;

}