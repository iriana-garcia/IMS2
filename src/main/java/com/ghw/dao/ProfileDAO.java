package com.ghw.dao;

import java.util.List;
import java.util.Map;

import com.ghw.model.ClientApplication;
import com.ghw.model.Corporation;
import com.ghw.model.Groups;
import com.ghw.model.OwnerType;
import com.ghw.model.Profile;

public interface ProfileDAO extends GenericDAO<Profile, String> {

	public List<Profile> getDataByCA(ClientApplication ca);

	public List<Profile> getDataWithoutGroup(Groups group);

	public List<Profile> getDataByGroup(Groups group);

	public Long getCountByGroup(Groups group);

	public void clearGroup(Groups group);

	public void clearGroup(Profile ibo);

	public void update(List<Profile> ibos, Groups group);

	public void clearGroup(List<Profile> ibos);

	public void clearGroup(String idUser);

	public void update(Profile profile, String idState);

	public Profile loadAllById(String idUser);

	public Profile getDataBySendEmail(String idUser);

	public List<Profile> getDataActiveIbo();

	public List<Profile> getDataAllIboByCA(ClientApplication ca);

	// public List<Profile> getDataActiveOracle();
	//
	// public String updateSupplierNumber(Profile profile);
	//
	// public void updateNeedUpdatedFalse(List<Profile> ibos);

	public String getProfileIdByUserId(String idUser);

	public String getCorporationIdByUserId(String idUser);

	public boolean validateIfGoToBank(String userId);

	public String[] getEmailIboAndGroup(String iboId);

	public List<Profile> getDataIboToInvoice();

	public Profile getEmailIbo(String iboId);

	public void updateTotalSumbit(String idProfile);

	public List<Profile> getDataActiveIboDomestic();

	public List<Profile> getDataAllActiveIbo();

	public void resetTotalSumbit();

	public List<Profile> getDataByCorporationActiveNoOk(String corporationId);

	public Profile loadAllByIdToEdit(String idUser);

	public List<Profile> getDataByCorporationActive(String corporationId);

	public void updateIBOToPrincipal(Profile profile);

	public void updateOwnerType(Profile profile, OwnerType ownerType);

	public void updateOwnerType(String userId, OwnerType ownerType);

	public Profile getDataForInactive(String userId);

	public Map<String, Double> getRate();

	public Profile getDataForUser(String userId);

	public boolean hasCorporationIboOtherRegion(Corporation corporation,
			Profile profile);
	
	public String getProfileNumberByUser(String userID);
}
