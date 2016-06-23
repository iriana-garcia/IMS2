package com.ghw.report.model;

import java.util.List;

public class ReportTable {

	private List list;

	public ReportTable(List list) {
		super();
		this.list = list;
	}

	public Integer getCount() {

		return list == null ? 0 : list.size();
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

}
