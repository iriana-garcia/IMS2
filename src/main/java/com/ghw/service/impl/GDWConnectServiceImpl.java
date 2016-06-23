package com.ghw.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ghw.constant.Reports;
import com.ghw.controller.ApplicationBean;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.UpdatedDateDAO;
import com.ghw.model.AgentStateDetail;
import com.ghw.model.Event;
import com.ghw.model.LogSystem;
import com.ghw.model.Schedule;
import com.ghw.model.SkillPhoneSystem;
import com.ghw.model.UpdatedDate;
import com.ghw.service.AgentStateDetailService;
import com.ghw.service.EventService;
import com.ghw.service.GDWConnectService;
import com.ghw.service.InvoiceWorkService;
import com.ghw.service.ScheduleService;
import com.ghw.service.SkillPhoneSystemService;
import com.ghw.util.Context;
import com.ghw.util.IpServer;
import com.ghw.util.RestUtil;

@Component("gdwConnectService")
public class GDWConnectServiceImpl implements GDWConnectService {

	@Autowired
	private RestUtil restUtil;

	@Autowired
	private ApplicationBean applicationBean;

	@Autowired
	private LogSystemDAO logSystemDAO;

	@Autowired
	private EventService eventService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private SkillPhoneSystemService skillPhoneSystemService;

	@Autowired
	private UpdatedDateDAO updatedDateDAO;

	@Autowired
	private AgentStateDetailService agentStateDetailService;

	@Autowired
	private InvoiceWorkService invoiceWorkService;

	public void processScheduleOnlyTest() {

		Calendar startDateCalendar = new GregorianCalendar();
		startDateCalendar.set(Calendar.MONTH, 4);
		startDateCalendar.set(Calendar.DAY_OF_MONTH, 29);
		startDateCalendar.set(Calendar.YEAR, 2016);

		// make the update per day
		for (int i = 0; i < 2; i++) {

			// process the invoice work
			invoiceWorkService.createInvoiceWork(startDateCalendar.getTime());

			// when finish the process add a day to start day
			startDateCalendar.add(Calendar.DAY_OF_WEEK, 1);

		}

	}

	public void processSkillsFromGDW() {

		try {

			String urlWebService = applicationBean.getConfiguration()
					.getWebservice();

			// sync schedule, event and agent state from GDW
			String token = restUtil.getToken(urlWebService);

			if (StringUtils.isNotBlank(token)
					&& StringUtils.isNotBlank(urlWebService)) {

				// skills
				String bodySkill = restUtil.getBody(token, urlWebService,
						RestUtil.urlSkillsPhoneSystem);

				if (bodySkill != null) {
					List<SkillPhoneSystem> skills = restUtil
							.transformToSkill(bodySkill);
					skillPhoneSystemService.saverOrUpdate(skills);
				}
			}

		} catch (Exception e) {

			String errorMessage = Context.getUIMsg(e.getMessage());
			// insert logg system like an error
			LogSystem log = new LogSystem();
			log.setClassName("GDWConnect");
			log.setDetail("Error: "
					+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
							: errorMessage));
			log.setMethod("processSkillsFromGDW");
			log.setUser(null);
			log.setError(true);
			log.setIp(IpServer.ipServer());

			logSystemDAO.makePersistent(log);

		}
	}

	public void processDataFromGDW() {

		try {

			String urlWebService = applicationBean.getConfiguration()
					.getWebservice();

			// sync schedule, event and agent state from GDWs
			String token = restUtil.getToken(urlWebService);

			if (StringUtils.isNotBlank(token)
					&& StringUtils.isNotBlank(urlWebService)) {

				// get all the report active with the day from updated is less
				// than
				// yesterday
				Calendar yesterday = new GregorianCalendar();
				yesterday.setTime(new Date());
				yesterday.add(Calendar.DAY_OF_WEEK, -1);

				List<UpdatedDate> listNeedToUpdate = updatedDateDAO
						.getDataPendingUpdate(yesterday.getTime());

				for (UpdatedDate updatedDate : listNeedToUpdate) {
					switch (updatedDate.getId()) {
					case 1: {
						// event
						String bodyEvent = restUtil.getBody(token,
								urlWebService, RestUtil.urlEvent);
						if (bodyEvent != null) {
							List<Event> events = restUtil
									.transformToEvent(bodyEvent);
							eventService.saverOrUpdate(events);
						}
					}
						break;
					case 2: {
						// schedule
						String bodySchedule = restUtil.getBody(
								token,
								urlWebService,
								RestUtil.urlSchedule
										+ (new SimpleDateFormat("MMddyyyy"))
												.format(updatedDate
														.getDateUpdated()));
						if (bodySchedule != null) {
							List<Schedule> schedules = restUtil
									.transformToSchedule(bodySchedule);
							scheduleService.makePersistent(schedules);
						}
					}
						break;
					// case 3: {
					// // skills
					// String bodySkill = restUtil.getBody(token,
					// urlWebService, RestUtil.urlSkills);
					//
					// if (bodySkill != null) {
					// List<Skill> skills = restUtil
					// .transformToSkill(bodySkill);
					// skillService.saverOrUpdate(skills);
					// }
					// }
					// break;
					case 4: {
						if (new GregorianCalendar().get(Calendar.HOUR_OF_DAY) >= 4)
							processAgentStateDetail(token, urlWebService);

					}

					}
				}
			}
		} catch (Exception e) {

			String errorMessage = Context.getUIMsg(e.getMessage());
			// insert logg system like an error
			LogSystem log = new LogSystem();
			log.setClassName("GDWConnect");
			log.setDetail("Error: "
					+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
							: errorMessage));
			log.setMethod("processDataFromGDW");
			log.setUser(null);
			log.setError(true);
			log.setIp(IpServer.ipServer());

			logSystemDAO.makePersistent(log);

		}

	}

	private void callMethod(String token, String urlWebService, Date date)
			throws ParseException {

		String bodyAgentState = restUtil.getBody(token, urlWebService,
				RestUtil.urlAgentStateDetail
						+ (new SimpleDateFormat("MMddyyyy")).format(date));
		if (bodyAgentState != null) {
			List<AgentStateDetail> agentStateDetails = restUtil
					.transformToAgentStateDetail(bodyAgentState);

			// insert the agent state detail
			agentStateDetailService.makePersistent(agentStateDetails, date);

			// process the invoice work
			invoiceWorkService.createInvoiceWork(date);
		}

	}

	private void processAgentStateDetail(String token, String urlWebService)
			throws ParseException {

		UpdatedDate updatedDate = updatedDateDAO
				.getById(Reports.AGENT_STATE_DETAIL);

		int dayDifference = (int) ((new Date().getTime() - updatedDate
				.getDateUpdated().getTime()) / (1000 * 60 * 60 * 24));

		Calendar dateUpdate = new GregorianCalendar();
		dateUpdate.setTime(updatedDate.getDateUpdated());
		// analyzes if exists more than one day that need update
		if (dayDifference > 0
				|| (dayDifference == 0 && new GregorianCalendar()
						.get(Calendar.DAY_OF_MONTH) != dateUpdate
						.get(Calendar.DAY_OF_MONTH))) {

			Calendar startDateCalendar = new GregorianCalendar();
			startDateCalendar.setTime(updatedDate.getDateUpdated());

			// make the update per day
			for (int i = 0; i < dayDifference + 1; i++) {

				// never process the actual day
				if (new GregorianCalendar().get(Calendar.DAY_OF_MONTH) != dateUpdate
						.get(Calendar.DAY_OF_MONTH)) {

					callMethod(token, urlWebService,
							startDateCalendar.getTime());

					// when finish the process add a day to start day
					startDateCalendar.add(Calendar.DAY_OF_WEEK, 1);
				}

			}

			// if day difference is more than one is not necessary to ask for
			// change because am pulling historical data, only if is one
			// if (dayDifference <= 1) {
			//
			// // agent state detail update two day before
			// Calendar calendar = new GregorianCalendar();
			// calendar.setTime(new Date());
			// calendar.add(Calendar.DAY_OF_WEEK, -2);
			//
			// String bodyAgentStateUpdated = restUtil.getBody(
			// token,
			// urlWebService,
			// RestUtil.urlAgentStateDetailUpdate
			// + (new SimpleDateFormat("MMddyyyy"))
			// .format(calendar.getTime()));
			// if (bodyAgentStateUpdated != null) {
			// List<AgentStateDetail> agentStateDetailsUpdate = restUtil
			// .transformToAgentStateDetail(bodyAgentStateUpdated);
			//
			// agentStateDetailService.saveOrUpdate(
			// agentStateDetailsUpdate,
			// startDateCalendar.getTime());
			// }
			// }

		}

	}
}
