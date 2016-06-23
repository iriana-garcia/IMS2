package com.ghw.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.report.dao.GroupReportDao;
import com.ghw.report.model.GroupReport;
import com.ghw.service.impl.ServiceImpl;

@Service("groupReportService")
@Transactional(readOnly = false)
public class GroupReportServiceImpl extends
		ServiceImpl<GroupReport, String, GroupReportDao> {

	private GroupReportDao dao;

	@Autowired
	public void setDao(GroupReportDao dao) {
		this.dao = dao;
		super.setDao(dao);
	}

}
