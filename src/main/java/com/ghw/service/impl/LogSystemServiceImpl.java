package com.ghw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.LogSystemDAO;
import com.ghw.model.LogSystem;
import com.ghw.model.User;
import com.ghw.service.LogSystemService;

@Service("logSystemService")
@Transactional(readOnly = false)
public class LogSystemServiceImpl extends
		ServiceImpl<LogSystem, String, LogSystemDAO> implements
		LogSystemService {

	private LogSystemDAO dao;

	@Autowired
	public void setDao(LogSystemDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	public void insertLogout(User user) {
		dao.insertLogout(user);

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public LogSystem makePersistent(LogSystem entity) throws Exception {
		return super.makePersistent(entity);
	}

}
