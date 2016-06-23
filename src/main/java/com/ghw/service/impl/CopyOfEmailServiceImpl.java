package com.ghw.service.impl;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.Invoice;
import com.ghw.model.Profile;
import com.ghw.service.EmailService;

/**
 * Util class to send email
 * 
 * @author ifernandez
 * 
 */
//@Service("emailService")
@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
public class CopyOfEmailServiceImpl implements EmailService, Serializable {

	@Override
	public void sendWelcomeEmail(Profile profile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendFinancialEmail(List<Path> files) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendQuestion(String from, String to, String bcc,
			String subject, String body) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendInvoiceEmail(Invoice invoice) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendInvoiceEmail(List<Invoice> listInvoices) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean confirmSMTP(ConfigurationGeneral general) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}