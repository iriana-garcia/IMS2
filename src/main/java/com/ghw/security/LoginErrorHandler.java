package com.ghw.security;

import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

//@Component No used
public class LoginErrorHandler implements
		ApplicationListener<SessionDestroyedEvent> {

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
		UserDetails ud = null;
		for (SecurityContext securityContext : lstSecurityContext) {
			ud = (UserDetails) securityContext.getAuthentication()
					.getPrincipal();

		}
		try {
			if (ud != null) {
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect("aaaa.jsf");

				FacesContext.getCurrentInstance().responseComplete();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}