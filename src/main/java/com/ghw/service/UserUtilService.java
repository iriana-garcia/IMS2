package com.ghw.service;

import com.ghw.model.UserUtil;

public interface UserUtilService extends Service<UserUtil, String> {

	public void changeState(UserUtil user) throws Exception;
}