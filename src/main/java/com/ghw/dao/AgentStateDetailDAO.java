package com.ghw.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.ghw.model.AgentStateDetail;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceWork;

public interface AgentStateDetailDAO extends
		GenericDAO<AgentStateDetail, String> {

	public Long getCountByDate(Date dateStart);

	public List<String> getIdByDate(Date date);

	public List<AgentStateDetail> getDataForSchedule(String userId,
			Date startDate, Date endDate);

	public int update(List<String> ids, InvoiceWork invoiceWork);

	public List<AgentStateDetail> getDataByInvoice(Invoice invoice);

	public void updatePendingDuration(Set<AgentStateDetail> asd);

	public AgentStateDetail getWorkAfterTalking(String userId, Date endDate);
}