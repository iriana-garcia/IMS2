package com.ghw.dao;

import java.util.List;

import com.ghw.model.Corporation;

public interface CorporationDAO extends GenericDAO<Corporation, String> {

	public boolean validateIfExistsEin(String ein, String id);

	public boolean validateIfExistsName(String name, String id);

	public Corporation getDataByName(String name);

	public Corporation getDataByEin(String ein);

	public List<Corporation> getDataWithPrincipalInformation(String region);

	public String getIdPrincipalOwnerCorporation(Corporation corporation);

	public List<Corporation> getDataActiveOracle();

	public String updateSupplierNumber(Corporation corporation);

	public void updateNeedUpdatedFalse(List<Corporation> corporations);
}