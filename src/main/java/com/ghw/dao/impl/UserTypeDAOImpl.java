package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.UserTypeDAO;
import com.ghw.model.UserType;

@Repository("userTypeDAO")
public class UserTypeDAOImpl extends GenericHibernateDAO<UserType, String>
		implements UserTypeDAO {

}