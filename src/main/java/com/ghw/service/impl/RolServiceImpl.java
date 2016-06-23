package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.controller.SessionBean;
import com.ghw.dao.PermissionDAO;
import com.ghw.dao.RolDAO;
import com.ghw.dao.RolPermissionDAO;
import com.ghw.model.Rol;
import com.ghw.model.RolPermission;
import com.ghw.service.RolService;
import com.ghw.util.SystemException;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;

@Service("rolService")
@Transactional(readOnly = false)
public class RolServiceImpl extends ServiceImpl<Rol, String, RolDAO> implements
		RolService {

	private RolDAO dao;

	@Autowired
	private PermissionDAO permissionDao;

	@Autowired
	private RolPermissionDAO rolPermissionDao;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	public void setDao(RolDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	/**
	 * Validate the data before save or update
	 */
	@Override
	public void isValidate(Rol entity) throws Exception {
		if (dao.validateIfExistsName(entity.getName(), entity.getId()))
			throw new SystemException("name_exists", "form:txtName");
	}

	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','ROL_A')")
	@TriggersRemove(cacheName = "rolCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Rol makePersistent(Rol rol, List<RolPermission> selectedPerm)
			throws Exception {

		isValidate(rol);

		rol.setUserCreated(sessionBean.getUser());

		rol.getPermissions().clear();
		String fieldAdicional = " Permissions: [";
		for (RolPermission p : selectedPerm) {
			String access = "R" + (p.isAdd() ? "A" : "")
					+ (p.isModify() ? "M" : "")
					+ (p.isChangeState() ? "C" : "")
					+ (p.isDelete() ? "D" : "");
			fieldAdicional += " Permission: " + p.getPermission().getName()
					+ " Access: " + access;
			p.setPermission(permissionDao.loadById(p.getPermission().getId()));
			p.setAccess(access);
			p.setRol(rol);
			rol.getPermissions().add(p);

		}

		rol.setFieldAdicional(fieldAdicional + "]");

		dao.makePersistent(rol);

		return rol;
	}

	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','ROL_M')")
	@TriggersRemove(cacheName = "rolCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Rol update(Rol rol, List<RolPermission> selectedPerm)
			throws Exception {

		isValidate(rol);

		// if desactivate and have user error message
		if (!rol.isState() && dao.validateUserAssociate(rol))
			throw new SystemException("user_associate");

		List<String> idEliminate = new ArrayList<String>();
		for (RolPermission oldList : rol.getPermissions()) {
			if (oldList.getId() != null && !selectedPerm.contains(oldList))
				idEliminate.add(oldList.getId());
		}

		// delete permission
		rolPermissionDao.deleteByListId(idEliminate);

		// compare the modification
		rol.compare(dao.getById(rol.getId()));

		rol.getPermissions().clear();
		String fieldAdicional = " Permissions: [";
		for (RolPermission p : selectedPerm) {
			String access = "R" + (p.isAdd() ? "A" : "")
					+ (p.isModify() ? "M" : "")
					+ (p.isChangeState() ? "C" : "")
					+ (p.isDelete() ? "D" : "");
			fieldAdicional += " Permission: " + p.getPermission().getName()
					+ " Access: " + access;
			p.setPermission(permissionDao.loadById(p.getPermission().getId()));
			p.setAccess(access);
			p.setRol(rol);
			rol.getPermissions().add(p);

		}

		// Add fiel aditional to insert in Log System
		rol.setFieldAdicional(fieldAdicional + "] " + rol.getFieldAdicional());

		dao.merge(rol);

		return rol;
	}

	public void setPermissionDao(PermissionDAO permissionDao) {
		this.permissionDao = permissionDao;
	}

	public RolPermissionDAO getRolPermissionDao() {
		return rolPermissionDao;
	}

	public void setRolPermissionDao(RolPermissionDAO rolPermissionDao) {
		this.rolPermissionDao = rolPermissionDao;
	}

	/**
	 * Change the state and save the date and the user that make the action
	 */

	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','ROL_C')")
	@TriggersRemove(cacheName = "rolCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Rol changeState(Rol rol) throws Exception {

		// if desactivate and have user error message
		if (rol.isState() && dao.validateUserAssociate(rol))
			throw new SystemException("user_associate");

		dao.changeState(rol, sessionBean.getUser());

		rol.setState(!rol.isState());
		rol.setFieldAdicional(" Old state: "
				+ (rol.isState() ? "Inactive" : "Active"));

		return rol;
	}

	/**
	 * get if has user associate
	 */
	@Override
	public boolean validateUserAssociate(Rol rol) {
		return dao.validateUserAssociate(rol);
	}

	@Override
	@Cacheable(cacheName = "rolCache")
	public List<Rol> getDataActive() {
		return super.getDataActive();
	}

}
