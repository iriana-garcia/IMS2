package com.ghw.service;

import java.util.Date;
import java.util.List;

import com.ghw.model.AgentStateDetail;

public interface AgentStateDetailService extends
		Service<AgentStateDetail, String> {

	public void makePersistent(List<AgentStateDetail> agentStateDetails,
			Date dateProcess);

	public void saveOrUpdate(List<AgentStateDetail> agentStateDetails,
			Date dateProcess);

	public AgentStateDetail getWorkAfterTalking(String userId, AgentStateDetail asd);
	
	public List<AgentStateDetail> getDataForSchedule(String userId,
			Date startDate, Date endDate);
}