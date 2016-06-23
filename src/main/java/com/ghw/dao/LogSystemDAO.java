package com.ghw.dao;

import com.ghw.model.LogSystem;
import com.ghw.model.User;

public interface LogSystemDAO extends GenericDAO<LogSystem, String> {

	public void insertLogout(User user);

	public void insertError(User user, String method, String detail,
			String className);
}
