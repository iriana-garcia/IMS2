package com.ghw.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ghw.model.Menu;
import com.ghw.model.User;

/**
 * Store the user and the menu in session
 * 
 * @author ifernandez
 * 
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionBean implements Serializable {

	private User user;

	private boolean logueado = false;

//	// save the menu
//	public List<Menu> menu;
//
//	public List<Menu> getMenu() {
//		return menu;
//	}
//
//	public void setMenu(List<Menu> menu) {
//		this.menu = menu;
//	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isLogueado() {
		return logueado;
	}

	public void setLogueado(boolean logueado) {
		this.logueado = logueado;
	}

	public void clear() {
		//menu = null;
		logueado = false;
		user = null;
	}

	public static boolean hasAnyRol(List<String> roles) {

		UserDetails loggedInUser = (UserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				loggedInUser.getAuthorities());

		for (GrantedAuthority grantedAuthority : authorities) {
			if (roles.contains(grantedAuthority.getAuthority()))
				return true;
		}

		return false;
	}

	public static boolean hasAnyRol(String... roles) {

		UserDetails loggedInUser = (UserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				loggedInUser.getAuthorities());

		List<String> rolList = Arrays.asList(roles);

		for (GrantedAuthority grantedAuthority : authorities) {
			if (rolList.contains(grantedAuthority.getAuthority()))
				return true;
		}

		return false;
	}

	public static boolean hasRol(String rol) {

		UserDetails loggedInUser = (UserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				loggedInUser.getAuthorities());

		for (GrantedAuthority grantedAuthority : authorities) {
			if (rol.equals(grantedAuthority.getAuthority()))
				return true;
		}

		return false;
	}

}
