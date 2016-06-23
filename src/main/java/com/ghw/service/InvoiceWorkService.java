package com.ghw.service;

import java.util.Date;
import java.util.List;

import com.ghw.model.AgentStateDetail;
import com.ghw.model.ClientApplication;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceWork;
import com.ghw.model.ProblemInvoice;
import com.ghw.model.User;

public interface InvoiceWorkService extends Service<InvoiceWork, String> {

	public boolean validateAssociateCA(ClientApplication ca);

	public List<InvoiceWork> getDataAffectedByCAModification(
			ClientApplication ca, Date dateMod);

	public void createInvoiceWork(Date dateToProcess);

	public ProblemInvoice updateInvoice(Invoice invoice,
			ProblemInvoice problemInvoice, User user);

	public void addInvoiceWork(List<AgentStateDetail> listAgent);
}