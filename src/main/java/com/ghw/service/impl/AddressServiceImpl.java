package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.AddressDAO;
import com.ghw.model.Address;
import com.ghw.model.Corporation;
import com.ghw.service.AddressService;

@Service("addressService")
@Transactional(readOnly = false)
public class AddressServiceImpl extends
		ServiceImpl<Address, String, AddressDAO> implements AddressService {
	private AddressDAO dao;

	@Autowired
	public void setDAO(AddressDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	public Address getDataByCorporation(Corporation corporation) {
		List<Address> listAd = dao.getDataByCorporation(corporation);
		return (Address) (listAd != null && listAd.size() > 0 ? listAd.get(0)
				: new ArrayList<Address>());
	}

}