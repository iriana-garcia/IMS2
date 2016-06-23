package com.ghw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.controller.SessionBean;
import com.ghw.dao.UserUtilDAO;
import com.ghw.model.UserUtil;
import com.ghw.service.IboTemporalService;
import com.ghw.service.UserService;
import com.ghw.service.UserUtilService;

@Service("userUtilService")
@Transactional(readOnly = false)
public class UserUtilServiceImpl extends
		ServiceImpl<UserUtil, String, UserUtilDAO> implements UserUtilService {
	private UserUtilDAO dao;

	@Autowired
	public void setDAO(UserUtilDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Autowired
	private UserService userService;

	@Autowired
	private IboTemporalService iboTemporalService;

	@Autowired
	private SessionBean sessionBean;

	/**
	 * Change the state and save the date and the user that make the action
	 */

	public void changeState(UserUtil user) throws Exception {

		if (user.isTemporal()) {
			iboTemporalService.changeState(iboTemporalService.getById(user
					.getId()));
		} else {

			userService.changeState(userService.getById(user.getId()),
					sessionBean.getUser());
		}

	}
}