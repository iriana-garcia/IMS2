package com.ghw.dao;

import java.util.Date;
import java.util.List;

import com.ghw.model.AgentStateDetail;
import com.ghw.model.ClientApplication;
import com.ghw.model.Event;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceWork;
import com.ghw.model.Profile;
import com.ghw.model.User;

public interface InvoiceWorkDAO extends GenericDAO<InvoiceWork, String> {

	public boolean validateAssociateCA(ClientApplication ca);

	public int updateClientApplication(ClientApplication ca, Date dateMod,
			boolean modifyDescription, User userModify);

	public int updateEvents(ClientApplication ca, List<Event> events,
			User userModify);

	public int updateProfileRate(Profile profile, User userModify);

	public List<InvoiceWork> getDataAffectedByCAModification(
			ClientApplication ca, Date dateMod);

	public List<InvoiceWork> getDataByInvoice(Invoice invoice);

	public List<InvoiceWork> getDataByInvoice(List<Invoice> invoices);

	public int updateInvoice(Invoice invoice, User userModify);

	public int updateInvoice(Invoice invoice, String workId, User userModify);

	public List<InvoiceWork> getDataByDateUser(AgentStateDetail asd);

	public int updateActualService(InvoiceWork iw, User userModify);
}