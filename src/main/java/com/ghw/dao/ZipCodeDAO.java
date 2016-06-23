package com.ghw.dao;

import com.ghw.model.ZipCode;

public interface ZipCodeDAO extends GenericDAO<ZipCode, String> {

	public ZipCode getDataByCode(String code);
}