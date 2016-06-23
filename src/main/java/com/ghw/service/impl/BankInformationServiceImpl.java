package com.ghw.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.constant.Constant;
import com.ghw.controller.SessionBean;
import com.ghw.dao.BankInformationDAO;
import com.ghw.dao.ClientApplicationDAO;
import com.ghw.dao.ProfileDAO;
import com.ghw.dao.RoutingNumbersDAO;
import com.ghw.model.Bank;
import com.ghw.model.BankInformation;
import com.ghw.model.Corporation;
import com.ghw.model.Profile;
import com.ghw.model.RoutingNumbers;
import com.ghw.model.User;
import com.ghw.service.BankInformationService;
import com.ghw.service.InvoiceService;
import com.ghw.util.SystemException;

@Service("bankInformationService")
@Transactional(readOnly = false)
public class BankInformationServiceImpl extends
		ServiceImpl<BankInformation, String, BankInformationDAO> implements
		BankInformationService {

	private BankInformationDAO dao;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	private ProfileDAO profileDAO;

	@Autowired
	private ClientApplicationDAO clientApplicationDAO;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private RoutingNumbersDAO routingNumbersDAO;

	@Autowired
	public void setDAO(BankInformationDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	public void isValidate(BankInformation entity) throws Exception {
		// validate don't exists that bank information in system for other IBO,
		// is necessary for oracle
		if (dao.validateIfExists(entity)) {
			throw new SystemException("bank_exists");
		}
	}

	@Override
	@Loggable
	public BankInformation saveOrUpdate(BankInformation entity, User user)
			throws Exception {

		Corporation corporation = new Corporation(
				profileDAO.getCorporationIdByUserId(user.getId()));

		entity.setCorporation(corporation);

		entity.setId(dao.getIdByCorporation(corporation.getId()));

		// isValidate(entity);

		// save in this register need to be update in oracle
		entity.setNeedUpdate(true);

		// save in log system if modify any field
		if (StringUtils.isBlank(entity.getId())) {
			entity.compare(null);
			entity.setCreatedDate(new Date());
			entity.setUserCreated(user);
			dao.makePersistent(entity);
		} else {
			BankInformation b = dao.getById(entity.getId());
			entity.compare(b);
			entity.setUpdateDate(new Date());
			entity.setUserUpdated(user);
			dao.merge(entity);
		}

		// get all the IBO associate to the Corporation and the state Active and
		// status different OK
		List<Profile> profiles = profileDAO
				.getDataByCorporationActiveNoOk(corporation.getId());
		for (Profile profile : profiles) {
			// if the IBO has also Program associate change the state to OK
			Long totalProgram = clientApplicationDAO.getCountByIbo(profile);
			if (totalProgram != null && totalProgram > 0 && user.isState()) {
				profileDAO.update(profile, "4");
				// create invoice for this week
				invoiceService.createInvoiceIbo(profile, sessionBean.getUser());
			}

		}

		// if not exist the routing number inserted with need updated = true
		if (!routingNumbersDAO
				.validateIfExistRoutingNumber(entity.getRouting())) {
			RoutingNumbers rn = new RoutingNumbers();
			rn.setNeedUpdate(true);
			rn.setBank(new Bank(Constant.genericBankId));
			rn.setNumber(entity.getRouting());

			routingNumbersDAO.makePersistent(rn);
		}

		return entity;
	}
}