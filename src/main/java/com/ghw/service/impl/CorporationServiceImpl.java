package com.ghw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.dao.AddressDAO;
import com.ghw.dao.BankInformationDAO;
import com.ghw.dao.CorporationDAO;
import com.ghw.model.Address;
import com.ghw.model.Corporation;
import com.ghw.service.CorporationService;

@Service("corporationService")
@Transactional(readOnly = false)
public class CorporationServiceImpl extends
		ServiceImpl<Corporation, String, CorporationDAO> implements
		CorporationService {
	private CorporationDAO dao;

	@Autowired
	private BankInformationDAO bankInformationDAO;

	@Autowired
	private AddressDAO addressDAO;

	@Autowired
	public void setDAO(CorporationDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	public Corporation getDataByName(String name) {

		return dao.getDataByName(name);
	}

	@Override
	public Corporation getDataByEin(String ein) {
		return dao.getDataByEin(ein);
	}

	@Override
	public List<Corporation> loadAll() {
		List<Corporation> corp = dao.findAll();
		for (Corporation corporation : corp) {
			corporation.setBank(bankInformationDAO
					.getDataByCorporation(corporation));
		}

		return corp;
	}

	@Override
	public List<Corporation> getDataWithAddress(String region) {
		List<Corporation> corp = dao.getDataWithPrincipalInformation(region);

//		List<Address> addresses = addressDAO.getDataByCorporation(corp);
//		for (Corporation corporation : corp) {
//			for (Address address : addresses) {
//				if (corporation.getId()
//						.equals(address.getCorporation().getId())) {
//					corporation.setAddressCorporation(address);
//					break;
//				}
//			}
//		}

		return corp;
	}

}