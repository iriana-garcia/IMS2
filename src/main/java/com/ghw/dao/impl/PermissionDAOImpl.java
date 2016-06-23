package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.PermissionDAO;
import com.ghw.model.Permission;

@Repository("permissionDAO")
public class PermissionDAOImpl extends GenericHibernateDAO<Permission, Integer>
		implements PermissionDAO {

}