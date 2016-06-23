package com.ghw.service;

import java.util.List;

import com.ghw.model.ClientApplication;
import com.ghw.model.Skill;
import com.ghw.model.SkillPhoneSystem;

public interface SkillService extends Service<Skill, String> {

	public List<Skill> getDataWithoutCA();

	public List<Skill> getDataWithoutCA(ClientApplication ca);

	// public List<Skill> getDataByInvoice(Invoice invoice);

	public Skill makePersistent(Skill entity,
			List<SkillPhoneSystem> skillPhoneSystems, ClientApplication ca)
			throws Exception;

	public Skill update(Skill entity, List<SkillPhoneSystem> skillPhoneSystems,
			ClientApplication ca) throws Exception;

	public Skill changeState(Skill entity) throws Exception;

	public Skill loadAllById(Skill skill) throws Exception;

}