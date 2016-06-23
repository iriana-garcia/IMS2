package com.ghw.report.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.impl.GenericHibernateDAO;
import com.ghw.filter.FilterBase;
import com.ghw.report.dao.ReportDao;
import com.ghw.report.model.PrintFilter;
import com.ghw.report.model.Report;

@Repository("reportDao")
public class ReportDaoImpl extends GenericHibernateDAO<Report, String>
		implements ReportDao {

	@Override
	public List<Report> getData(FilterBase filter) {

		PrintFilter pFilter = (PrintFilter) filter;

		Criteria criteria = getSession().createCriteria(Report.class);

		if (StringUtils.isNotBlank(pFilter.getIdGroup()))
			criteria.add(Restrictions.eq("group.id", pFilter.getIdGroup()));

		return criteria.list();

	}

}
