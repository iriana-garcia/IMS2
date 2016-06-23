package com.ghw.service;

import com.ghw.model.Address;
import com.ghw.model.Corporation;

public interface AddressService extends Service<Address, String> {

	public Address getDataByCorporation(Corporation corporation);
}