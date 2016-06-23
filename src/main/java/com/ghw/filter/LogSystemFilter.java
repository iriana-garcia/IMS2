package com.ghw.filter;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class LogSystemFilter extends FilterBase {

	private Date startDate;

	private Date endDate;

	private int typeReport = 1;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getTypeReport() {

		Object type = Context.getSessionAttribute("idReportLogSystem");
		if (type != null) {
			typeReport = new Integer((String) type);
			Context.removeSessionAttribute("idReportLogSystem");
		}
		return typeReport;
	}

	public void setTypeReport(int typeReport) {
		this.typeReport = typeReport;
	}

}
