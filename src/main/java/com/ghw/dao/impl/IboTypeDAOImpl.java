package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.IboTypeDAO;
import com.ghw.model.IboType;

@Repository("iboTypeDAO")
public class IboTypeDAOImpl extends GenericHibernateDAO<IboType, String>
		implements IboTypeDAO {
}