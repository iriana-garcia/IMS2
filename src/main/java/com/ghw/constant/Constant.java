package com.ghw.constant;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class Constant {

	public static final String usCountryId = "52f03556-162b-4963-8ea1-62fe2c373ede";

	public static final String genericBankId = "0008fc3e-896e-4ccc-9cab-78670892539c";

	private String rowsToShow = "10,15,20,25,30,40,50,100";

	public String getUsCountryId() {
		return usCountryId;
	}

	public String getRowsToShow() {
		return rowsToShow;
	}

}
