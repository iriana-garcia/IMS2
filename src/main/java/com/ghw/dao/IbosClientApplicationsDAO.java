package com.ghw.dao;

import com.ghw.model.IbosClientApplications;
import com.ghw.model.Profile;

public interface IbosClientApplicationsDAO extends
		GenericDAO<IbosClientApplications, String> {

	public void deleteByCA(String idCa);

	public void deleteByIbo(Profile profile);

	public void deleteByIbo(String idUser);
}