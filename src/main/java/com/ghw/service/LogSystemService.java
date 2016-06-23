package com.ghw.service;

import com.ghw.model.LogSystem;
import com.ghw.model.User;

public interface LogSystemService extends Service<LogSystem, String> {

	public void insertLogout(User user);
}