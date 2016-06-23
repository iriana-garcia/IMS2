package com.ghw.dao;

import java.util.List;

import com.ghw.model.BankInformation;
import com.ghw.model.Corporation;
import com.ghw.model.Profile;

public interface BankInformationDAO extends GenericDAO<BankInformation, String> {

	public boolean validateIfExists(BankInformation entity);


	public String getIdByCorporation(String corporationId);

	public List<BankInformation> getDataActiveOracle();

	public Long getNextValue(String secuence);

	public void updateNeedUpdatedFalse(List<BankInformation> banks);

	public Long getCountByIbo(Profile profile);

	public boolean getExistByCorporation(Corporation corporation);

	public Long getCountByCorporation(Corporation corporation);

	public boolean getExistByIbo(Profile profile);

	public BankInformation getDataByCorporation(Corporation corporation);
}