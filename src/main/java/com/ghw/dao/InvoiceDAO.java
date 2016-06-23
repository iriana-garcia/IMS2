package com.ghw.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ghw.filter.InvoiceFilter;
import com.ghw.model.Invoice;
import com.ghw.model.Profile;
import com.ghw.model.User;

public interface InvoiceDAO extends GenericDAO<Invoice, String> {

	public boolean validateAssociateIbo(String idUser);

	public boolean validateIfExists(Date dateStart, Date dateEnd);

	public boolean validateIfExists(Date dateStart, Date dateEnd, Profile ibo);

	public List<Invoice> getDataActiveOracle();

	public List<Invoice> getDataActivePayPal();

	public void updateNeedUpdatedFalse(List<Invoice> invoices);

	public Invoice getCurrentInvoice(Profile user);

	public Invoice getCurrentInvoice(String invoiceId);

	public void updateSendQuestion(Invoice invoice);

	public void changeState(Invoice invoice, String state, User user);

	public Invoice getTotalInvoices(InvoiceFilter filter) throws Exception;

	public void updateAdminFee(Invoice invoice, User user);

	public void updateNotes(Invoice invoice, User user);

	public List<Invoice> getDataPendingByIbo(String idIbo);

	public void resubmitInvoice(Invoice invoice, User user);

	public Invoice loadAllById(Invoice entity);

	public int cancelInvoices(Date date);

	public int submitInternationInvoices();

	public Invoice getDataForWeek(Date dateStartWeek, String userId);

	public Map<String, String> getDataForWeek(Date dateStartWeek);

	public List<Invoice> getDataPendingOrSubmitted(String userId,
			Date dateInvoiceWork);
	
	public Invoice getDataForEndWeek(Date dateEndWeek, String userId);
}