package com.ghw.security;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ghw.util.Context;

public class LoginErrorPhaseListener implements PhaseListener {
	private static final long serialVersionUID = -1216620620302322995L;

	@Override
	public void beforePhase(final PhaseEvent arg0) {
		Exception e = (Exception) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap()
				.get("SPRING_SECURITY_LAST_EXCEPTION");

		if (e instanceof BadCredentialsException) {

			Context.addErrorMessageFromMSG(e.getMessage());

		} else if (e instanceof UsernameNotFoundException) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	@Override
	public void afterPhase(final PhaseEvent arg0) {
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}
