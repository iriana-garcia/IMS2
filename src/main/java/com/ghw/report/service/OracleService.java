package com.ghw.report.service;

import com.ghw.model.User;

public interface OracleService {

	public String createSupplierFiles(User user);

	public String createPayables(User user);
}
