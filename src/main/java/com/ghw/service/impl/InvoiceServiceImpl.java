package com.ghw.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.controller.ApplicationBean;
import com.ghw.dao.AddressDAO;
import com.ghw.dao.AgentStateDetailDAO;
import com.ghw.dao.ConfigurationSystemDAO;
import com.ghw.dao.IncentiveDAO;
import com.ghw.dao.InvoiceDAO;
import com.ghw.dao.InvoiceWorkDAO;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.ProfileDAO;
import com.ghw.filter.FilterBase;
import com.ghw.filter.InvoiceFilter;
import com.ghw.model.ConfigurationSystem;
import com.ghw.model.Incentive;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceState;
import com.ghw.model.InvoiceWork;
import com.ghw.model.LogSystem;
import com.ghw.model.Profile;
import com.ghw.model.Thresholds;
import com.ghw.model.User;
import com.ghw.service.EmailService;
import com.ghw.service.InvoiceService;

@Service("invoiceService")
@Transactional(readOnly = false)
public class InvoiceServiceImpl extends
		ServiceImpl<Invoice, String, InvoiceDAO> implements InvoiceService {

	private InvoiceDAO dao;

	@Autowired
	private ProfileDAO profileDAO;

	// @Autowired
	// private SessionBean sessionBean;

	@Autowired
	private LogSystemDAO logSystemDao;

	@Autowired
	private ConfigurationSystemDAO configurationSystemDAO;

	@Autowired
	private ApplicationBean applicationBean;

	@Autowired
	private InvoiceWorkDAO invoiceWorkDAO;

	@Autowired
	private IncentiveDAO incentiveDAO;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AgentStateDetailDAO agentStateDetailDAO;

	private Date dateStart, dateEnd, datePay;

	@Autowired
	public void setDAO(InvoiceDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	/**
	 * Cancel all the invoices without work and incentive in state Pending
	 */
	@Override
	public void cancelInvoice(Date date) {

		int totalCancel = dao.cancelInvoices(date);
		String detail = "Canceled invoices with start date less than "
				+ (new SimpleDateFormat("E, M-d-yy")).format(date)
				+ " Total invoices: " + totalCancel;

		LogSystem logSystem = new LogSystem("Invoice", "cancelInvoice", detail,
				null, false, null, null);
		logSystemDao.makePersistent(logSystem);
	}

	/**
	 * Create an invoice when the IBO change state like add to a system,
	 * activate, etc
	 */
	@Override
	public void createInvoiceIbo(Profile profile, User userModif) {

		// the invoice is for the current week
		getDayOfInvoiceCurrentWeek();

		if (!dao.validateIfExists(dateStart, dateEnd, profile)) {

			// get the admin fee
			List<ConfigurationSystem> list = configurationSystemDAO.findAll();
			ConfigurationSystem cs = list != null && list.size() > 0 ? list
					.get(0) : null;
			createInvoice(profile, cs, userModif);
		}
	}

	/**
	 * Create weekly the invoice from the next week
	 */
	@Override
	public void makePersistent() {

		getDayOfInvoice();

		List<Profile> ibos = new ArrayList<Profile>();

		// validate not exits the invoices in that date range
		if (!dao.validateIfExists(dateStart, dateEnd)) {

			// get the admin fee
			List<ConfigurationSystem> list = configurationSystemDAO.findAll();
			ConfigurationSystem cs = list != null && list.size() > 0 ? list
					.get(0) : null;

			// get all the active IBO
			ibos = profileDAO.getDataAllActiveIbo();

			for (Profile profile : ibos) {
				createInvoice(profile, cs, null);
			}

		}
		// insert into log system
		String detail = "Start date: "
				+ (new SimpleDateFormat("E, M-d-yy h:mm a")).format(dateStart)
				+ " End date: "
				+ (new SimpleDateFormat("E, M-d-yy h:mm a")).format(dateEnd)
				+ " Total invoices: " + ibos.size();
		LogSystem logSystem = new LogSystem("Invoice", "makePersistent",
				detail, null, false, null, null);
		logSystemDao.makePersistent(logSystem);

	}

	/**
	 * Get the invoice date
	 */
	private void getDayOfInvoice() {
		// Get the actual date
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		// Search the next sunday, the invoices are between Sunday at 0000
		// hours
		// and Saturday at 2359 hours.
		c.add(Calendar.DATE, 7);
		dateStart = c.getTime();

		// Get the saturday after the sunday
		c.add(Calendar.DATE, 6);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);

		dateEnd = c.getTime();

		// Get the pay day, is next friday
		c.add(Calendar.DATE, 6);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		datePay = c.getTime();

	}

	private void getDayOfInvoiceCurrentWeek() {
		// Get the actual date
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		// Search the next sunday, the invoices are between Sunday at 0000
		// hours
		// and Saturday at 2359 hours.

		dateStart = c.getTime();

		// Get the saturday after the sunday
		c.add(Calendar.DATE, 6);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 0);

		dateEnd = c.getTime();

		// Get the pay day, is next friday
		c.add(Calendar.DATE, 6);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		datePay = c.getTime();

	}

	/**
	 * Get the invoice date
	 */
	// private void getDayOfInvoiceElimina() {
	// // Get the actual date
	// Calendar c = Calendar.getInstance();
	// c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	// c.set(Calendar.HOUR_OF_DAY, 0);
	// c.set(Calendar.MINUTE, 0);
	// c.set(Calendar.SECOND, 0);
	//
	// // Search the next sunday, the invoices are between Sunday at 0000
	// // hours
	// // and Saturday at 2359 hours.
	// // c.add(Calendar.DATE, 7);
	// dateStart = c.getTime();
	//
	// // Get the saturday after the sunday
	// c.add(Calendar.DATE, 6);
	// c.set(Calendar.HOUR_OF_DAY, 23);
	// c.set(Calendar.MINUTE, 59);
	// c.set(Calendar.SECOND, 0);
	//
	// dateEnd = c.getTime();
	//
	// // Get the pay day, is next friday
	// c.add(Calendar.DATE, 6);
	// c.set(Calendar.HOUR_OF_DAY, 0);
	// c.set(Calendar.MINUTE, 0);
	// c.set(Calendar.SECOND, 0);
	//
	// datePay = c.getTime();
	//
	// }

	private void createInvoice(Profile profile, ConfigurationSystem cs,
			User userModif) {
		Invoice invoice = new Invoice();
		invoice.setStart(dateStart);
		invoice.setEnd(dateEnd);
		invoice.setPayDate(datePay);
		invoice.setState(new InvoiceState("1"));
		invoice.setUser(profile);
		invoice.setImportTotal(-cs.getAdminFee());
		invoice.setAdminFee(cs != null ? cs.getAdminFee() : null);
		invoice.setActualService(0.0);
		invoice.setHoursAdded(0.0);
		invoice.setTotalIncentive(0.0);
		invoice.setServiceRevenue(0.0);

		// set the invoice number Inv#02/21-02/27
		String number = "Inv#"
				+ (new SimpleDateFormat("MM/dd")).format(dateStart) + "-"
				+ (new SimpleDateFormat("MM/dd")).format(dateEnd);
		invoice.setNumber(number);

		dao.makePersistent(invoice);

		// search all the invoices work without invoices associate in that date
		// range
		invoiceWorkDAO.updateInvoice(invoice, userModif);
	}

	/**
	 * Change the invoice state to Submit
	 * 
	 * @param invoice
	 * @return
	 */
	@Loggable
	@Override
	public Invoice submitInvoice(Invoice invoice, User userSession) {

		invoice = submitInvoiceBasic(invoice, userSession);
		// update in DB how many times the IBO submit
		profileDAO.updateTotalSumbit(invoice.getUser().getId());
		dao.flush();

		return invoice;
	}

	private Invoice submitInvoiceBasic(Invoice invoice, User userSession) {
		if (invoice.getState() != null && invoice.getState().getId() != null
				&& invoice.getState().getId().equals("1")) {
			// change the invoice's state to Submitted and update the actual
			// date
			dao.changeState(invoice, "2", userSession);
			dao.flush();

			invoice.setFieldAdicional(invoice.getUser().toString());
		}

		return invoice;
	}

	/**
	 * Change the invoice state to Submit to all the invoices Pending
	 * international IBOs
	 */
	@Override
	public void submitInternationalInvoices() {

		int total = dao.submitInternationInvoices();

		String detail = "Submited invoices with start date less than "
				+ (new SimpleDateFormat("E, M-d-yy")).format(new Date())
				+ " Total invoices: " + total;

		LogSystem logSystem = new LogSystem("Invoice",
				"submitInternationalInvoices", detail, null, false, null, null);
		logSystemDao.makePersistent(logSystem);
	}

	/**
	 * Change the invoice state to Cancel
	 * 
	 * @param invoice
	 * @return
	 */
	@Loggable
	@Override
	public Invoice cancelInvoice(Invoice invoice, User userSession) {
		if (invoice.getState() != null && invoice.getState().getId() != null
				&& invoice.getState().getId().equals("1")) {
			// change the invoice's state to Canceled and update the actual date
			dao.changeState(invoice, "4", userSession);
			dao.flush();

			invoice.setFieldAdicional(invoice.getUser().toString());
		}

		return invoice;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	private void processSubmitOrCancelInvoice(Invoice i, User userSession) {
		String detail = "Submitted: ";
		// Submit the invoice if the hours > 0 or import total > 0 or
		// service revenue > 0 or houers added > 0
		if (i.getActualService() > 0 || i.getHoursAdded() > 0
				|| i.getTotalIncentive() > 0 || i.getImportTotal() > 0
				|| i.getServiceRevenue() > 0) {
			submitInvoiceBasic(i, userSession);
		} else {
			cancelInvoice(i, userSession);
			detail = "Canceled: ";
		}

		// insert log system
		LogSystem log = new LogSystem();
		log.setClassName(i.getClass().getSimpleName());
		log.setDetail(detail + i.toString() + i.getFieldAdicional());
		log.setMethod("submitOrCancelInvoice");
		log.setUser(null);
		log.setClassId(i.getIdentity());

		logSystemDao.makePersistent(log);
		logSystemDao.flush();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	@Override
	public void submitOrCancelInvoice(User user, User userSession) {

		// Get the ibo id
		String profileId = profileDAO.getProfileIdByUserId(user.getId());
		List<Invoice> invoices = dao.getDataPendingByIbo(profileId);

		for (Invoice i : invoices) {
			// System.out.println("Invoice ID:" + i.getId());

			processSubmitOrCancelInvoice(i, userSession);

		}

	}

	@Override
	public Long count(FilterBase filter) throws Exception {
		InvoiceFilter invoiceFilter = (InvoiceFilter) filter;
		// if the user is an IBO only get the user's invoices
		User user = invoiceFilter.getUser();
		if (user.getType() != null && user.isAnIbo() && user.getIbo() != null
				&& user.getIbo().getId() != null)
			invoiceFilter.setIboId(user.getIbo().getId());

		return super.count(filter);
	}

	@Override
	public List<Invoice> getData(FilterBase filter) throws Exception {

		InvoiceFilter invoiceFilter = (InvoiceFilter) filter;
		// if the user is an IBO only get the user's invoices
		User user = invoiceFilter.getUser();
		if (user.getType() != null && user.isAnIbo() && user.getIbo() != null
				&& user.getIbo().getId() != null) {
			invoiceFilter.setIboId(user.getIbo().getId());
		}

		// if the type is Invoice need to analize the threshold
		List<Invoice> list = super.getData(filter);

		if (invoiceFilter.getTypeReport() == 1) {

			if (invoiceFilter.getTypeReport() == 1) {
				List<Thresholds> thresholds = applicationBean.getThresholds();

				for (Invoice i : list) {
					Double totalHours = i.getActualService()
							+ i.getHoursAdded();
					for (Thresholds t : thresholds) {
						switch (t.getId()) {
						case "1":// Service Revenue
							if (i.getServiceRevenue() < t.getMin()
									|| i.getServiceRevenue() > t.getMax()) {
								t.setFieldAdicional(i.getServiceRevenue()
										.toString());
								i.getThresholds().put(t.getId(), t);
							}
							break;
						case "2":// Incentive Revenue
							if (i.getTotalIncentive() < t.getMin()
									|| i.getTotalIncentive() > t.getMax()) {
								t.setFieldAdicional(i.getTotalIncentive()
										.toString());
								i.getThresholds().put(t.getId(), t);
							}

							break;

						case "3":// Total Revenue
							if (i.getImportTotal() < t.getMin()
									|| i.getImportTotal() > t.getMax()) {
								t.setFieldAdicional(i.getImportTotal()
										.toString());
								i.getThresholds().put(t.getId(), t);
							}
							break;

						case "4":// Total Hours
							if (totalHours < t.getMin()
									|| totalHours > t.getMax()) {
								t.setFieldAdicional(totalHours.toString());
								i.getThresholds().put(t.getId(), t);
							}
							break;

						case "5":// 0 Hours Qty. with Pay
							if (totalHours <= new Double(0)
									&& i.getImportTotal() > 0) {
								t.setFieldAdicional(i.getImportTotal()
										.toString());
								i.getThresholds().put(t.getId(), t);
							}
							break;
						case "6":// 0 Pay with Hours Qty
							if (totalHours > 0 && i.getImportTotal() < 0) {
								t.setFieldAdicional(totalHours.toString());
								i.getThresholds().put(t.getId(), t);
							}
							break;
						}

					}

				}
			}
		}
		return list;
	}

	@Override
	public Invoice getTotalInvoices(FilterBase filter) throws Exception {
		return dao.getTotalInvoices((InvoiceFilter) filter);
	}

	private Invoice getInvoiceDetail(Invoice invoice) {

		if (invoice != null) {
			invoice.setInvoiceWork(invoiceWorkDAO.getDataByInvoice(invoice));
			invoice.setIncentive(incentiveDAO.getDataByInvoice(invoice));
			invoice.setPhoneSystemDetail(agentStateDetailDAO
					.getDataByInvoice(invoice));
		}

		return invoice;
	}

	@Override
	public Invoice getCurrentInvoice(Profile user) {
		return getInvoiceDetail(dao.getCurrentInvoice(user));
	}

	@Override
	public Invoice getCurrentInvoice(String invoiceId) {
		return getInvoiceDetail(dao.getCurrentInvoice(invoiceId));
	}

	@Override
	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','INVOICES_M')")
	public Invoice updateAdminFee(Invoice invoice, Double oldValue,
			Double newValue, User userSession) {

		invoice.setAdminFee(newValue);

		dao.updateAdminFee(invoice, userSession);

		String fieldAdicional = " User: " + invoice.getUser().toString()
				+ " New Admin fee: " + newValue + "  Old Admin fee: "
				+ oldValue;

		invoice.setFieldAdicional(fieldAdicional);

		return invoice;
	}

	@Override
	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','INVOICES_M')")
	public Invoice updateNote(Invoice invoice, User userSession) {

		dao.updateNotes(invoice, userSession);

		String fieldAdicional = " User: " + invoice.getUser().toString();

		invoice.setFieldAdicional(fieldAdicional);

		return invoice;
	}

	private Invoice invoiceEmail;

	/**
	 * Change the invoice state to Submit
	 * 
	 * @param invoice
	 * @return
	 */
	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','INVOICES_M')")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Invoice approbalInvoice(Invoice invoice, User userSession) {

		approbal(invoice, userSession);

		invoiceEmail = invoice;

		// send email to IBO
		emailService.sendInvoiceEmail(invoiceEmail);

		// // send an email to all IBO
		// Runnable myrunnable = new Runnable() {
		// public void run() {
		// // send email to IBO
		// emailService.sendInvoiceEmail(invoiceEmail);
		// }
		// };
		//
		// new Thread(myrunnable).start();// Call it when you need to run the
		// function

		return invoice;
	}

	private Invoice approbal(Invoice invoice, User userSession) {

		if (invoice.getState() != null && invoice.getState().getId() != null
				&& invoice.getState().getId().equals("2")) {
			// change the invoice's state to Submitted and update the actual
			// date
			dao.changeState(invoice, "3", userSession);

			String threshold = "";
			for (Map.Entry<String, Thresholds> entry : invoice.getThresholds()
					.entrySet()) {
				threshold += entry.getValue().getMetric() + " "
						+ entry.getValue().getFieldAdicional();
			}

			invoice.setFieldAdicional(" "
					+ invoice.getUser().toString()
					+ (StringUtils.isNotBlank(threshold) ? " Thresholds: ["
							+ threshold + "]" : "")
					+ (invoice.getProblemInvoices() != null
							&& invoice.getProblemInvoices().size() > 0 ? " Problems: "
							+ invoice.getProblemInvoices().toString()
							: ""));
		}

		return invoice;

	}

	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','INVOICES_M')")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Invoice resubmittInvoice(Invoice invoice, User userSession) {

		if (invoice.getState() != null && invoice.getState().getId() != null
				&& invoice.getState().getId().equals("3")) {

			dao.resubmitInvoice(invoice, userSession);

			String threshold = "";
			for (Map.Entry<String, Thresholds> entry : invoice.getThresholds()
					.entrySet()) {
				threshold += entry.getValue().getMetric() + " "
						+ entry.getValue().getFieldAdicional();
			}

			invoice.setFieldAdicional(" "
					+ invoice.getUser().toString()
					+ (StringUtils.isNotBlank(threshold) ? " Thresholds: ["
							+ threshold + "]" : "")
					+ (invoice.getProblemInvoices() != null
							&& invoice.getProblemInvoices().size() > 0 ? " Problems: "
							+ invoice.getProblemInvoices().toString()
							: ""));
		}

		return invoice;
	}

	@Autowired
	private AddressDAO addressDAO;

	@Override
	@Transactional(readOnly = true)
	public Invoice loadAllById(Invoice entity) throws Exception {
		Invoice cl = dao.loadAllById(entity);

		// get IBO address
		if (cl.getUser().getCorporation() != null
				&& StringUtils
						.isNotBlank(cl.getUser().getCorporation().getId())) {
			cl.getUser()
					.getCorporation()
					.setAddress(
							addressDAO.getDataByCorporation(cl.getUser()
									.getCorporation()));
		}
		// get invoice work
		cl.setInvoiceWork(invoiceWorkDAO.getDataByInvoice(cl));

		// get invoice incentive
		cl.setIncentive(incentiveDAO.getDataByInvoice(cl));

		cl.setPhoneSystemDetail(agentStateDetailDAO.getDataByInvoice(cl));

		return cl;
	}

	@Override
	public List<Invoice> getDataActiveOracle() {
		List<Invoice> invoices = dao.getDataActiveOracle();

		// get all the invoice work
		List<InvoiceWork> invoiceWorks = invoiceWorkDAO
				.getDataByInvoice(invoices);

		// get all the incentive
		List<Incentive> incentives = incentiveDAO.getDataByInvoice(invoices);

		for (Invoice invoice : invoices) {
			for (InvoiceWork invoiceWork : invoiceWorks) {
				if (invoiceWork.getId().equals(invoice.getId()))
					invoice.getInvoiceWork().add(invoiceWork);
			}
			for (Incentive incentive : incentives) {
				if (incentive.getInvoice().getId().equals(invoice.getId()))
					invoice.getIncentive().add(incentive);
			}
		}

		// for (Invoice cl : invoices) {
		// // get invoice work
		// cl.setInvoiceWork(invoiceWorkDAO.getDataByInvoice(cl));
		//
		// // get invoice incentive
		// cl.setIncentive(incentiveDAO.getDataByInvoice(cl));
		//
		// }

		return invoices;
	}

	private List<Invoice> listInvoice = new ArrayList<Invoice>();

	/**
	 * Change the invoice state to Submit
	 * 
	 * @param invoice
	 * @return
	 */
	@PreAuthorize("hasAnyRole('SUPER','INVOICES_M')")
	@Override
	public void approbalInvoice(List<Invoice> invoice, User userSession,
			String message) throws Exception {

		listInvoice = new ArrayList<Invoice>();

		// add to log general
		LogSystem log = new LogSystem();
		log.setClassName("Invoice");
		log.setDetail(message);
		log.setMethod("approbalResubmitInvoice");
		log.setUser(userSession);
		log.setIp(userSession != null ? userSession.getIp() : null);
		logSystemDao.makePersistent(log);

		for (Invoice i : invoice) {

			String action = "";
			if (i.getState().getId().equals("2")) {
				approbal(i, userSession);
				action = "approvalInvoice";
				listInvoice.add(i);
			} else if (i.getState().getId().equals("3")) {
				resubmittInvoice(i, userSession);
				action = "resubmitInvoice";
			}

			// add to log
			log = new LogSystem();
			log.setClassName("Invoice");
			log.setDetail(i.toString() + i.getFieldAdicional());
			log.setMethod(action);

			log.setUser(userSession);
			log.setIp(userSession != null ? userSession.getIp() : null);
			log.setClassId(i.getId());
			logSystemDao.makePersistent(log);

		}

		emailService.sendInvoiceEmail(listInvoice);

	}

	@Override
	public void makePersistent(Date ini, Date fin) {
		makePersistentPrueba(ini);

	}

	public Invoice makePersistentPrueba(Date dateIni) {
		Calendar c = new GregorianCalendar();
		c.setTime(dateIni);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		dateStart = c.getTime();

		// Get the saturday after the sunday
		c.add(Calendar.DATE, 6);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);

		dateEnd = c.getTime();

		// Get the pay day, is next friday
		c.add(Calendar.DATE, 6);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		datePay = c.getTime();
		// llamat al metodo makePersistent

		List<Profile> ibos = new ArrayList<Profile>();

		List<ConfigurationSystem> list = configurationSystemDAO.findAll();
		ConfigurationSystem cs = list != null && list.size() > 0 ? list.get(0)
				: null;

		createInvoice(new Profile("e5da0cae-4df5-44d2-b70a-5a697f393f4e"), cs,
				null);

		// validate not exits the invoices in that date range
		// if (!dao.validateIfExists(dateStart, dateEnd)) {

		// get the admin fee
		// List<ConfigurationSystem> list = configurationSystemDAO.findAll();
		// ConfigurationSystem cs = list != null && list.size() > 0 ? list
		// .get(0) : null;

		// get all the active IBO
		// ibos = profileDAO.getDataAllActiveIbo();

		// for (Profile profile : ibos) {
		// createInvoice(profile, cs, null);
		// }

		// }
		// insert into log system
		// String detail = "Start date: "
		// + (new SimpleDateFormat("E, M-d-yy h:mm a")).format(dateStart)
		// + " End date: "
		// + (new SimpleDateFormat("E, M-d-yy h:mm a")).format(dateEnd)
		// + " Total invoices: " + ibos.size();
		// LogSystem logSystem = new LogSystem("Invoice", "makePersistent",
		// detail, sessionBean.getUser().getIp(), false, null,
		// sessionBean.getUser());
		// logSystemDao.makePersistent(logSystem);

		return null;
	}

	@Override
	public List<Invoice> getDataPendingOrSubmitted(String userId,
			Date dateInvoiceWork) {
		return dao.getDataPendingOrSubmitted(userId, dateInvoiceWork);
	}
}