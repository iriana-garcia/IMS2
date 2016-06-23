package com.ghw.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.ghw.dao.UpdatedDateDAO;
import com.ghw.model.UpdatedDate;

@Repository("updatedDateDAO")
public class UpdatedDateDAOImpl extends
		GenericHibernateDAO<UpdatedDate, Integer> implements UpdatedDateDAO {

	public int update(String field, Date value) {

		// // String sql =
		// // "update updated_date set updated_agent_cisco =:dateUpdate";
		// String sql = "update updated_date set updated_agent_cisco =NOW()";
		// Query query = getSession().createSQLQuery(sql);
		// // query.setTimestamp("dateUpdate", new Date());
		//
		// return query.executeUpdate();

		Query query = getSession().createQuery(
				"update UpdatedDate set updateCallDetailIncontact=:lastLogin");
		query.setTimestamp("lastLogin", new Date());

		query.executeUpdate();

		return 0;

	}

	public List<UpdatedDate> getDataPendingUpdate(Date date) {

		// is important order by id ASC because the schedule must to be inserted
		// before agent state detail, because after insert agent state detail
		// I process Invoice Work
		Query query = getSession()
				.createQuery(
						"select u from UpdatedDate u where cast(u.dateUpdated as date) <=:date and u.state = true order by id ASC");
		query.setDate("date", date);

		return query.list();
	}
}