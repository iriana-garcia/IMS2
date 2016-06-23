package com.ghw.dao;

import java.util.List;

import com.ghw.model.Skill;
import com.ghw.model.SkillPhoneSystem;

public interface SkillPhoneSystemDAO extends
		GenericDAO<SkillPhoneSystem, String> {

	public void update(List<SkillPhoneSystem> skills, Skill s);

	public List<SkillPhoneSystem> getDataWithoutSkills();

	public List<SkillPhoneSystem> getDataWithSkillId();

	public List<SkillPhoneSystem> getDataBySkill(Skill skill);

	public void clearSkills(List<SkillPhoneSystem> skills);

	public List<SkillPhoneSystem> getDataWithoutSkills(Skill skill);

	public void clearSkills(Skill skill);
}