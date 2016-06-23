package com.ghw.security;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

public class SessionEndedListener implements
		ApplicationListener<ApplicationEvent> {

	@Autowired
	HttpSession httpSession;

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof HttpSessionCreatedEvent) { // If event
																	// is a
																	// session
																	// created
																	// event

		} else if (applicationEvent instanceof HttpSessionDestroyedEvent) { // If
																			// event
																			// is
																			// a
																			// session
																			// destroy
																			// event
			// handler.expireCart();

			System.out.println("" + (Long) httpSession.getAttribute("userId"));

			System.out.println(" Session is destroyed  :"); // log data

		} else if (applicationEvent instanceof AuthenticationSuccessEvent) { // If
																				// event
																				// is
																				// a
																				// session
																				// destroy
																				// event
			System.out.println("  authentication is success  :"); // log data
		} else {
			System.out.println(" unknown event occur  :  Source: "); // log data

		}
	}
}
// @Override
// public void sessionCreated(HttpSessionEvent arg0) {
//
// System.out.println("sessionCreated - add one session into counter");
//
// }
//
// @Override
// public void sessionDestroyed(HttpSessionEvent arg0) {
//
// System.out
// .println("sessionDestroyed - deduct one session from counter");
//
// try {
// FacesContext.getCurrentInstance().getExternalContext()
// .redirect("login.jsf");
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// }
// }