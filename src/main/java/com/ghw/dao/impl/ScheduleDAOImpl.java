package com.ghw.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.ghw.dao.ScheduleDAO;
import com.ghw.model.InvoiceWork;
import com.ghw.model.Schedule;

@Repository("scheduleDAO")
public class ScheduleDAOImpl extends GenericHibernateDAO<Schedule, String>
		implements ScheduleDAO {

	public List<Schedule> getDataByDate(Date dateStart, Date dateEnd) {
		Criteria criteria = getSession().createCriteria(Schedule.class);
		criteria.add(Restrictions.between("dateStart", dateStart, dateEnd));

		return criteria.list();
	}

	public List<Schedule> getDataByDate(Date date) {
		Query query = getSession()
				.createQuery(
						"select s from Schedule s where cast(s.dateStart as date) = :date ");
		query.setDate("date", date);
		return query.list();
	}

	public List<Schedule> getDataByDatePending(Date date) {
		Query query = getSession()
				.createQuery(
						"select s from Schedule s join fetch s.event left join fetch s.clientApplication left join fetch s.user "
								+ "where cast(s.dateStart as date) = :date "
								+ "and s.invoiceWork is null "
								+ "and s.dateStart != s.dateEnd order by s.user.id ASC, s.dateStart ASC");
		query.setDate("date", date);
		return query.list();
	}

	@Override
	public Long getCountByDate(Date dateStart, Date dateEnd) {

		Criteria criteria = getSession().createCriteria(Schedule.class);
		criteria.add(Restrictions.between("dateStart", dateStart, dateEnd));

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.rowCount());

		criteria.setProjection(projection);

		return (Long) criteria.list().get(0);
	}

	public void update(Schedule schedule, InvoiceWork invoiceWork) {

		Query query = getSession().createQuery(
				"update Schedule set invoiceWork.id =:invWork where id =:id");
		query.setString("invWork", invoiceWork.getId());
		query.setString("id", schedule.getId());

		query.executeUpdate();
	}

}