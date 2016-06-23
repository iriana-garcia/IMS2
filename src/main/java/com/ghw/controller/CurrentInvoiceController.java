package com.ghw.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;

import com.ghw.model.Category;
import com.ghw.model.Incentive;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceHoursAdded;
import com.ghw.model.InvoiceWork;
import com.ghw.model.Profile;
import com.ghw.model.Skill;
import com.ghw.model.User;
import com.ghw.service.IncentiveService;
import com.ghw.service.InvoiceHoursAddedService;
import com.ghw.service.InvoiceService;
import com.ghw.service.MessageBoardService;
import com.ghw.service.ProfileService;
import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class CurrentInvoiceController implements Serializable {

	@ManagedProperty(value = "#{invoiceService}")
	private InvoiceService service;

	@ManagedProperty(value = "#{profileService}")
	private ProfileService profileService;

	@ManagedProperty(value = "#{invoiceHoursAddedService}")
	private InvoiceHoursAddedService invoiceHoursAddedService;

	@ManagedProperty(value = "#{incentiveService}")
	private IncentiveService incentiveService;

	@ManagedProperty(value = "#{messageBoardService}")
	private MessageBoardService messageBoardService;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	@ManagedProperty(value = "#{invoiceUtil}")
	public InvoiceUtil invoiceUtil;

	@ManagedProperty(value = "#{applicationBean}")
	private ApplicationBean applicationBean;

	private Invoice invoice;

	private Invoice invoiceSaved;

	private String message;

	private Profile user;

	private InvoiceWork invoiceWork;

	// totals
	private Double loggedTime, totalNotReadyHours, totalReadyHours,
			actualService, importTotal, hoursAdd;

	// // used to populated the cmbSkill to add incentive
	// private List<Skill> listSkill;
	// @ManagedProperty(value = "#{skillService}")
	// private SkillService skillService;

	// safe if exists any change without save
	private boolean changeWithoutSave = false;

	private boolean disabledSubmit = false;

	private boolean showUser = true;

	private boolean showButtonIbo = true;

	private String back = null;

	// for save value cmbCategory - Add hours
	private Category category;
	// for save value hours added
	private InvoiceHoursAdded newHours = new InvoiceHoursAdded();
	// for save the list hours added
	private List<InvoiceHoursAdded> hours = new ArrayList<InvoiceHoursAdded>();

	private Incentive incentive;

	private Skill skill;

	@PostConstruct
	public void init() {
		try {

			// first search if is in session
			if (invoice == null && invoiceUtil != null
					&& invoiceUtil.getInvoice() != null
					&& StringUtils.isNotBlank(invoiceUtil.getInvoice().getId())) {

				processCurrentInvoice(invoiceUtil.getInvoice());

				invoiceUtil.setInvoice(null);
				showUser = false;
			}

			processDisabledSubmit();

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

		} finally {

		}
	}

	/**
	 * Analize if the Submit button need to be disabled
	 */
	private void processDisabledSubmit() {

		disabledSubmit = false;

		Calendar start = new GregorianCalendar();
		start.setTime(applicationBean.getSystemConf().getStart());

		Calendar end = new GregorianCalendar();
		end.setTime(applicationBean.getSystemConf().getEnd());

//		System.out.println((new SimpleDateFormat("E, M-d-yy h:mm a"))
//				.format(start.getTime()));
//
//		System.out.println((new SimpleDateFormat("E, M-d-yy h:mm a"))
//				.format(end.getTime()));

		// the submit button is disabled between Sunday 5 pm to Wednesday
		// 11:59
		// pm
		// Get the actual sunday
		Calendar iniEna = new GregorianCalendar();
		iniEna.setTime(new Date());
		iniEna.set(Calendar.DAY_OF_WEEK, start.get(Calendar.DAY_OF_WEEK));
		iniEna.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
		iniEna.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
		iniEna.set(Calendar.SECOND, 0);

//		System.out.println((new SimpleDateFormat("E, M-d-yy h:mm a"))
//				.format(iniEna.getTime()));

		// get the actual wednesday
		Calendar endEna = new GregorianCalendar();
		endEna.setTime(new Date());
		endEna.set(Calendar.DAY_OF_WEEK, end.get(Calendar.DAY_OF_WEEK));
		endEna.set(Calendar.HOUR_OF_DAY, end.get(Calendar.HOUR_OF_DAY));
		endEna.set(Calendar.MINUTE, end.get(Calendar.MINUTE));
		endEna.set(Calendar.SECOND, 0);

		Date actual = new Date();

		// System.out.println((new SimpleDateFormat("E, M-d-yy h:mm a"))
		// .format(endEna.getTime()));

		// analize if the invoice can be submitted
		// if state is pending and date NOT is between Sunday after 5 pm to
		// Wednesday
		if (!(actual.after(iniEna.getTime()) && actual.before(endEna.getTime()))) {
			disabledSubmit = true;
		} else if (sessionBean.getUser() != null
				&& sessionBean.getUser().getType() != null
				&& sessionBean.getUser().isAnIbo()
				&& sessionBean.getUser().getIbo() != null
				&& sessionBean.getUser().getIbo().getTotalSubmit() >= applicationBean
						.getSystemConf().getTotalSubmitInvoice()) {
			disabledSubmit = true;
		}

		if (!disabledSubmit
				&& (invoice == null || invoice.getId() == null
						|| invoice.getState() == null
						|| invoice.getState().getId() == null || !invoice
						.getState().getId().equals("1"))) {
			disabledSubmit = true;
		} else if (!disabledSubmit && invoice != null
				&& invoice.getId() != null
				&& invoice.getStart().after(new Date())) {
			disabledSubmit = true;
		}

	}

	public boolean isShowButtonIbo() {

		Invoice i = getInvoice();
		User user = sessionBean.getUser();

		showButtonIbo = user.isAnIbo() && i != null && i.getUser() != null
				&& i.getUser().getId().equals(user.getIbo().getId());

		return showButtonIbo;
	}

	public void setShowButtonIbo(boolean showButtonIbo) {
		this.showButtonIbo = showButtonIbo;
	}

	public void onCellEdit(CellEditEvent event) {

		try {

			Object oldValue = event.getOldValue();
			Object newValue = event.getNewValue();

			if (newValue != null && !newValue.equals(oldValue)) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Cell Changed", "Old: " + oldValue + ", New:"
								+ newValue);
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * calculate the invoice's totals
	 */
	private void calculateTotals() {

		// calculate the total
		loggedTime = 0.0;
		totalNotReadyHours = 0.0;
		totalReadyHours = 0.0;
		actualService = 0.0;
		importTotal = 0.0;
		hoursAdd = 0.0;
		if (invoice != null && invoice.getInvoiceWork() != null
				&& invoice.getInvoiceWork().size() > 0) {
			for (InvoiceWork iw : invoice.getInvoiceWork()) {
				loggedTime += iw.getLoggedTime();
				totalNotReadyHours += iw.getTotalNotReadyHours();
				totalReadyHours += iw.getTotalReadyHours();
				actualService += iw.getActualService();
				importTotal += iw.getImportTotal();
				hoursAdd += iw.getHoursAdded();
			}
		}
	}

	/**
	 * Get the invoice's detail when cmbUser change
	 * 
	 * @param user
	 */
	private void processCurrentInvoice(Profile user) {

		// listSkill = null;
		message = "";

		invoice = service.getCurrentInvoice(user);

		// if an IBO does not have invoice show a messa
		if (invoice == null) {
			message = Context.getMSGText("record_not_found");
			invoice = new Invoice();
		}
		// message = invoice.getInvoiceWork().isEmpty() ? Context
		// .getMSGText("must_submit_invoice") : "";

		calculateTotals();

		processDisabledSubmit();

	}

	/**
	 * Get the invoice's detail
	 * 
	 * @param invoice
	 */
	private void processCurrentInvoice(Invoice invoice) {

		// listSkill = null;

		this.invoice = service.getCurrentInvoice(invoice.getId());

		calculateTotals();

		setUser(this.invoice.getUser());

		setShowUser(false);

		back = "invoices";

	}

	/**
	 * Submit the invoice from IBO
	 */
	public void submitInvoice() {

		try {

			service.submitInvoice(invoice, sessionBean.getUser());
			setInvoice(null);

			// update User in session
			int cant = sessionBean.getUser().getIbo().getTotalSubmit();
			sessionBean.getUser().getIbo().setTotalSubmit(++cant);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	private String from, to, subject, content, bcc;

	public void updateFormEmail() {

		try {

			MessageBoardController mc = (MessageBoardController) Context
					.getBean("messageBoardController");

			// showAddMessage is true if is and IBO and does not have message
			// mc.setShowAddMessage(isShowButtonIbo()
			// && invoice.getHaveQuestion() == null);
			mc.setMessageBoards(null);
			// mc.setMessageBoards(messageBoardService.getDataByInvoice(invoice));
			mc.setInvoice(invoice);

			// see if can be show the button add message
			// if user logged is an ibo and does not have message pending to
			// reply show button
			// if (invoice.isEditInvoice() && showButtonIbo
			// && !invoice.isHavePendingReply())
			// mc.setShowButtonAddMessage(true);
			// else if (invoice.isEditInvoice()
			// && !sessionBean.getUser().isAnIbo()
			// && invoice.isHavePendingReply())
			// mc.setShowButtonAddMessage(true);

			// String[] emails = profileService.getEmailIboAndGroup(invoice
			// .getUser().getId());
			// // to: is the email user associate to the IBO's group
			// from = emails[0];
			// to = emails[1];
			// subject = "Invoice issue";
			// content = "Invoice Number: " + invoice.getNumber()
			// + " <br/> Date Start: " + invoice.getStartDateFormat()
			// + " Date End: " + invoice.getEndDateFormat();

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Cancel all the invoice change is call from confirmation panel when yo
	 * made a change and dont save it
	 * 
	 * @param event
	 */
	public void cancelChange(ActionEvent event) {
		changeWithoutSave = false;
		invoiceSaved = null;

	}

	/**
	 * Cancel all the invoice change (hours ans incentive)
	 * 
	 * @return
	 */
	public String cancelChangeGeneral() {
		changeWithoutSave = false;
		invoiceSaved = null;
		setInvoice(null);

		return back;
	}

	/**
	 * Save all the invoice change (hours and incentive)
	 * 
	 * @return
	 */
	public String saveChangeGeneral() {
		changeWithoutSave = false;
		invoiceSaved = null;
		setInvoice(null);

		return back;
	}

	/**
	 * Save all the invoice change is call from confirmation panel when yo made
	 * a change and dont save it
	 */
	public void saveChange(ActionEvent event) {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			changeWithoutSave = false;
			invoiceSaved = null;

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}
	}

	private Category categoryNew;

	public void needCreateCategory() {
		if (category.equals(categoryNew)) {
			RequestContext.getCurrentInstance().execute(
					"PF('dlgCategory').show()");
		}

	}

	// save is the incentive is added or edited
	private boolean insertedIncentive = true;

	public boolean isInsertedIncentive() {
		return insertedIncentive;
	}

	public void setInsertedIncentive(boolean insertedIncentive) {
		this.insertedIncentive = insertedIncentive;
	}

	public void addIncentive(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			incentive.setSkill(skill);
			incentive.setInvoice(invoice);

			if (insertedIncentive) {
				if (incentive.getAmount() <= 0)
					throw new Exception("amount_required");
				incentive.setDate(invoice.getStart());
				invoice.getIncentive().add(incentive);
				invoice.setImportTotal(invoice.getImportTotal()
						+ incentive.getAmount());
				incentiveService.makePersistent(incentive);
			} else {
				invoice.recalculateImport();
				incentiveService.merge(incentive);
			}

			setIncentive(null);
			setSkill(null);
			changeWithoutSave = true;

		} catch (MailSendException e) {
			error = true;
			Context.addErrorMessageFromMSG("mail_send_exception");

		} catch (MailAuthenticationException e) {
			error = true;
			Context.addErrorMessageFromMSG("mail_authentication_exception");

		} catch (MailException e) {
			error = true;
			Context.addErrorMessageFromMSG("mail_exception");

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}

	}

	/**
	 * Methos call when cancel dialog incentive and add incentive
	 */
	public void cleanIncentive() {
		insertedIncentive = true;
		setIncentive(null);
		setSkill(null);

	}

	/**
	 * Method calles by cmbIbo when the user change it, charge the invoice
	 */
	public void onUserChange() {
		try {
			// if (changeWithoutSave) {
			// invoiceSaved = invoice;
			// RequestContext.getCurrentInstance().execute(
			// "PF('dlgConfirmationSave').show()");
			// }

			setInvoice(null);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	public void showHoursAdded() {
		try {

			RequestContext context = RequestContext.getCurrentInstance();

			context.execute("PF('dlgHoursDetail').show()");

			// update dlgHoursDetailId
			context.update("dlgHoursDetailId");

		} catch (Exception e) {

			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Used to initialize invoices hours in the dialog
	 */

	public void chargeListHours() {
		try {

			hours = new ArrayList<InvoiceHoursAdded>();

			for (InvoiceHoursAdded i : invoiceWork.getHoursAddeds()) {
				i.calculate();
				hours.add(i.clone());
			}

			setNewHours(new InvoiceHoursAdded());

		} catch (Exception e) {

			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Add hour to the hours list in dlgHours, initialize category and newHours
	 */
	public void addHours(ActionEvent event) {

		try {

			if (newHours.getHour() <= 0 && newHours.getMinutes() <= 0) {
				throw new Exception("hours_required");
			}

			newHours.setCategory(category);

			Double hoursRound = MathUtils.round(newHours.getHour()
					+ (new Double(newHours.getMinutes()) / 60), 2);

			newHours.setHours(hoursRound);

			newHours.calculate();

			hours.add(newHours);

			setNewHours(new InvoiceHoursAdded());

		} catch (Exception e) {

			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	// public void onRowEdit(RowEditEvent event) {
	//
	// try {
	//
	// InvoiceHoursAdded entity = (InvoiceHoursAdded) event.getObject();
	//
	// entity.setHours(newHours.getHour()
	// + (new Double(newHours.getMinutes()) / 60));
	//
	// entity.calculate();
	//
	// } catch (Exception e) {
	// Context.addErrorMessageFromMSG(e.getMessage());
	//
	// FacesContext.getCurrentInstance().validationFailed();
	// }
	//
	// }

	/**
	 * Save all the change in dglHours
	 * 
	 * @param event
	 */
	public void saveHoursAdded(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			List<InvoiceHoursAdded> listBefore = null;
			for (InvoiceWork iw : invoice.getInvoiceWork()) {
				if (iw.equals(invoiceWork)) {
					listBefore = iw.getHoursAddeds();
					iw.setHoursAddeds(hours);
					break;
				}
			}

			changeWithoutSave = true;
			invoice.recalculateImport();
			calculateTotals();

			// save the change
			invoiceHoursAddedService.saverOrUpdate(hours, listBefore, invoice,
					invoiceWork);

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}

	}

	public InvoiceService getService() {
		return service;
	}

	public void setService(InvoiceService service) {
		this.service = service;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public InvoiceUtil getInvoiceUtil() {
		return invoiceUtil;
	}

	public void setInvoiceUtil(InvoiceUtil invoiceUtil) {
		this.invoiceUtil = invoiceUtil;
	}

	public Invoice getInvoice() {
		if (invoice == null) {
			try {
				// Get the current invoice of the user if type = IBO
				// IF is not an IBO wait for the selected user
				if (user == null) {
					User userSession = sessionBean.getUser();
					if (userSession.getType() != null
							&& StringUtils.isNotBlank(userSession.getType()
									.getId())
							&& userSession.getType().getId().equals("2")) {

						user = userSession.getIbo();
						user.setUser(sessionBean.getUser());
					}

				}

				if (user != null) {
					processCurrentInvoice(user);
				}

			} catch (Exception e) {
				Context.addErrorMessageFromMSG(e.getMessage());
			}
		}
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	public Profile getUser() {
		return user;
	}

	public void setUser(Profile user) {
		this.user = user;
	}

	public boolean isDisabledSubmit() {
		return disabledSubmit;
	}

	public void setDisabledSubmit(boolean disabledSubmit) {
		this.disabledSubmit = disabledSubmit;
	}

	public Double getLoggedTime() {
		return loggedTime;
	}

	public void setLoggedTime(Double loggedTime) {
		this.loggedTime = loggedTime;
	}

	public Double getTotalNotReadyHours() {
		return totalNotReadyHours;
	}

	public void setTotalNotReadyHours(Double totalNotReadyHours) {
		this.totalNotReadyHours = totalNotReadyHours;
	}

	public Double getTotalReadyHours() {
		return totalReadyHours;
	}

	public void setTotalReadyHours(Double totalReadyHours) {
		this.totalReadyHours = totalReadyHours;
	}

	public Double getActualService() {
		return actualService;
	}

	public void setActualService(Double actualService) {
		this.actualService = actualService;
	}

	public Double getImportTotal() {
		return importTotal;
	}

	public void setImportTotal(Double importTotal) {
		this.importTotal = importTotal;
	}

	public Double getHoursAdd() {
		return hoursAdd;
	}

	public void setHoursAdd(Double hoursAdd) {
		this.hoursAdd = hoursAdd;
	}

	public Incentive getIncentive() {
		if (incentive == null) {
			incentive = new Incentive();
		}
		return incentive;
	}

	public void setIncentive(Incentive incentive) {
		this.incentive = incentive;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	// public SkillService getSkillService() {
	// return skillService;
	// }
	//
	// public void setSkillService(SkillService skillService) {
	// this.skillService = skillService;
	// }
	//
	// public List<Skill> getListSkill() {
	// if (listSkill == null) {
	// listSkill = skillService.getDataByInvoice(invoice);
	// }
	// return listSkill;
	// }
	//
	// public void setListSkill(List<Skill> listSkill) {
	// this.listSkill = listSkill;
	// }

	public boolean isChangeWithoutSave() {
		return changeWithoutSave;
	}

	public void setChangeWithoutSave(boolean changeWithoutSave) {
		this.changeWithoutSave = changeWithoutSave;
	}

	public String getBack() {
		return back;
	}

	public void setBack(String back) {
		this.back = back;
	}

	public boolean isShowUser() {
		return showUser;
	}

	public void setShowUser(boolean showUser) {
		this.showUser = showUser;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	/**************** ELIMINAAAAAAAAAAAAAAAA ***********/
	// esto es para eliminar
	private Date dateIni, dateFin;

	public Date getDateIni() {
		return dateIni;
	}

	public void setDateIni(Date dateIni) {
		this.dateIni = dateIni;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public void createInvoice() {

		try {

			service.makePersistent(dateIni, dateFin);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	public Invoice getInvoiceSaved() {
		return invoiceSaved;
	}

	public void setInvoiceSaved(Invoice invoiceSaved) {
		this.invoiceSaved = invoiceSaved;
	}

	public InvoiceWork getInvoiceWork() {
		return invoiceWork;
	}

	public void setInvoiceWork(InvoiceWork invoiceWork) {
		this.invoiceWork = invoiceWork;
	}

	public InvoiceHoursAdded getNewHours() {
		return newHours;
	}

	public void setNewHours(InvoiceHoursAdded newHours) {
		this.newHours = newHours;
	}

	public List<InvoiceHoursAdded> getHours() {
		return hours;
	}

	public void setHours(List<InvoiceHoursAdded> hours) {
		this.hours = hours;
	}

	public InvoiceHoursAddedService getInvoiceHoursAddedService() {
		return invoiceHoursAddedService;
	}

	public void setInvoiceHoursAddedService(
			InvoiceHoursAddedService invoiceHoursAddedService) {
		this.invoiceHoursAddedService = invoiceHoursAddedService;
	}

	public Category getCategoryNew() {
		return categoryNew;
	}

	public void setCategoryNew(Category categoryNew) {
		this.categoryNew = categoryNew;
	}

	public IncentiveService getIncentiveService() {
		return incentiveService;
	}

	public void setIncentiveService(IncentiveService incentiveService) {
		this.incentiveService = incentiveService;
	}

	public MessageBoardService getMessageBoardService() {
		return messageBoardService;
	}

	public void setMessageBoardService(MessageBoardService messageBoardService) {
		this.messageBoardService = messageBoardService;
	}

}
