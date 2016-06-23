package com.ghw.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

import com.ghw.datamodel.InvoiceDataModel;
import com.ghw.filter.InvoiceFilter;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceWork;
import com.ghw.model.ProblemInvoice;
import com.ghw.report.service.OracleService;
import com.ghw.service.EmailService;
import com.ghw.service.InvoiceService;
import com.ghw.service.ProblemInvoiceService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class InvoiceController extends
		Controller<Invoice, String, InvoiceService> {

	@ManagedProperty(value = "#{invoiceDataModel}")
	private InvoiceDataModel lazyModel;

	@ManagedProperty(value = "#{invoiceService}")
	private InvoiceService service;

	@ManagedProperty(value = "#{emailService}")
	private EmailService emailService;

	@ManagedProperty(value = "#{invoiceFilter}")
	private InvoiceFilter filter;

	@ManagedProperty(value = "#{invoiceUtil}")
	public InvoiceUtil invoiceUtil;

	@ManagedProperty(value = "#{oracleService}")
	private OracleService oracleService;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	@ManagedProperty(value = "#{problemInvoiceService}")
	private ProblemInvoiceService problemInvoiceService;

	// list invoices selected in screen invoices by checkbox
	private List<Invoice> selectedInvoices;

	// list invoices with issues
	List<ProblemInvoice> problemInvoices = new ArrayList<ProblemInvoice>();

	private List<Invoice> listDetail = new ArrayList<Invoice>();
	private Integer position = 0;

	private boolean showSendFinance = false;

	private boolean showSaveInvoice = true;

	private boolean disabledBtnSubmit = true;

	private String messageInvoices, messageInsertLog;

	private List<Invoice> invoices = new ArrayList<Invoice>();

	// totals
	private Double loggedTime, totalNotReadyHours, totalReadyHours,
			actualService, importTotal, hoursAdd;

	private DecimalFormat myFormatter = new DecimalFormat(".##");

	@PostConstruct
	public void init() {
		try {

			String request = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestServletPath();

			if (request != null && request.contains("invoices_history")) {

				Context.setSessionAttribute("idReportTypeInvoice", "2");
			}

			// The Send to Finance button only show Tuesday (00:00 to 11:59 all
			// day)
			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
				showSendFinance = true;
			}
			// // first search if is in session
			// if (invoiceUtil != null && invoiceUtil.getInvoiceFilter() !=
			// null) {
			//
			// DataTable dataTable = (DataTable) FacesContext
			// .getCurrentInstance().getViewRoot()
			// .findComponent("form:uniTable");
			//
			// dataTable.setFilters(invoiceUtil.getInvoiceFilter()
			// .getFilters());
			//
			// setFilter(invoiceUtil.getInvoiceFilter());
			//
			// invoiceUtil.setInvoiceFilter(null);
			//
			// }

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

		} finally {

		}
	}

	public void updateBtnSubmit() {
		disabledBtnSubmit = selectedInvoices == null
				|| selectedInvoices.size() == 0;
	}

	public String invoicesHistory() {

		Context.setSessionAttribute("idReportTypeInvoice", "2");

		return "invoices_history";
	}

	public void sentFinance() {
		try {

			messageInvoices = oracleService.createPayables(sessionBean
					.getUser());

			if (StringUtils.isBlank(messageInvoices)) {
				messageInvoices = "There is no invoices to sent to Finance.";
			}
			RequestContext.getCurrentInstance().execute(
					"PF('dlgInvoicesProcessed').show()");

			// FacesMessage msg = new FacesMessage("Information processed", "");
			// FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	public void showSubmitInvoice() {

		int submit = 0;
		int resubmit = 0;
		Double importTotal = 0.0;
		int totalNegativeImport = 0;

		invoices.clear();

		for (Invoice i : selectedInvoices) {
			if (i.getImportTotal() < 0) {
				totalNegativeImport++;
			} else {
				importTotal += i.getImportTotal();
			}

			if (i.getState().getId().equals("2")) {
				submit++;
			} else {
				resubmit++;
			}
			if (i.getThresholds().size() > 0) {
				invoices.add(i);
			}
		}
		// search if any invoice has issues
		problemInvoices = problemInvoiceService
				.getDataByListInvoices(selectedInvoices);
		// assigned the problem with the invoices
		for (ProblemInvoice p : problemInvoices) {
			for (Invoice i : selectedInvoices) {
				if (i.getId().equals(p.getInvId())) {
					i.getProblemInvoices().add(p);
				}
			}
		}

		if (submit > 0 || resubmit > 0) {
			messageInvoices = "You are selected " + submit + " to submit and "
					+ resubmit + " to RESUBMIT. </br>" + "Invoices Issues: "
					+ problemInvoices.size()
					+ "</br> Invoices with thresholds: " + invoices.size();
			messageInsertLog = "Approval process in bulk. Total submitted: "
					+ submit + " Total resubmitted: " + resubmit
					+ " Import total: " + myFormatter.format(importTotal)
					+ " Total with negative import: " + totalNegativeImport
					+ " Total with issues: " + problemInvoices.size()
					+ " Total with thresholds: " + invoices.size();

		} else {
			messageInvoices = "You must to select some invoice to do this action.";
			showSaveInvoice = false;
		}

		if ((invoices != null && invoices.size() > 0)
				|| (problemInvoices != null && problemInvoices.size() > 0)) {
			RequestContext.getCurrentInstance().execute(
					"PF('dlgInvoices').show()");
		} else {
			RequestContext.getCurrentInstance().execute(
					"PF('dlgInvoicesMess').show()");
		}
	}

	/**
	 * Save all the change in dglHours
	 * 
	 * @param event
	 */

	public void approvalInvoices(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			service.approbalInvoice(selectedInvoices, sessionBean.getUser(),
					messageInsertLog);

			selectedInvoices = null;
			// disabledBtnSubmit = true;

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}

	}

	public void approvalInvoice(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			if (entity.getState().getId().equals("2")) {
				service.approbalInvoice(entity, sessionBean.getUser());
			} else if (entity.getState().getId().equals("3")) {
				service.resubmittInvoice(entity, sessionBean.getUser());
			}

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}

	}

	public void approvalInvoice() {

		try {
			// first see if has issues or thresholds
			List<Invoice> list = new ArrayList<Invoice>();
			list.add(entity);

			// search if any invoice has issues
			problemInvoices = problemInvoiceService.getDataByListInvoices(list);
			invoices.clear();

			entity.setProblemInvoices(problemInvoices);

			if (entity.getThresholds() != null
					&& entity.getThresholds().size() > 0) {
				invoices.add(entity);
			}

			if ((invoices != null && invoices.size() > 0)
					|| (problemInvoices != null && problemInvoices.size() > 0)) {
				RequestContext.getCurrentInstance().execute(
						"PF('dlgInvoice').show()");
			} else {
				approvalInvoice(null);
				RequestContext.getCurrentInstance().execute("refreshRow()");
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Load all the invoice data
	 */
	public void loadData() {
		try {

			// save the groups list, used in loadDataNext and loadDataBefore
			listDetail = lazyModel.getDatasource();

			entity = service.loadAllById(entity);

			calculateTotals();

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * search the next group and show the details
	 */
	public void loadDataNext() {
		try {

			// in the groups list get the actual position
			listDetail = lazyModel.getDatasource();
			for (int i = 0; i < listDetail.size(); i++) {
				if (listDetail.get(i).getId().equals(entity.getId())) {
					position = i;
					break;
				}
			}
			// get the next group
			entity = listDetail.get(++position >= listDetail.size() ? 0
					: position);
			// load all the data
			entity = service.loadAllById(entity);

			calculateTotals();

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * search the before group and show the details
	 */
	public void loadDataBefore() {
		try {
			// in the groups list get the actual position
			listDetail = lazyModel.getDatasource();
			for (int i = 0; i < listDetail.size(); i++) {
				if (listDetail.get(i).getId().equals(entity.getId())) {
					position = i;
					break;
				}
			}
			// get the before group
			entity = listDetail.get(--position < 0 ? listDetail.size() - 1
					: position);
			// load all the data
			entity = service.loadAllById(entity);

			calculateTotals();

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
		if (entity != null && entity.getInvoiceWork() != null
				&& entity.getInvoiceWork().size() > 0) {
			for (InvoiceWork iw : entity.getInvoiceWork()) {
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
	 * Save in session the invoice and filter, go to current invoice page
	 * 
	 * @return
	 */
	public String modifyInvoice() {

		invoiceUtil.setInvoice(entity);
		// invoiceUtil.setInvoiceFilter(filter);

		return "edit";
	}

	/**
	 * Save the admin fee modify
	 * 
	 * @param event
	 */
	public void onCellEdit(CellEditEvent event) {

		try {

			Object oldValue = event.getOldValue();
			Object newValue = event.getNewValue();

			if (newValue != null && !newValue.equals(oldValue)) {

				Double oldAdminFee = ((ArrayList<Double>) oldValue).get(0);
				Double newAdminFee = ((ArrayList<Double>) newValue).get(0);

				service.updateAdminFee(lazyModel.getRowData(event.getRowKey()),
						oldAdminFee, newAdminFee, sessionBean.getUser());

				// lazyModel.getRowData(event.getRowKey()).setImportTotal(222.01);
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Admin Fee Changed", "Old: " + oldAdminFee + ", New: "
								+ newAdminFee);
				FacesContext.getCurrentInstance().addMessage(null, msg);

				RequestContext request = RequestContext.getCurrentInstance();
				request.update("form:uniTable");
				request.update("form:msgs");
				request.execute("refreshRow()");
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
		}
	}

	/**
	 * Clear the datatable filter
	 */
	@Override
	public void clearFilter() {

		// super.clearFilter();

		SelectOneMenu selectOne = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbSta");
		if (selectOne != null) {
			selectOne.resetValue();
		}

		cleanDetailFilter();

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').clearFilters()");

	}

	public void cleanDetailFilter() {

		filter.clear();
	}

	/** EDIT NOTE **/
	private boolean editNote = false;

	public void changeToEdit() {
		editNote = true;
	}

	/**
	 * Save the invoices notes
	 */
	public void editNotes() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			service.updateNote(entity, sessionBean.getUser());

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}
	}

	public String getMessageInvoices() {
		return messageInvoices;
	}

	public void setMessageInvoices(String messageInvoices) {
		this.messageInvoices = messageInvoices;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public InvoiceFilter getFilter() {
		return filter;
	}

	public void setFilter(InvoiceFilter filter) {
		this.filter = filter;
	}

	public boolean isEditNote() {
		return editNote;
	}

	public void setEditNote(boolean editNote) {
		this.editNote = editNote;
	}

	public InvoiceDataModel getLazyModel() {
		return lazyModel;
	}

	@Override
	public InvoiceService getService() {
		return service;
	}

	public void setService(InvoiceService service) {
		this.service = service;
	}

	public void setLazyModel(InvoiceDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public List<Invoice> getSelectedInvoices() {
		return selectedInvoices;
	}

	public void setSelectedInvoices(List<Invoice> selectedInvoices) {
		this.selectedInvoices = selectedInvoices;
	}

	public InvoiceUtil getInvoiceUtil() {
		return invoiceUtil;
	}

	public void setInvoiceUtil(InvoiceUtil invoiceUtil) {
		this.invoiceUtil = invoiceUtil;
	}

	public List<Invoice> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<Invoice> listDetail) {
		this.listDetail = listDetail;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
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

	public OracleService getOracleService() {
		return oracleService;
	}

	public void setOracleService(OracleService oracleService) {
		this.oracleService = oracleService;
	}

	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public boolean isShowSendFinance() {
		return showSendFinance;
	}

	public void setShowSendFinance(boolean showSendFinance) {
		this.showSendFinance = showSendFinance;
	}

	public boolean isShowSaveInvoice() {
		return showSaveInvoice;
	}

	public void setShowSaveInvoice(boolean showSaveInvoice) {
		this.showSaveInvoice = showSaveInvoice;
	}

	public boolean isDisabledBtnSubmit() {
		return disabledBtnSubmit;
	}

	public void setDisabledBtnSubmit(boolean disabledBtnSubmit) {
		this.disabledBtnSubmit = disabledBtnSubmit;
	}

	public void validateFilter() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			if (filter.getEndDate() != null && filter.getStartDate() != null
					&& filter.getEndDate().before(filter.getStartDate())) {
				throw new Exception("start_end_date_incorrect");
			}

			if (filter.getEndDate() != null && filter.getStartDate() == null) {
				throw new SystemException("date_end_without_start");
			}

			if (filter.getEndDateSubmitted() != null
					&& filter.getStartDateSubmitted() != null
					&& filter.getEndDateSubmitted().before(
							filter.getStartDateSubmitted())) {
				throw new Exception("submitted_start_end_date_incorrect");
			}

			if (filter.getEndDateSubmitted() != null
					&& filter.getStartDateSubmitted() == null) {
				throw new SystemException("date_end_without_start",
						"form:endDateSubmittedId");
			}

			if (filter.getEndDatePay() != null
					&& filter.getStartDatePay() != null
					&& filter.getEndDatePay().before(filter.getStartDatePay())) {
				throw new Exception("pay_start_end_date_incorrect");
			}

			if (filter.getEndDatePay() != null
					&& filter.getStartDatePay() == null) {
				throw new SystemException("date_end_without_start",
						"form:endDatePayId");
			}

		} catch (SystemException e) {
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			error = true;
		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public List<ProblemInvoice> getProblemInvoices() {
		return problemInvoices;
	}

	public void setProblemInvoices(List<ProblemInvoice> problemInvoices) {
		this.problemInvoices = problemInvoices;
	}

	public ProblemInvoiceService getProblemInvoiceService() {
		return problemInvoiceService;
	}

	public void setProblemInvoiceService(
			ProblemInvoiceService problemInvoiceService) {
		this.problemInvoiceService = problemInvoiceService;
	}

	public String getMessageInsertLog() {
		return messageInsertLog;
	}

	public void setMessageInsertLog(String messageInsertLog) {
		this.messageInsertLog = messageInsertLog;
	}

}