package com.ghw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;

import com.ghw.datamodel.ProblemInvoiceDataModel;
import com.ghw.model.ClientApplication;
import com.ghw.model.Invoice;
import com.ghw.model.ProblemId;
import com.ghw.model.ProblemInvoice;
import com.ghw.model.User;
import com.ghw.service.ClientApplicationService;
import com.ghw.service.InvoiceService;
import com.ghw.service.InvoiceWorkService;
import com.ghw.service.ProblemInvoiceService;
import com.ghw.service.UserService;
import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class ProblemInvoiceController extends
		Controller<ProblemInvoice, String, ProblemInvoiceService> {

	@ManagedProperty(value = "#{problemInvoiceDataModel}")
	private ProblemInvoiceDataModel lazyModel;

	private ProblemInvoice entity;

	@ManagedProperty(value = "#{objectUtil}")
	protected ObjectUtil objectUtil;

	@ManagedProperty(value = "#{clientApplicationService}")
	private ClientApplicationService clientApplicationService;

	// The option rate can be User or Program
	private String optionRate = "Program";

	// The option rate can be User or Program
	private String optionEventNoProgram = "Edit";
	private ClientApplication program;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	@ManagedProperty(value = "#{invoiceService}")
	private InvoiceService invoiceService;

	@ManagedProperty(value = "#{invoiceWorkService}")
	private InvoiceWorkService invoiceWorkService;

	// The option rate can be User or Program
	private String optionInvoice;
	private List<String[]> actionWorkWithoutInvoice = new ArrayList<String[]>();
	private Invoice invoice;
	private List<Invoice> invoices = new ArrayList<Invoice>();

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	// @PostConstruct
	// public void init() {
	// try {
	//
	// } catch (Exception e) {
	// Context.addErrorMessageFromMSG(e.getMessage());
	//
	// } finally {
	// }
	//
	// }

	public SelectItem[] getProblemList() {
		ProblemId[] listProblem = ProblemId.values();
		SelectItem[] problems = new SelectItem[listProblem.length];
		for (int i = 0; i < listProblem.length; i++) {

			SelectItem select = new SelectItem(ProblemId.valueOf(listProblem[i]
					.name()), ProblemId.valueOf(listProblem[i].name())
					.getValor());
			problems[i] = select;
		}
		return problems;
	}

	public String fixProblem() {
		try {

			actionWorkWithoutInvoice = new ArrayList<String[]>();

			if (entity != null) {

				// UserDetails loggedInUser = (UserDetails)
				// SecurityContextHolder
				// .getContext().getAuthentication().getPrincipal();

				switch (entity.getProblemId()) {
				case "A":// Event is not associate to a Program

					if (!SessionBean.hasAnyRol("ROLE_SUPER", "ROLE_CA_A",
							"ROLE_CA_M")) {
						throw new Exception("access_denied");
					}

					RequestContext.getCurrentInstance().execute(
							"PF('dlgEventNoProgram').show()");

					return null;

				case "B":// No Rate
					if (!SessionBean.hasAnyRol("ROLE_SUPER", "ROLE_USER_M",
							"ROLE_CA_M")) {
						throw new Exception("access_denied");
					}

					RequestContext.getCurrentInstance().execute(
							"PF('dlgRateCero').show()");
					return null;

				case "C":// Program not associate to IBO

					if (!SessionBean.hasAnyRol("ROLE_SUPER", "ROLE_USER_M")) {
						throw new Exception("access_denied");
					}

					objectUtil.setEdit(2);
					objectUtil.setObject(new User(entity.getUserId()));
					objectUtil.setAction("invoices_problem");
					return "user_edit";

				case "D":// Work not associate to invoice

					if (!SessionBean.hasAnyRol("ROLE_SUPER", "ROLE_INVOICES_M")) {
						throw new Exception("access_denied");
					}

					// if the user is inactive show the action to active the IBO
					// if (!SessionBean.hasAnyRol("ROLE_SUPER", "ROLE_USER_C",
					// "ROLE_INVOICES_M"))
					// throw new Exception("access_denied");

					// if (SessionBean.hasAnyRol("ROLE_SUPER", "ROLE_USER_C")) {
					// User user = userService.getById(entity.getUserId());
					// if (!user.isState()) {
					// actionWorkWithoutInvoice.add(new String[] {
					// "ActiveIBO", "Active IBO" });
					// entity.setUser(user);
					// optionInvoice = "Active IBO";
					// }
					// }

					if (SessionBean.hasAnyRol("ROLE_SUPER", "ROLE_INVOICES_M")) {
						// or can asspociate to another invoice
						invoices = invoiceService.getDataPendingOrSubmitted(
								entity.getUserId(), entity.getSchStartTime());
						if (invoices.size() > 0) {
							actionWorkWithoutInvoice.add(new String[] {
									"AddInvoice", "Add to Invoice" });
						}
					}

					if (actionWorkWithoutInvoice.size() == 0) {
						throw new Exception("work_without_invoice_no_action");
					}

					// RequestContext.getCurrentInstance().execute(
					// "refreshFormNoWork()");

					RequestContext.getCurrentInstance().update(
							"frmWorkNoInvoice");

					RequestContext.getCurrentInstance().execute(
							"PF('dlgWorkNoInvoice').show()");

					return null;

				}
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

		return null;
	}

	public String fixWorkWithoutInvoice() {
		try {

			if (StringUtils.isNotBlank(optionInvoice)) {

				if (optionInvoice.equals("ActiveIBO")) {

					userService.changeState(entity.getUser(),
							sessionBean.getUser());

				} else if (optionInvoice.equals("AddInvoice")) {

					invoiceWorkService.updateInvoice(invoice, entity,
							sessionBean.getUser());

				}
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

		return null;
	}

	public String fixRate() {
		try {

			if (StringUtils.isNotBlank(optionRate)) {

				if (optionRate.equals("User")) {
					objectUtil.setEdit(2);
					objectUtil.setObject(new User(entity.getUserId()));
					objectUtil.setAction("invoices_problem");
					return "user_edit";

				} else if (optionRate.equals("Program")) {
					// get the Program Data
					ClientApplication ca = clientApplicationService
							.getById(entity.getCliId());

					objectUtil.setEdit(2);
					objectUtil.setObject(ca);
					objectUtil.setAction("invoices_problem");
					return "program_edit";

				}
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

		return null;
	}

	public String fixEventNoProgram() {
		try {

			if (StringUtils.isNotBlank(optionEventNoProgram)) {

				if (optionEventNoProgram.equals("Add")) {

					if (!SessionBean.hasAnyRol("ROLE_SUPER", "ROLE_CA_A")) {
						throw new Exception("access_denied");
					}

					objectUtil.setEdit(1);
				} else if (optionEventNoProgram.equals("Edit")) {

					if (!SessionBean.hasAnyRol("ROLE_SUPER", "ROLE_CA_M")) {
						throw new Exception("access_denied");
					}
					// get the Program Data
					// ClientApplication ca = clientApplicationService
					// .getById(entity.getCliId());
					objectUtil.setEdit(2);
					objectUtil.setObject(program);

				}
				objectUtil.setIdEvent(entity.getSkillId());
				objectUtil.setAction("invoices_problem");
				return "program_edit";

			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

		return null;
	}

	/**
	 * Clear the datatable filter
	 */

	public void clearFilter() {

		// super.clearFilter();

		SelectOneMenu selectOne = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTableProblem:cmbProblem");
		if (selectOne != null) {
			selectOne.resetValue();
		}

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFilProblem').clearFilters()");
	}

	public ProblemInvoiceDataModel getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(ProblemInvoiceDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public ProblemInvoice getEntity() {
		return entity;
	}

	public void setEntity(ProblemInvoice entity) {
		this.entity = entity;
	}

	public ObjectUtil getObjectUtil() {
		return objectUtil;
	}

	public void setObjectUtil(ObjectUtil objectUtil) {
		this.objectUtil = objectUtil;
	}

	public ClientApplicationService getClientApplicationService() {
		return clientApplicationService;
	}

	public void setClientApplicationService(
			ClientApplicationService clientApplicationService) {
		this.clientApplicationService = clientApplicationService;
	}

	public String getOptionRate() {
		return optionRate;
	}

	public void setOptionRate(String optionRate) {
		this.optionRate = optionRate;
	}

	public String getOptionEventNoProgram() {
		return optionEventNoProgram;
	}

	public void setOptionEventNoProgram(String optionEventNoProgram) {
		this.optionEventNoProgram = optionEventNoProgram;
	}

	public ClientApplication getProgram() {
		return program;
	}

	public void setProgram(ClientApplication program) {
		this.program = program;
	}

	public List<String[]> getActionWorkWithoutInvoice() {
		return actionWorkWithoutInvoice;
	}

	public void setActionWorkWithoutInvoice(
			List<String[]> actionWorkWithoutInvoice) {
		this.actionWorkWithoutInvoice = actionWorkWithoutInvoice;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public InvoiceService getInvoiceService() {
		return invoiceService;
	}

	public void setInvoiceService(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public String getOptionInvoice() {
		return optionInvoice;
	}

	public void setOptionInvoice(String optionInvoice) {
		this.optionInvoice = optionInvoice;
	}

	public InvoiceWorkService getInvoiceWorkService() {
		return invoiceWorkService;
	}

	public void setInvoiceWorkService(InvoiceWorkService invoiceWorkService) {
		this.invoiceWorkService = invoiceWorkService;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
