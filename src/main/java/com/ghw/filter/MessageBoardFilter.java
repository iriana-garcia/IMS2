package com.ghw.filter;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class MessageBoardFilter extends FilterBase {

	private Date startDate;

	private Date endDate;

	private Date startDateSubmitted;

	private Date endDateSubmitted;

	private int typeReport = 1;

	private Integer note;

	private Integer question;

	private String corporationName;	

	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

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
		return typeReport;
	}

	public void setTypeReport(int typeReport) {
		this.typeReport = typeReport;
	}

	public Date getStartDateSubmitted() {
		return startDateSubmitted;
	}

	public void setStartDateSubmitted(Date startDateSubmitted) {
		this.startDateSubmitted = startDateSubmitted;
	}

	public Date getEndDateSubmitted() {
		return endDateSubmitted;
	}

	public void setEndDateSubmitted(Date endDateSubmitted) {
		this.endDateSubmitted = endDateSubmitted;
	}

	public Integer getNote() {
		return note;
	}

	public void setNote(Integer note) {
		this.note = note;
	}

	public Integer getQuestion() {
		return question;
	}

	public void setQuestion(Integer question) {
		this.question = question;
	}

	public String getCorporationName() {
		return corporationName;
	}

	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}


}
