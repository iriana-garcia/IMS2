package com.ghw.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.ghw.dao.IbosClientApplicationsDAO;
import com.ghw.model.IbosClientApplications;
import com.ghw.model.Profile;

@Repository("ibosClientApplicationsDAO")
public class IbosClientApplicationsDAOImpl extends
		GenericHibernateDAO<IbosClientApplications, String> implements
		IbosClientApplicationsDAO {

	@Override
	public void deleteByCA(String idCa) {

		Query query = getSession()
				.createQuery(
						"delete from IbosClientApplications where clientApplication.id =:idCa");
		query.setString("idCa", idCa);

		query.executeUpdate();
	}

	/**
	 * remove all the client application associate to a IBO
	 */
	@Override
	public void deleteByIbo(Profile profile) {

		Query query = getSession().createQuery(
				"delete IbosClientApplications p where p.user.id=:id ");
		query.setString("id", profile.getId());

		query.executeUpdate();

	}

	/**
	 * remove all the client application associate to a USER
	 */
	@Override
	public void deleteByIbo(String idUser) {

		Query query = getSession()
				.createQuery(
						"delete IbosClientApplications p where p.user.id IN (select p.id from Profile p where p.user.id =:id) ");
		query.setString("id", idUser);

		query.executeUpdate();

	}

}