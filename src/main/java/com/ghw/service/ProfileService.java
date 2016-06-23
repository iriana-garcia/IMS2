package com.ghw.service;

import java.util.List;

import com.ghw.model.ClientApplication;
import com.ghw.model.Groups;
import com.ghw.model.Profile;

public interface ProfileService extends Service<Profile, String> {

	public List<Profile> getDataWithoutGroup(Groups group);

	public boolean sendWelcomeEmail(String idUser) throws Exception;

	public Profile loadAllById(String idUser) throws Exception;

	public List<Profile> getDataActiveIbo();

	public List<Profile> getDataAllIboByCA(ClientApplication ca);

	public String[] getEmailIboAndGroup(String iboId);

	public void resetTotalSumbit();

	public Profile loadAllByIdToEdit(String idUser) throws Exception;

	public List<Profile> getDataByCorporationActive(String corporationId);

	public Profile updateIBOToPrincipal(Profile profile);
	
	public String getProfileNumberByUser(String userID) ;
}