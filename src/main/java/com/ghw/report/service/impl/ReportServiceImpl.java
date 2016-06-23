package com.ghw.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.report.dao.ReportDao;
import com.ghw.report.model.Report;
import com.ghw.report.service.ReportService;
import com.ghw.service.impl.ServiceImpl;

@Service("reportService")
@Transactional(readOnly = false)
public class ReportServiceImpl extends ServiceImpl<Report, String, ReportDao>
		implements ReportService {
	private ReportDao dao;

	@Autowired
	public void setDao(ReportDao dao) {
		this.dao = dao;
		super.setDao(dao);
	}
}
