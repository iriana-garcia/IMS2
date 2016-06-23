package com.ghw.service;

import java.util.List;

import com.ghw.model.Skill;
import com.ghw.model.SkillPhoneSystem;

public interface SkillPhoneSystemService extends
		Service<SkillPhoneSystem, String> {

	public void saverOrUpdate(List<SkillPhoneSystem> skills);

	public List<SkillPhoneSystem> getDataWithoutSkills();

	public List<SkillPhoneSystem> getDataWithSkillId();

	public List<SkillPhoneSystem> getDataWithoutSkills(Skill skill);

}