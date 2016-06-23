package com.ghw.service;

import com.ghw.model.BankInformation;
import com.ghw.model.User;

public interface BankInformationService extends
		Service<BankInformation, String> {

	public BankInformation saveOrUpdate(BankInformation entity, User user)  throws Exception ;
}