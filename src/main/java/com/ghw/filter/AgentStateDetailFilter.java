package com.ghw.filter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DualListModel;

import com.ghw.controller.SessionBean;
import com.ghw.model.User;

@ManagedBean
@ViewScoped
public class AgentStateDetailFilter extends FilterBase {

	private Date startDate;

	private Date endDate;

	private String userId;

	private String iboNumber;

	// private List<String> eventId = new ArrayList<String>();

	private DualListModel<Object[]> eventId = new DualListModel<Object[]>(
			new ArrayList<Object[]>(), new ArrayList<Object[]>());

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	public User getUser() {
		return sessionBean.getUser();
	}

	public AgentStateDetailFilter() {

		List<Object[]> listSource = new ArrayList<Object[]>();
		listSource.add(new Object[] { 1, "Log in" });
		listSource.add(new Object[] { 2, "Not ready" });
		listSource.add(new Object[] { 3, "Ready" });
		listSource.add(new Object[] { 4, "Reserved" });
		listSource.add(new Object[] { 5, "Talking" });
		listSource.add(new Object[] { 6, "Work" });
		listSource.add(new Object[] { 7, "Log Out" });

		eventId = new DualListModel<Object[]>(listSource,
				new ArrayList<Object[]>());
	}

	public Date getStartDate() {
		if (startDate == null) {
			// this sunday
			Calendar c = Calendar.getInstance();
			// c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			c.add(Calendar.DAY_OF_WEEK, -1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			startDate = c.getTime();
		}

		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		if (endDate == null) {
			// this saturday
			Calendar c = Calendar.getInstance();
			// c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			c.add(Calendar.DAY_OF_WEEK, -1);
			// c.add(Calendar.DATE, 6);
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);

			endDate = c.getTime();
		}

		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIboNumber() {
		return iboNumber;
	}

	public void setIboNumber(String iboNumber) {
		this.iboNumber = iboNumber;
	}

	public DualListModel<Object[]> getEventId() {
		return eventId;
	}

	public void setEventId(DualListModel<Object[]> eventId) {
		this.eventId = eventId;
	}

}
