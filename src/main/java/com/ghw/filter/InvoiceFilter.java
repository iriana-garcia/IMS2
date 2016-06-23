package com.ghw.filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.controller.SessionBean;
import com.ghw.model.User;
import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class InvoiceFilter extends FilterBase {

	private Date startDate, startDatePay, endDatePay;

	private Date endDate;

	private Date startDateSubmitted;

	private Date endDateSubmitted;

	private int typeReport = 1;

	private Integer note;

	private Integer question;

	private String corporationName;

	private String statusId;

	private String iboId;

	private Integer typeImport;

	private Integer sentFinance;

	private Date startDateFinance, endDateFinance;

	private String userFullName;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	public User getUser() {
		return sessionBean.getUser();
	}

	public Date getStartDate() {
		// if the filter type is 1 define a date by default
		if (typeReport == 1 && startDate == null) {

			Calendar actualDate = new GregorianCalendar();

			Calendar actualSunday = Calendar.getInstance();

			// from Sunday to Tusday is week before by default
			if (actualDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
					|| actualDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
					|| actualDate.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {

				actualSunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				actualSunday.add(Calendar.DATE, -7);

			} else {
				// from Wednesday to Saturday is actual week
				actualSunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			}
			startDate = actualSunday.getTime();
		}
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

		Object type = Context.getSessionAttribute("idReportTypeInvoice");
		if (type != null) {
			typeReport = new Integer((String) type);
			Context.removeSessionAttribute("idReportTypeInvoice");
		}
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

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getIboId() {
		return iboId;
	}

	public void setIboId(String iboId) {
		this.iboId = iboId;
	}

	public Date getStartDatePay() {
		return startDatePay;
	}

	public void setStartDatePay(Date startDatePay) {
		this.startDatePay = startDatePay;
	}

	public Date getEndDatePay() {
		return endDatePay;
	}

	public void setEndDatePay(Date endDatePay) {
		this.endDatePay = endDatePay;
	}

	public Integer getTypeImport() {
		return typeImport;
	}

	public void setTypeImport(Integer typeImport) {
		this.typeImport = typeImport;
	}

	public void clear() {

		setEndDate(null);
		setStartDate(null);
		setStartDateSubmitted(null);
		setEndDateSubmitted(null);
		setNote(null);
		setQuestion(null);
		startDatePay = null;
		endDatePay = null;
		typeImport = -1;
		sentFinance = null;
		startDateFinance = null;
		endDateFinance = null;
	}

	public Integer getSentFinance() {
		return sentFinance;
	}

	public void setSentFinance(Integer sentFinance) {
		this.sentFinance = sentFinance;
	}

	public Date getStartDateFinance() {
		return startDateFinance;
	}

	public void setStartDateFinance(Date startDateFinance) {
		this.startDateFinance = startDateFinance;
	}

	public Date getEndDateFinance() {
		return endDateFinance;
	}

	public void setEndDateFinance(Date endDateFinance) {
		this.endDateFinance = endDateFinance;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	/**
	 * Get the actual week for the filter
	 * 
	 * @return
	 */
	public String getDateRange() {

		if (getStartDate() != null) {
			String rangeDate = "Date from: "
					+ (new SimpleDateFormat("E, M-d-yy")).format(startDate)
					+ " 12:00 AM to ";

			if (endDate == null) {

				Calendar nextSaturday = new GregorianCalendar();
				nextSaturday.setTime(startDate);
				nextSaturday.add(Calendar.DATE, 6);

				rangeDate += (new SimpleDateFormat("E, M-d-yy"))
						.format(nextSaturday.getTime()) + "  11:59 PM";

			} else
				rangeDate += (new SimpleDateFormat("E, M-d-yy"))
						.format(endDate) + "  11:59 PM";

			return rangeDate;

		}

		return "";
	}

}
