package com.ghw.service;

import com.ghw.model.ZipCode;

public interface ZipCodeService extends Service<ZipCode, String> {

	public ZipCode getDataByCode(String code);
}