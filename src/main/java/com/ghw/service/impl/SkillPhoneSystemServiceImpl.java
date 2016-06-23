package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.SkillPhoneSystemDAO;
import com.ghw.model.LogSystem;
import com.ghw.model.Skill;
import com.ghw.model.SkillPhoneSystem;
import com.ghw.service.SkillPhoneSystemService;
import com.ghw.util.IpServer;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;

@Service("skillPhoneSystemService")
@Transactional(readOnly = false)
public class SkillPhoneSystemServiceImpl extends
		ServiceImpl<SkillPhoneSystem, String, SkillPhoneSystemDAO> implements
		SkillPhoneSystemService {

	private SkillPhoneSystemDAO dao;

	@Autowired
	private LogSystemDAO logSystemDAO;

	@Autowired
	public void setDAO(SkillPhoneSystemDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	@TriggersRemove(cacheName = "skillPhoneCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public void saverOrUpdate(List<SkillPhoneSystem> skills) {

		LogSystem log = new LogSystem();
		log.setMethod("saverOrUpdate");
		log.setClassName("SkillPhoneSystem");

		boolean error = false;
		String detail = "";
		try {

			if (skills != null && skills.size() > 0) {

				// get all the Skill to make the comparision
				List<SkillPhoneSystem> listSkillSystem = dao.findAll();

				// inserted if not exists
				// I need to compare with the id in pipkins
				List<SkillPhoneSystem> newList = new ArrayList<SkillPhoneSystem>();
				newList.addAll(skills);
				newList.removeAll(listSkillSystem);

				for (SkillPhoneSystem skill : newList) {
					dao.makePersistent(skill);
				}

				int totalUpdated = 0;
				// update the event if exists in system
				for (SkillPhoneSystem system : listSkillSystem) {
					for (SkillPhoneSystem skill : skills) {
						if (skill.equals(system)) {
							system.setName(skill.getName());
							system.setState(skill.isState());
							dao.merge(system);
							break;
						}
					}
				}

				detail = "Total inserted: " + newList.size()
						+ " Total updated: " + totalUpdated;

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			error = true;
			detail += e.getMessage();
			throw e;
		} finally {

			log.setError(error);
			log.setDetail(detail);
			log.setIp(IpServer.ipServer());
			logSystemDAO.makePersistent(log);

		}

	}

	@Override
	public List<SkillPhoneSystem> getDataWithoutSkills() {
		return dao.getDataWithoutSkills();
	}

	@Override
	public List<SkillPhoneSystem> getDataWithSkillId() {
		return dao.getDataWithSkillId();
	}

	@Override
	public List<SkillPhoneSystem> getDataWithoutSkills(Skill skill) {
		return dao.getDataWithoutSkills(skill);
	}

}