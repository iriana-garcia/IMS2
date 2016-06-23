package com.ghw.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.controller.SessionBean;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.SkillDAO;
import com.ghw.dao.SkillPhoneSystemDAO;
import com.ghw.model.ClientApplication;
import com.ghw.model.Skill;
import com.ghw.model.SkillPhoneSystem;
import com.ghw.service.SkillService;
import com.ghw.util.SystemException;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;

@Service("skillService")
@Transactional(readOnly = false)
public class SkillServiceImpl extends ServiceImpl<Skill, String, SkillDAO>
		implements SkillService {

	private SkillDAO dao;

	@Autowired
	private LogSystemDAO logSystemDAO;

	@Autowired
	private SkillPhoneSystemDAO skillPhoneSystemDAO;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	public void setDao(SkillDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	public List<Skill> getDataWithoutCA() {
		return dao.getDataWithoutCA();
	}

	@Override
	public List<Skill> getDataWithoutCA(ClientApplication ca) {
		return dao.getDataWithoutCA(ca);
	}

	@Override
	@Cacheable(cacheName = "skillCache")
	public List<Skill> getDataOrder() {
		return super.getDataOrder();
	}

	/**
	 * Validate if exists the Skill name in system
	 */
	@Override
	public void isValidate(Skill entity) throws Exception {
		if (dao.validateIfExistsName(entity.getName(), entity.getId()))
			throw new SystemException("name_exists", "formSkill:txtSkillName");
	}

	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA_A')")
	@TriggersRemove(cacheName = "skillCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Skill makePersistent(Skill entity,
			List<SkillPhoneSystem> skillPhoneSystems, ClientApplication ca)
			throws Exception {

		// validate if exist the name in system
		isValidate(entity);

		if (ca != null && StringUtils.isNotBlank(ca.getId()))
			entity.setClientApplication(ca);

		dao.makePersistent(entity);
		dao.flush();

		// associate the skills to CA
		skillPhoneSystemDAO.update(skillPhoneSystems, entity);

		// insert in field adicional to insert in logg system
		// entity.setFieldAdicional(" Phone System Skills: "
		// + skillPhoneSystems.toString());

		return entity;
	}

	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA_A')")
	@TriggersRemove(cacheName = "skillCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Skill update(Skill entity, List<SkillPhoneSystem> skillPhoneSystems,
			ClientApplication ca) throws Exception {
		// validate if exist the name in system
		isValidate(entity);

		entity.setClientApplication(ca);

		// assigned the lists to compare
		entity.setSkillPhoneSystems(skillPhoneSystems);

		// get the old object before update
		Skill old = dao.getById(entity.getId());

		// search the old list ok phone system skills to compare
		List<SkillPhoneSystem> listOld = skillPhoneSystemDAO
				.getDataBySkill(entity);
		old.setSkillPhoneSystems(listOld);

		// compare the data
		entity.compare(old);

		// clear the skills associate to CA
		skillPhoneSystemDAO.clearSkills(listOld);
		// associate the skills to CA
		skillPhoneSystemDAO.update(skillPhoneSystems, entity);

		dao.merge(entity);
		dao.flush();

		return entity;
	}

	/**
	 * Change the CA state
	 */
	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA_C')")
	@TriggersRemove(cacheName = "skillCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Skill changeState(Skill entity) throws Exception {

		// if the CA is going to deactivate clean the skill
		if (entity.isState()) {
			skillPhoneSystemDAO.clearSkills(entity);
		}

		dao.changeState(entity, sessionBean.getUser());

		entity.setState(!entity.isState());
		entity.setFieldAdicional(" Old state: "
				+ (entity.isState() ? "Inactive" : "Active"));

		return entity;
	}
	

	/**
	 * Load all the clients applications data
	 */
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA')")
	@Transactional(readOnly = true)
	public Skill loadAllById(Skill skill) throws Exception {
		Skill cl = dao.loadAllById(skill);
		cl.setSkillPhoneSystems(skillPhoneSystemDAO.getDataBySkill(skill));

		return cl;
	}


}