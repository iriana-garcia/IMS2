package com.ghw.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.controller.SessionBean;
import com.ghw.dao.CategoryDAO;
import com.ghw.model.Category;
import com.ghw.service.CategoryService;
import com.ghw.util.SystemException;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;

@Service("categoryService")
@Transactional(readOnly = false)
public class CategoryServiceImpl extends
		ServiceImpl<Category, String, CategoryDAO> implements CategoryService {

	private CategoryDAO dao;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	public void setDAO(CategoryDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	public void isValidate(Category entity) throws Exception {

		if (dao.validateIfExistsName(entity.getName(), entity.getId()))
			throw new SystemException("name_exists");

		super.isValidate(entity);
	}

	@Override
	@Transactional(readOnly = false)
	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','CATEGORY_A')")
	@TriggersRemove(cacheName = "categoryCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Category makePersistent(Category entity) throws Exception {
		entity.setUserCreated(sessionBean.getUser());
		entity.setCreatedDate(new Date());
		return super.makePersistent(entity);
	}

	/**
	 * Change the CA state
	 */
	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CATEGORY_C')")
	@TriggersRemove(cacheName = "categoryCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Category changeState(Category entity) throws Exception {

		dao.changeState(entity, sessionBean.getUser());

		entity.setState(!entity.isState());
		entity.setFieldAdicional(" Old state: "
				+ (entity.isState() ? "Inactive" : "Active"));

		return entity;
	}

	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasAnyRole('SUPER','CATEGORY_M')")
	@TriggersRemove(cacheName = "categoryCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public Category merge(Category entity) throws Exception {
		return super.merge(entity);
	}

	@Override
	@Cacheable(cacheName = "categoryCache")
	public List<Category> getDataActive() {
		return super.getDataActive();
	}

}