package com.ghw.dao;

import java.util.Date;
import java.util.List;

import com.ghw.model.InvoiceWork;
import com.ghw.model.Schedule;

public interface ScheduleDAO extends GenericDAO<Schedule, String> {

	public List<Schedule> getDataByDate(Date dateStart, Date dateEnd);

	public List<Schedule> getDataByDate(Date date);

	public Long getCountByDate(Date dateStart, Date dateEnd);

	public List<Schedule> getDataByDatePending(Date date);

	public void update(Schedule schedule, InvoiceWork invoiceWork);
}