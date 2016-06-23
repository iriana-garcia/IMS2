package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.UserUtilDAO;
import com.ghw.model.UserUtil;

@Repository("userUtilDAO")
public class UserUtilDAOImpl extends GenericHibernateDAO<UserUtil, String>
		implements UserUtilDAO {
}