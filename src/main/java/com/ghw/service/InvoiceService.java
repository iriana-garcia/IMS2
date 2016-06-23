package com.ghw.service;

import java.util.Date;
import java.util.List;

import com.ghw.filter.FilterBase;
import com.ghw.model.Invoice;
import com.ghw.model.Profile;
import com.ghw.model.User;

public interface InvoiceService extends Service<Invoice, String> {

	public void createInvoiceIbo(Profile profile, User userModif);

	public void makePersistent();

	public void makePersistent(Date ini, Date fin);

	public Invoice getCurrentInvoice(Profile user);

	public Invoice getCurrentInvoice(String invoiceId);

	public Invoice submitInvoice(Invoice invoice, User userSession);

	public Invoice getTotalInvoices(FilterBase filter) throws Exception;

	public Invoice updateAdminFee(Invoice invoice, Double oldValue,
			Double newValue, User userSession);

	public Invoice updateNote(Invoice invoice, User userSession);

	public Invoice cancelInvoice(Invoice invoice, User userSession);

	public void submitOrCancelInvoice(User user, User userSession);

	public Invoice resubmittInvoice(Invoice invoice, User userSession);

	// public void approbalInvoice(List<Invoice> invoice);

	public Invoice approbalInvoice(Invoice invoice, User userSession);

	public Invoice loadAllById(Invoice entity) throws Exception;

	public void cancelInvoice(Date date);

	public List<Invoice> getDataActiveOracle();

	public void approbalInvoice(List<Invoice> invoice, User userSession,
			String message) throws Exception;

	public void submitInternationalInvoices();

	public List<Invoice> getDataPendingOrSubmitted(String userId,
			Date dateInvoiceWork);
}