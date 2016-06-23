package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.constant.Reports;
import com.ghw.dao.AgentStateDetailDAO;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.UpdatedDateDAO;
import com.ghw.filter.AgentStateDetailFilter;
import com.ghw.filter.FilterBase;
import com.ghw.model.AgentStateDetail;
import com.ghw.model.LogSystem;
import com.ghw.model.UpdatedDate;
import com.ghw.model.User;
import com.ghw.service.AgentStateDetailService;
import com.ghw.util.IpServer;

@Service("agentStateDetailService")
@Transactional(readOnly = false)
public class AgentStateDetailServiceImpl extends
		ServiceImpl<AgentStateDetail, String, AgentStateDetailDAO> implements
		AgentStateDetailService {

	private AgentStateDetailDAO dao;

	@Autowired
	private LogSystemDAO logSystemDAO;

	@Autowired
	private UpdatedDateDAO updatedDateDAO;

	@Autowired
	public void setDAO(AgentStateDetailDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public void makePersistent(List<AgentStateDetail> agentStateDetails,
			Date dateProcess) {

		LogSystem log = new LogSystem();
		log.setMethod("makePersistent");
		log.setClassName("AgentStateDetail");

		boolean error = false;
		String detail = "";
		try {

			if (agentStateDetails != null && agentStateDetails.size() > 0) {

				// search all the AgentStateDetail in that date
				// if exists Agent state detail for that range date skip
				Long count = dao.getCountByDate(dateProcess);

				if (count == null || count.equals(0L)) {

					Integer total = 0;
					for (AgentStateDetail s : agentStateDetails) {

						dao.makePersistent(s);

						if (++total % 50 == 0) { // 500, same as the JDBC batch
													// size
							// flush a batch of inserts and release memory:
							dao.flush();
							dao.clear();
						}
					}

					dao.flush();

					detail = "Total inserted: " + agentStateDetails.size();

				}
			}

			UpdatedDate updatedDate = updatedDateDAO
					.getById(Reports.AGENT_STATE_DETAIL);
			updatedDate.setDateUpdated(DateUtils.addDays(dateProcess, 1));
			updatedDateDAO.update(updatedDate);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			error = true;
			detail += e.getMessage();
			throw e;
		} finally {

			log.setError(error);
			log.setDetail(detail);
			log.setIp(IpServer.ipServer());
			logSystemDAO.makePersistent(log);

		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public void saveOrUpdate(List<AgentStateDetail> agentStateDetails,
			Date dateProcess) {

		if (agentStateDetails != null && agentStateDetails.size() > 0) {

			LogSystem log = new LogSystem();
			log.setMethod("saverOrUpdate");
			log.setClassName("AgentStateDetail");

			boolean error = false;
			String detail = "";
			try {

				List<String> idsSystem = dao.getIdByDate(dateProcess);

				List<AgentStateDetail> inserted = new ArrayList<AgentStateDetail>();

				Integer total = 0, totalInserted = 0;
				for (AgentStateDetail s : agentStateDetails) {

					if (!idsSystem.contains(s.getId())) {

						inserted.add(s);
						totalInserted++;
						dao.makePersistent(s);
					}

					if (++total % 50 == 0) { // 500, same as the JDBC batch
												// size
						// flush a batch of inserts and release memory:
						dao.flush();
						dao.clear();
					}
				}

				dao.flush();

				// process the invoice of all the new one
				if (inserted.size() > 0) {

					for (AgentStateDetail agentStateDetail : inserted) {

					}
				}

				detail = "Total inserted: " + totalInserted;

			} catch (Exception e) {
				System.out.println(e.getMessage());
				error = true;
				detail += e.getMessage();
				throw e;
			} finally {

				log.setError(error);
				log.setDetail(detail);
				log.setIp(IpServer.ipServer());
				logSystemDAO.makePersistent(log);

			}
		}

	}

	@Override
	public List<AgentStateDetail> getData(FilterBase filter) throws Exception {
		AgentStateDetailFilter asdFilter = (AgentStateDetailFilter) filter;
		// if the user is an IBO only get the user's invoices
		User user = asdFilter.getUser();
		if (user.getType() != null && user.isAnIbo() && user.getIbo() != null
				&& user.getIbo().getId() != null) {
			asdFilter.setUserId(user.getId());
		}

		return super.getData(asdFilter);
	}

	@Override
	public Long count(FilterBase filter) throws Exception {
		AgentStateDetailFilter asdFilter = (AgentStateDetailFilter) filter;
		// if the user is an IBO only get the user's invoices
		User user = asdFilter.getUser();
		if (user.getType() != null && user.isAnIbo() && user.getIbo() != null
				&& user.getIbo().getId() != null) {
			asdFilter.setUserId(user.getId());
		}
		return super.count(asdFilter);
	}

	@Override
	public AgentStateDetail getWorkAfterTalking(String userId,
			AgentStateDetail asd) {
		return dao.getWorkAfterTalking(userId, asd.getEndDate());
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public List<AgentStateDetail> getDataForSchedule(String userId,
			Date startDate, Date endDate) {
		return dao.getDataForSchedule(userId, startDate, endDate);
	}

}