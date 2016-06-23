package com.ghw.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ghw.controller.ApplicationBean;
import com.ghw.controller.MenuController;
import com.ghw.controller.SessionBean;
import com.ghw.model.Menu;
import com.ghw.model.UserDetail;
import com.ghw.service.ConfigurationGeneralService;
import com.ghw.service.UserService;
import com.ghw.util.Context;

/**
 * Provider class for user authentication
 * 
 * @author ifernandez
 * 
 */
public class CustomAuthenticationProvider implements AuthenticationProvider,
		Serializable {

	@Autowired
	private UserService userService;

	// @Autowired
	// private MenuService menuService;

	@Autowired
	private ConfigurationGeneralService configurationService;

	@Autowired
	private ApplicationBean applicationBean;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	private MenuController menuController;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		String username = authentication.getName().trim();
		String password = (String) authentication.getCredentials();

		if (applicationBean.getConfiguration() == null) {
			applicationBean.setConfiguration(configurationService
					.getDataConfiguration());

		}

		com.ghw.model.User domainUser = null;
//		List<Menu> menus = null;
		try {

			menuController.setModel(null);

			domainUser = userService.getUser(username, password,
					applicationBean.getConfiguration());

			if (domainUser == null)
				throw new UsernameNotFoundException("bad_credential");

			if (domainUser.getRol() == null
					|| StringUtils.isBlank(domainUser.getRol().getId()))
				throw new UsernameNotFoundException("user_without_role");

			// // if all ok get menu
			// // if is an IBO inactive don't search anything
			// if (domainUser.isAnIbo() && !domainUser.isState())
			// menus = new ArrayList<Menu>();
			// else
			// menus = menuService.getDataByUser(domainUser);

			domainUser.setIp(Context.getIp());

			// update last login field
			userService.updateLastLogin(domainUser);

		} catch (UsernameNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage());
		} catch (Exception e) {
			throw new UsernameNotFoundException(e.getMessage());
		}

		sessionBean.setUser(domainUser);
		sessionBean.setLogueado(true);
		// sessionBean.setMenu(menus);

		menuController.getModel();

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		UserDetail user = new UserDetail(domainUser.getName(), "", enabled,
				accountNonExpired, credentialsNonExpired, accountNonLocked,
				getGrantedAuthorities(domainUser));

		return new UsernamePasswordAuthenticationToken(user, password,
				user.getAuthorities());
	}

	public static List<GrantedAuthority> getGrantedAuthorities(
			com.ghw.model.User user) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (String p : user.getPermissions()) {
			authorities.add(new SimpleGrantedAuthority(p));
		}
		return authorities;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}