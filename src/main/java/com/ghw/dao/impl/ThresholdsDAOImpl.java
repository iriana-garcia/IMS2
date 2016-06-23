package com.ghw.dao.impl;

import org.springframework.stereotype.Repository;

import com.ghw.dao.ThresholdsDAO;
import com.ghw.model.Thresholds;

@Repository("thresholdsDAO")
public class ThresholdsDAOImpl extends GenericHibernateDAO<Thresholds, String>
		implements ThresholdsDAO {
}	