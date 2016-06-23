package com.ghw.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class UserPassToken extends UsernamePasswordAuthenticationToken {

	public UserPassToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
