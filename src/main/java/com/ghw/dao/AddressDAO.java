package com.ghw.dao;

import java.util.List;

import com.ghw.model.Address;
import com.ghw.model.Corporation;

public interface AddressDAO extends GenericDAO<Address, String> {

	public List<Address> getDataByCorporation(Corporation corporation);

	public void updateNeedUpdatedFalse(List<Address> addresses);

	public List<Address> getDataActiveOracle();
	
	public List<Address> getDataByCorporation(List<Corporation> corporations);
}