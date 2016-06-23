package com.ghw.model;

public enum EmailEnum {
	W("Welcome email"), I("Invoice email"), F("Finance email");

	private String statusCode;

	private EmailEnum(String s) {
		statusCode = s;
	}

	public String getStatusCode() {
		return statusCode;
	}
}
