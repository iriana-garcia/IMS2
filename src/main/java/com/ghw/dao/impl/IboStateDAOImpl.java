package com.ghw.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.ghw.dao.IboStateDAO;
import com.ghw.model.IboState;

@Repository("iboStateDAO")
public class IboStateDAOImpl extends GenericHibernateDAO<IboState, String>
		implements IboStateDAO {

}