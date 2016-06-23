package com.ghw.service;

import java.nio.file.Path;
import java.util.List;

import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.Invoice;
import com.ghw.model.Profile;

public interface EmailService {

	public void sendWelcomeEmail(Profile profile);

	public void sendFinancialEmail(List<Path> files);

	public void sendQuestion(String from, String to, String bcc,
			String subject, String body) throws Exception;

	public void sendInvoiceEmail(Invoice invoice);

	public void sendInvoiceEmail(List<Invoice> listInvoices);

	public boolean confirmSMTP(ConfigurationGeneral general) throws Exception;

}