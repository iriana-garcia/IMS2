package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.controller.SessionBean;
import com.ghw.dao.GroupsDAO;
import com.ghw.dao.ProfileDAO;
import com.ghw.dao.UserDAO;
import com.ghw.model.Groups;
import com.ghw.model.Profile;
import com.ghw.model.User;
import com.ghw.service.GroupsService;
import com.ghw.util.SystemException;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;

@Service("groupsService")
@Transactional(readOnly = false)
public class GroupsServiceImpl extends ServiceImpl<Groups, String, GroupsDAO>
		implements GroupsService {

	private GroupsDAO dao;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	private ProfileDAO profileDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	public void setDAO(GroupsDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	/**
	 * Validate if exists the Groups name in system
	 */
	@Override
	public void isValidate(Groups entity) throws Exception {
		if (dao.validateIfExistsName(entity.getName(), entity.getId()))
			throw new SystemException("name_exists", "form:txtName");
	}

	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','GROUP_M')")
	@TriggersRemove(cacheName = "groupCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Groups update(Groups entity, List<Profile> ibos, User user)
			throws Exception {
		// validate if exist the name in system
		isValidate(entity);

		// get the old object before update
		Groups old = dao.getById(entity.getId());
		User userOld = old.getUser() != null ? userDAO.getById(old.getUser()
				.getId()) : null;
		List<Profile> profilesOld = profileDAO.getDataByGroup(entity);

		String fieldAdicional = "";
		// if desactivate and clear the IBO associate
		if (!entity.isState()) {
			entity.setProfile(new ArrayList<Profile>());
			fieldAdicional = " IBOs: Undefined";
		} else {

			// insert in field adicional to insert in logg system
			fieldAdicional = " IBOs: "
					+ (ibos == null || ibos.size() == 0 ? "Undefined" : ibos
							.toString());
			entity.setProfile(ibos);

		}
		entity.setFieldAdicional(fieldAdicional);

		old.setUser(userOld);
		old.setProfile(profilesOld);
		entity.setUser(user);

		entity.compare(old);

		// updated IBO associate
		profileDAO.clearGroup(entity);

		// associate group to IBOs
		if (entity.isState())
			profileDAO.update(ibos, entity);

		entity.setProfile(null);
		old.setProfile(null);

		dao.merge(entity);
		dao.flush();

		return entity;
	}

	/**
	 * Insert a client application with it skills
	 */
	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','GROUP_A')")
	@TriggersRemove(cacheName = "groupCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Groups makePersistent(Groups entity, List<Profile> ibos, User user)
			throws Exception {
		// validate if exist the name in system
		isValidate(entity);

		entity.setUserCreated(sessionBean.getUser());

		entity.setUser(user);

		// insert in field adicional to insert in logg system
		String fieldAdicional = user != null ? user.toString() : "";

		dao.makePersistent(entity);
		dao.flush();

		// associate group to IBOs
		profileDAO.update(ibos, entity);

		// insert in field adicional to insert in logg system
		fieldAdicional = " IBOs: "
				+ (ibos == null || ibos.size() == 0 ? "Undefined" : ibos
						.toString());

		entity.setFieldAdicional(entity.getFieldAdicional() + fieldAdicional);

		return entity;
	}

	/**
	 * Change the CA state
	 */
	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','GROUP_C')")
	@TriggersRemove(cacheName = "groupCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Groups changeState(Groups entity) throws Exception {

		// if desactivate and clear the IBO associate
		if (entity.isState()) {
			profileDAO.clearGroup(entity);
		}

		dao.changeState(entity, sessionBean.getUser());

		entity.setState(!entity.isState());
		entity.setFieldAdicional(" Old state: "
				+ (entity.isState() ? "Inactive" : "Active"));

		return entity;
	}

	/**
	 * Validate if the Groups can be deactivate.
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean validateIfDeactivate(Groups entity) {
		return profileDAO.getCountByGroup(entity) > 0;
	}

	/**
	 * Load all the group data
	 */
	@Override
	@PreAuthorize("hasAnyRole('SUPER','GROUP')")
	@Transactional(readOnly = true)
	public Groups loadAllById(Groups entity) throws Exception {
		Groups cl = dao.loadAllById(entity);
		cl.setProfile(profileDAO.getDataByGroup(entity));

		return cl;
	}

	/**
	 * Get all the groups that does not have an user associate
	 */
	@Override
	public List<Groups> getDataWithoutUser(User user) {
		return dao.getDataWithoutUser(user);
	}

	/**
	 * Get all the groups that does have an user associate
	 */
	@Override
	public List<Groups> getDataWithUser(User user) {
		return dao.getDataWithUser(user);
	}

	@Override
	@Cacheable(cacheName = "groupCache")
	public List<Groups> getDataActive() {
		return super.getDataActive();
	}
}