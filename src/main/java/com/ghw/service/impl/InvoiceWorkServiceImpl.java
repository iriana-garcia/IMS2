package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.constant.EventType;
import com.ghw.controller.ApplicationBean;
import com.ghw.dao.AgentStateDetailDAO;
import com.ghw.dao.InvoiceDAO;
import com.ghw.dao.InvoiceWorkDAO;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.ProfileDAO;
import com.ghw.dao.ReasonCodePayDAO;
import com.ghw.dao.ScheduleDAO;
import com.ghw.model.AgentStateDetail;
import com.ghw.model.ClientApplication;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceWork;
import com.ghw.model.LogSystem;
import com.ghw.model.ProblemInvoice;
import com.ghw.model.ReasonCodePay;
import com.ghw.model.Schedule;
import com.ghw.model.User;
import com.ghw.service.AgentStateDetailService;
import com.ghw.service.InvoiceWorkService;
import com.ghw.util.DateUtil;
import com.ghw.util.IpServer;

@Service("invoiceWorkService")
@Transactional(readOnly = false)
public class InvoiceWorkServiceImpl extends
		ServiceImpl<InvoiceWork, String, InvoiceWorkDAO> implements
		InvoiceWorkService {

	private InvoiceWorkDAO dao;

	@Autowired
	private InvoiceDAO invoiceDAO;

	@Autowired
	private ScheduleDAO scheduleDAO;

	@Autowired
	private ApplicationBean applicationBean;

	@Autowired
	private AgentStateDetailDAO agentStateDetailDAO;

	@Autowired
	private AgentStateDetailService agentStateDetailService;

	@Autowired
	private ProfileDAO profileDAO;

	@Autowired
	private LogSystemDAO logSystemDAO;

	@Autowired
	private ReasonCodePayDAO reasonCodePayDAO;

	private List<AgentStateDetail> listAgentStateDetailUpdate = new ArrayList<AgentStateDetail>();

	// save the state that need to be inserted like pending
	private Set<AgentStateDetail> listAgentStateDetailInsertPending = new LinkedHashSet<AgentStateDetail>();

	private Long durationFinal = 0L, durationPending = 0L;

	private Long durationNextInvoice, durationPrevInvoice;

	private Double totalNotReadyTime, actualService, serviceRevenue;

	@Autowired
	public void setDAO(InvoiceWorkDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean validateAssociateCA(ClientApplication ca) {
		return dao.validateAssociateCA(ca);
	}

	/**
	 * Get all the invoice related to a client application in state pending or
	 * submitted
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InvoiceWork> getDataAffectedByCAModification(
			ClientApplication ca, Date dateMod) {
		return dao.getDataAffectedByCAModification(ca, dateMod);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public void createInvoiceWork(Date dateToProcess) {

		LogSystem log = new LogSystem();
		log.setMethod("createInvoiceWork");
		log.setClassName("InvoiceWork");

		boolean error = false;
		String detail = "Created invoice work. ";
		try {

			// get all the schedule for yesterday
			List<Schedule> schedules = scheduleDAO
					.getDataByDatePending(dateToProcess);

			if (schedules.size() == 0)
				return;

			// search the reason code that we pay
			List<ReasonCodePay> reasonCodePays = reasonCodePayDAO.findAll();

			detail += " Total schedules processed: " + schedules.size();

			// get the buffer (is in minutes)
			Integer buffer = applicationBean.getSystemConf().getBuffer();
			if (buffer == null)
				buffer = 0;

			// get all the profile's rate
			Map<String, Double> rates = profileDAO.getRate();

			// // get all the invoices for this week
			Map<String, String> invoices = invoiceDAO
					.getDataForWeek(dateToProcess);

			// if not exist invoice out
			// if (invoices.size() == 0)
			// return;

			// every schedule is an row in Invoice work
			for (Schedule schedule : schedules) {

				// ini the variables
				totalNotReadyTime = 0.00;
				actualService = 0.00;
				serviceRevenue = 0.00;
				durationNextInvoice = 0L;
				durationPrevInvoice = 0L;

				InvoiceWork invoiceWork = new InvoiceWork();
				// save the buffer in this moment
				invoiceWork.setBuffer(buffer);

				// search the invoice for the user
				String invoiceId = invoices.get(schedule.getUser().getId());
				// if not exists an invoice pass to the next schedule
				if (invoiceId != null) {
					invoiceWork.setInvoice(invoiceDAO.loadById(invoiceId));
				}

				// the event and program description is save
				invoiceWork.setSkillId(schedule.getEvent().getId());
				invoiceWork.setSkillName(schedule.getEvent().getName());
				if (schedule.getClientApplication() != null) {
					invoiceWork.setCliId(schedule.getClientApplication()
							.getId());
					invoiceWork.setCliName(schedule.getClientApplication()
							.getName());
				}
				invoiceWork.setSchStartTime(schedule.getDateStart());
				invoiceWork.setSchEndTime(schedule.getDateEnd());

				// if the IBO has a rate > 0 this override the Program's rate
				Double rate = 0.0;
				Double rateIbo = rates.get(schedule.getUser().getId());
				if (rateIbo != null && rateIbo > 0) {
					rate = rateIbo;
				} else if (schedule.getClientApplication() != null) {
					rate = schedule.getClientApplication().getAmount();
				}

				// Invoice Import total =
				// Summatory((ActualService+HourAdded)*Rate)
				// + Summatory Incentive - Admin Fee

				// ActualService = Time in state: Ready, Reserved, Talking, Work
				// and Not Ready with some code reason: Incontact: 660 ACW
				// Wrapup,
				// Cisco: 32756, 32758, 32761, 32762

				// get all the agent state detail the schedule rate, I need the
				// add
				// in the start date the buffer
				// Buffer is per schedule (program) se aplica only before, after
				// only pay when is talking Por ejemplo: si el schedule es hasta
				// 1pm
				// y empezo a hablar a 12:55pm se le paga hasta q termina la
				// llamada. OJO VER Q NO LO COJA EL BUFFER DEL PROXIMO SCHEDULE
				Calendar dateAddBuffer = new GregorianCalendar();
				dateAddBuffer.setTime(schedule.getDateStart());
				dateAddBuffer.add(Calendar.MINUTE, -buffer);

				Date dateBuffer = dateAddBuffer.getTime();
				// need to process the date end to get the last seconds and
				// miliseconds
				Calendar startDateWithSecAndMil = new GregorianCalendar();
				startDateWithSecAndMil.setTime(schedule.getDateStart());
				startDateWithSecAndMil.set(Calendar.SECOND, 0);
				startDateWithSecAndMil.set(Calendar.MILLISECOND, 0);

				Calendar endDateWithSecAndMil = new GregorianCalendar();
				endDateWithSecAndMil.setTime(schedule.getDateEnd());
				endDateWithSecAndMil.set(Calendar.SECOND, 59);
				endDateWithSecAndMil.set(Calendar.MILLISECOND, 999);

				List<AgentStateDetail> agentStateDetails = agentStateDetailDAO
						.getDataForSchedule(schedule.getUser().getId(),
								dateBuffer, endDateWithSecAndMil.getTime());

				for (AgentStateDetail asd : agentStateDetails) {

					// if the schedule is between Saturday and Sunday make
					// cutoff
					Calendar calendarStart = new GregorianCalendar();
					calendarStart.setTime(dateBuffer);

					Calendar calendar = new GregorianCalendar();
					calendar.setTime(schedule.getDateEnd());
					// if the call is between Saturday and Sunday,
					// cutoff
					if (calendarStart.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
							&& calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

						// deal with 2 schedule
						// fist result go to the invoice preview week
						// process the 4 dates
						// save the end date phone for pass in the second
						Date iniDatePhone = asd.getStartDate();
						Date endDatePhone = asd.getEndDate();
						Integer duration = asd.getDuration();

						Date dateEndSaturday = DateUtil
								.getEndOfDay(dateAddBuffer.getTime());
						// if phone record is all before sunday the duration is
						// complete the the fisrt part
						Integer durationFirst = 0;
						if (asd.getStartDate().before(dateEndSaturday)
								&& asd.getEndDate().before(dateEndSaturday)) {
							durationFirst = asd.getDuration();
						} else if (asd.getStartDate().before(dateEndSaturday)
								&& asd.getEndDate().after(dateEndSaturday)) {
							// if the phone records is between the saturday and
							// sunday need to split
							durationFirst = new Long(dateEndSaturday.getTime()
									- iniDatePhone.getTime()).intValue();
						} else if (asd.getStartDate().after(dateEndSaturday)) {
							// if start after sunday the duration in the first
							// part is 0
							durationFirst = 0;
						}

						asd.setEndDate(asd.getEndDate().after(dateEndSaturday) ? dateEndSaturday
								: endDatePhone);
						asd.setDuration(durationFirst);
						Calendar calendarEnd = new GregorianCalendar();
						calendarEnd.setTime(dateEndSaturday);

						if (durationFirst > 0) {
							// process Saturday
							matchScheduleWithPhoneSystemRecord(asd,
									dateAddBuffer, startDateWithSecAndMil,
									calendarEnd, invoiceWork, reasonCodePays,
									true, false);
						}

						durationPending = 0L;

						Date dateIniSunday = DateUtil.getIniOfDay(schedule
								.getDateEnd());
						Calendar calendarDateIniSunday = new GregorianCalendar();
						calendarDateIniSunday.setTime(dateIniSunday);
						// the second go to the actual week
						asd.setStartDate(asd.getStartDate().before(
								dateIniSunday) ? dateIniSunday : asd
								.getStartDate());
						asd.setEndDate(endDatePhone);
						asd.setDuration(duration - durationFirst);
						// process Sunday
						if (asd.getDuration() > 0) {
							matchScheduleWithPhoneSystemRecord(asd,
									calendarDateIniSunday,
									startDateWithSecAndMil,
									endDateWithSecAndMil, invoiceWork,
									reasonCodePays, false, true);
						}
						// keep the old date IMPORTANT for no update the
						// register
						asd.setStartDate(iniDatePhone);
						asd.setEndDate(endDatePhone);
						asd.setDuration(duration);

					} else {
						matchScheduleWithPhoneSystemRecord(asd, dateAddBuffer,
								startDateWithSecAndMil, endDateWithSecAndMil,
								invoiceWork, reasonCodePays, false, false);
					}

					// if it's talking and the call end after schedule time
					// need to get to the work time
					if (asd.getEventType().equals(EventType.TALKING)
							&& asd.getEndDate().after(schedule.getDateEnd())) {

						AgentStateDetail asdWorkTime = agentStateDetailDAO
								.getWorkAfterTalking(
										schedule.getUser().getId(),
										asd.getEndDate());
						if (asdWorkTime != null) {
							matchScheduleWithPhoneSystemRecord(asdWorkTime,
									dateAddBuffer, startDateWithSecAndMil,
									endDateWithSecAndMil, invoiceWork,
									reasonCodePays, false, false);
						}

					}

				}

				// set all the invoice work to the phone system
				for (AgentStateDetail as : listAgentStateDetailUpdate) {
					as.setInvoiceWork(invoiceWork);
				}

				// if (actualService < 0)
				// System.out.println("Problemmmmm testing");

				invoiceWork.setTotalNotReadyTimeMili(totalNotReadyTime
						.intValue());
				invoiceWork.setActualServiceMili(actualService.intValue());

				// convert miliseconds to hours
				actualService = MathUtils.round(actualService
						/ new Double(60 * 60 * 1000), 2);

				totalNotReadyTime = MathUtils.round(totalNotReadyTime
						/ new Double(60 * 60 * 1000), 2);

				serviceRevenue = actualService * rate;

				invoiceWork.setActualService(actualService);
				invoiceWork.setAmount(rate);
				invoiceWork.setServiceRevenue(serviceRevenue);
				invoiceWork.setTotalNotReadyTime(totalNotReadyTime);
				invoiceWork.setUser(schedule.getUser());
				invoiceWork = dao.makePersistent(invoiceWork);

				dao.flush();

				// update the schedule with the invoice work associate
				scheduleDAO.update(schedule, invoiceWork);
				scheduleDAO.flush();

				// update all the agent state detail with the invoice work
				// associate
				// agentStateDetailDAO.update(listAgentStateDetailUpdate,
				// invoiceWork);
				// agentStateDetailDAO.flush();
				//
				// // insert all the duration pending
				// agentStateDetailDAO
				// .updatePendingDuration(listAgentStateDetailInsertPending);
				// agentStateDetailDAO.flush();

				if (durationNextInvoice > 0) {

					InvoiceWork invoiceWorkNextInvoice = new InvoiceWork();
					invoiceWorkNextInvoice.copy(invoiceWork);

					invoiceWorkNextInvoice.setTotalNotReadyTimeMili(0);
					invoiceWorkNextInvoice
							.setActualServiceMili(durationNextInvoice
									.intValue());
					// set the duration and import
					actualService = MathUtils.round(durationNextInvoice
							/ new Double(60 * 60 * 1000), 2);
					serviceRevenue = actualService * rate;
					invoiceWorkNextInvoice.setActualService(actualService);
					invoiceWorkNextInvoice.setServiceRevenue(serviceRevenue);

					// set the invoice
					Invoice invoice = invoiceDAO.getDataForWeek(DateUtils
							.addDays(dateToProcess, 1), schedule.getUser()
							.getId());

					if (invoice != null) {
						invoiceWorkNextInvoice.setInvoice(invoice);
					}

					dao.makePersistent(invoiceWorkNextInvoice);
					dao.flush();

				} else if (durationPrevInvoice > 0) {
					InvoiceWork invoiceWorkPrevInvoice = new InvoiceWork();
					invoiceWorkPrevInvoice.copy(invoiceWork);

					invoiceWorkPrevInvoice.setTotalNotReadyTimeMili(0);
					invoiceWorkPrevInvoice
							.setActualServiceMili(durationPrevInvoice
									.intValue());
					// set the duration and import
					actualService = MathUtils.round(durationPrevInvoice
							/ new Double(60 * 60 * 1000), 2);
					serviceRevenue = actualService * rate;
					invoiceWorkPrevInvoice.setActualService(actualService);
					invoiceWorkPrevInvoice.setServiceRevenue(serviceRevenue);

					// set the invoice
					Invoice invoice = invoiceDAO.getDataForEndWeek(DateUtils
							.addDays(dateToProcess, -1), schedule.getUser()
							.getId());

					if (invoice != null)
						invoiceWorkPrevInvoice.setInvoice(invoice);

					dao.makePersistent(invoiceWorkPrevInvoice);
					dao.flush();
				}

			}

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

	private void matchScheduleWithPhoneSystemRecord(AgentStateDetail asd,
			Calendar dateBufferAdd, Calendar startDateWithSecAndMil,
			Calendar endDateWithSecAndMil, InvoiceWork invoiceWork,
			List<ReasonCodePay> reasonCodePays, boolean isForPrevius,
			boolean exceptDurationPending) {

		System.out.println("Event: " + asd.getEventTypeName());

		// the buffer time is only for Talking, Reserved and Ready state
		Date dateStartAnalisis = null;
		if (asd.getEventType().equals(EventType.TALKING)
				|| asd.getEventType().equals(EventType.RESERVED)
				|| asd.getEventType().equals(EventType.READY)) {
			dateStartAnalisis = dateBufferAdd.getTime();
		} else {
			dateStartAnalisis = startDateWithSecAndMil.getTime();
		}

		// if the Agent state detail is out of range do no process
		if (asd.getEndDate().before(dateStartAnalisis)
				|| asd.getStartDate().after(endDateWithSecAndMil.getTime())
				&& (!asd.getEventType().equals(EventType.WORK) || (asd
						.getEventType().equals(EventType.WORK) && asd
						.getEndDate().before(dateStartAnalisis)))) {
			return;
		}

		if (!exceptDurationPending && asd.getDurationPending() != null
				&& asd.getDurationPending() != 0) {
			// means the other schedule take it first so I need
			// analize only the pending time
			durationFinal = asd.getDurationPending().longValue();
		} else {
			durationFinal = asd.getDuration().longValue();
		}

		// save if the records it is include or not in the invoice
		boolean isIncludeInvoice = true;

		durationPending = durationFinal;

		// process the date
		// if start date is before the schedule start date + buffer
		// cut
		// the duration
		// if the phone system record is more than schedule cutoff
		if (asd.getStartDate().before(dateStartAnalisis)
				&& asd.getEndDate().after(endDateWithSecAndMil.getTime())) {
			// if was talking cut only at the beginning
			if (asd.getEventType().equals(EventType.TALKING)
					|| asd.getEventType().equals(EventType.WORK)) {
				Calendar calendarStart = new GregorianCalendar();
				calendarStart.setTime(asd.getStartDate());

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(asd.getEndDate());
				// if the call is between Saturday and Sunday,
				// cutoff
				if (calendarStart.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						&& calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

					durationFinal = DateUtil.getEndOfDay(asd.getStartDate())
							.getTime() - dateStartAnalisis.getTime();

					// get the next invoice and insert this schedule
					// to in the invoice
					Long dnext = asd.getEndDate().getTime()
							- DateUtil.getIniOfDay(asd.getEndDate()).getTime();
					durationNextInvoice += dnext;

					durationPending -= dnext;

				} else {
					durationFinal = asd.getEndDate().getTime()
							- dateStartAnalisis.getTime();
				}
			} else {
				durationFinal = endDateWithSecAndMil.getTimeInMillis()
						- dateStartAnalisis.getTime();
			}

		} else if (asd.getStartDate().before(dateStartAnalisis)
				&& asd.getEndDate().before(dateStartAnalisis)) {

			durationFinal = 0L;
			isIncludeInvoice = false;

		} else if (asd.getStartDate().before(dateStartAnalisis)) {
			// need to cut off, NEVER pay before buffer time
			durationFinal = asd.getEndDate().getTime()
					- dateStartAnalisis.getTime();

		} else if (asd.getEndDate().after(endDateWithSecAndMil.getTime())) {
			// if is NOT talking cut off the duration
			if (!asd.getEventType().equals(EventType.TALKING)
					&& !asd.getEventType().equals(EventType.WORK)) {
				durationFinal = endDateWithSecAndMil.getTime().getTime()
						- asd.getStartDate().getTime();
			}// if is talking and is saturday cutoff at midnight and
				// pass after midnight to the next invoice
			else {
				Calendar calendarStart = new GregorianCalendar();
				calendarStart.setTime(asd.getStartDate());

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(asd.getEndDate());
				// if the call is between Saturday and Sunday,
				// cutoff
				if (calendarStart.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						&& calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

					durationFinal = DateUtil.getEndOfDay(asd.getStartDate())
							.getTime() - asd.getStartDate().getTime();

					// get the next invoice and insert this schedule
					// to in the invoice
					Long dnext = asd.getEndDate().getTime()
							- DateUtil.getIniOfDay(asd.getEndDate()).getTime();
					durationNextInvoice += dnext;

					durationPending -= dnext;

				}
			}

		}

		// if duration pending is less
		if (durationFinal > durationPending) {
			durationFinal = durationPending;
		}

		switch (asd.getEventType()) {
		case 1:// Log In
		case 7:// Log Out
			if (!(asd.getStartDate().after(dateStartAnalisis) && asd
					.getStartDate().before(endDateWithSecAndMil.getTime())))
				isIncludeInvoice = false;
			durationPending = 0L;
			listAgentStateDetailUpdate.add(asd);
			break;
		case 2:// Not ready
			if (asd.isReasonCodeWorking(reasonCodePays)) {
				actualService += durationFinal;
				durationPending -= durationFinal;
			} else {
				totalNotReadyTime += durationFinal.intValue();
				// durationPending = 0L;
				durationPending -= durationFinal;
			}
			break;
		case 3:// Ready
		case 4:// Reserved
		case 5:// Talking
		case 6:// Work
			if (isForPrevius)
				durationPrevInvoice += durationFinal;
			else if (durationNextInvoice > 0
					&& asd.getEventType().equals(EventType.WORK))
				durationNextInvoice += durationFinal;
			else
				actualService += durationFinal;

			durationPending -= durationFinal;

			break;
		}

		if (isIncludeInvoice && asd.getDurationPending() != null
				&& asd.getDurationPending() > 0) {
			// if come with initial duration always update the records
			asd.setInvoiceWorkPending(invoiceWork);
			// listAgentStateDetailInsertPending.add(asd);
		} else if (isIncludeInvoice && durationFinal > 0) {
			// if is the first time that this phone record is treatment update
			// the invoice work associate
			// asd.setInvoiceWork(invoiceWork);
			listAgentStateDetailUpdate.add(asd);
		}

		// if the phone record come with duration pending initial or now has
		// duration pending need to update the record
		if (exceptDurationPending) {
			// means that the phone records was split in two so the exception I
			// need to sum and no rest
			asd.setDurationPending((asd.getDurationPending() == null ? 0 : asd
					.getDurationPending()) + durationPending.intValue());
		} else if (isIncludeInvoice) {
			asd.setDurationPending(durationPending.intValue());
		}

		// if (isIncludeInvoice && durationPending != null && durationPending >
		// 0) {
		// rest all the duration to the duration initial
		// this is only for the states that we paid
		// update all the pending work

		// listAgentStateDetailInsertPending.add(asd);
		// }

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public void addInvoiceWork(List<AgentStateDetail> listAgent) {

		LogSystem log = new LogSystem();
		log.setMethod("addInvoiceWork");
		log.setClassName("InvoiceWork");

		boolean error = false;
		String detail = "Add invoice work. ";
		try {

			// search the reason code that we pay
			List<ReasonCodePay> reasonCodePays = reasonCodePayDAO.findAll();

			// search the invoice work existing for that user, invoice status
			// must to be Pending and Submitted
			for (AgentStateDetail asd : listAgent) {

				List<InvoiceWork> invoiceWorks = dao.getDataByDateUser(asd);
				for (InvoiceWork invoiceWork : invoiceWorks) {

					// ini the variables
					totalNotReadyTime = 0.00;
					actualService = 0.00;
					serviceRevenue = 0.00;
					durationNextInvoice = 0L;
					durationFinal = 0L;
					durationPending = 0L;

					Calendar dateAddBuffer = new GregorianCalendar();
					dateAddBuffer.setTime(invoiceWork.getSchStartTime());
					dateAddBuffer
							.add(Calendar.MINUTE, -invoiceWork.getBuffer());

					// need to process the date end to get the last seconds
					// and
					// miliseconds

					Calendar startDateWithSecAndMil = new GregorianCalendar();
					startDateWithSecAndMil.setTime(invoiceWork
							.getSchStartTime());
					startDateWithSecAndMil.set(Calendar.SECOND, 0);
					startDateWithSecAndMil.set(Calendar.MILLISECOND, 0);

					Calendar endDateWithSecAndMil = new GregorianCalendar();
					endDateWithSecAndMil.setTime(invoiceWork.getSchEndTime());
					endDateWithSecAndMil.set(Calendar.SECOND, 59);
					endDateWithSecAndMil.set(Calendar.MILLISECOND, 999);

					matchScheduleWithPhoneSystemRecord(asd, dateAddBuffer,
							startDateWithSecAndMil, endDateWithSecAndMil,
							invoiceWork, reasonCodePays, false, false);

					// add the work to the invoice
					if (actualService > 0 || totalNotReadyTime > 0) {

						invoiceWork.setTotalNotReadyTimeMili(invoiceWork
								.getTotalNotReadyTimeMili()
								+ totalNotReadyTime.intValue());
						invoiceWork.setActualServiceMili(invoiceWork
								.getActualServiceMili()
								+ actualService.intValue());

						// convert miliseconds to hours
						actualService = MathUtils.round(actualService
								/ new Double(60 * 60 * 1000), 2);

						totalNotReadyTime = MathUtils.round(totalNotReadyTime
								/ new Double(60 * 60 * 1000), 2);

						serviceRevenue = actualService
								* invoiceWork.getAmount();

						invoiceWork.setActualService(invoiceWork
								.getActualService() + actualService);
						invoiceWork.setServiceRevenue(invoiceWork
								.getServiceRevenue() + serviceRevenue);
						invoiceWork.setTotalNotReadyTime(invoiceWork
								.getTotalNotReadyTime() + totalNotReadyTime);

						dao.updateActualService(invoiceWork, null);
						dao.flush();

						// update all the agent state detail with the invoice
						// work
						// associate

						// set all the invoice work to the phone system
						for (AgentStateDetail as : listAgentStateDetailUpdate) {
							as.setInvoiceWork(invoiceWork);
						}

						// agentStateDetailDAO.update(listAgentStateDetailUpdate,
						// invoiceWork);
						// agentStateDetailDAO.flush();
						//
						// // insert all the duration pending
						// agentStateDetailDAO
						// .updatePendingDuration(listAgentStateDetailInsertPending);
						// agentStateDetailDAO.flush();

					}
				}

				//

			}

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

	@Override
	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','INVOICES_M')")
	public ProblemInvoice updateInvoice(Invoice invoice,
			ProblemInvoice problemInvoice, User user) {

		// update the invoice in the invoice work
		dao.updateInvoice(invoice, problemInvoice.getId(), user);

		problemInvoice.setFieldAdicional(" Invoice assigned: "
				+ invoice.toString());

		return problemInvoice;
	}

}