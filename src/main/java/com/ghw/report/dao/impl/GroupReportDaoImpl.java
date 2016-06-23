package com.ghw.report.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.ghw.dao.impl.GenericHibernateDAO;
import com.ghw.report.dao.GroupReportDao;
import com.ghw.report.model.GroupReport;

@Repository("groupReportDao")
public class GroupReportDaoImpl extends
		GenericHibernateDAO<GroupReport, String> implements GroupReportDao {

	public List<GroupReport> getList() {

		Query query = getSession().createQuery("from GrupoReporte");

		return query.list();
	}

}
