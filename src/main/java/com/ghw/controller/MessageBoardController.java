package com.ghw.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;

import com.ghw.datamodel.MessageBoardDataModel;
import com.ghw.filter.MessageBoardFilter;
import com.ghw.model.Invoice;
import com.ghw.model.MessageBoard;
import com.ghw.model.User;
import com.ghw.service.MessageBoardService;
import com.ghw.util.Context;

//@ManagedBean
//@ViewScoped
public class MessageBoardController extends
		Controller<MessageBoard, String, MessageBoardService> {

	@ManagedProperty(value = "#{messageBoardDataModel}")
	private MessageBoardDataModel lazyModel;

	@ManagedProperty(value = "#{messageBoardService}")
	private MessageBoardService service;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	private String from, subject, content;

	private boolean showAddMessage = false, showButtonAddMessage = false;

	private List<MessageBoard> messageBoards;

	private Invoice invoice = null;

	@ManagedProperty(value = "#{messageBoardFilter}")
	private MessageBoardFilter filter;

	@PostConstruct
	public void init() {

		// Map<String, Object> filters = new HashMap<String, Object>();
		// filters.put("status", 2);
		//
		// DataTable dataTable = (DataTable) FacesContext.getCurrentInstance()
		// .getViewRoot().findComponent("form:uniTable");
		//
		// if (dataTable != null)
		// dataTable.setFilters(filters);

		filter.setStatus(1);

		SelectOneMenu selectOne = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbStatusMessage");
		if (selectOne != null) {
			selectOne.setValue(1);
		}

	}

	/**
	 * Clear the datatable filter
	 */
	@Override
	public void clearFilter() {

		// super.clearFilter();

		filter.setStatus(null);

		SelectOneMenu selectOne = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbStatusMessage");
		if (selectOne != null) {
			selectOne.resetValue();
		}

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').clearFilters()");
	}

	// public void showReplyMessage() {
	//
	// from = sessionBean.getUser().getEmail();
	// }

	public void changeToEdit() {
		showAddMessage = true;
		showButtonAddMessage = false;
	}

	public void sendReply(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			service.replyQuestion(entity);

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

	public void cancelQuestion(ActionEvent event) {
		showAddMessage = false;
		showButtonAddMessage = false;
	}

	public void openMessageBoard() {

		try {

			// showAddMessage = invoice != null
			// && ((isShowButtonIbo() && invoice.getHaveQuestion() == null
			// && invoice.getState() != null && (invoice
			// .getState().getId().equals("1") || invoice
			// .getState().getId().equals("2"))) || (sessionBean
			// .getUser() != null
			// && !sessionBean.getUser().isAnIbo()
			// && invoice.getHaveQuestion() != null && invoice
			// .getHaveQuestion().equals(2)));

			messageBoards = null;
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	public void sendQuestion(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			// see if a need to insert a question or an reply
			// boolean allReply = true;
			//
			// for (MessageBoard m : messageBoards) {
			// if (m.getDateReplyMessage() == null) {
			// entity = m;
			// allReply = false;
			// break;
			// }
			// }

			// if (sessionBean.getUser().isAnIbo() && allReply
			// && isShowButtonIbo()) {
			// service.sendQuestion(getInvoice(), invoice.getUser(), content);
			// context.execute("PF('dlgMessageWarning').show()");
			// invoice.setHaveQuestion(2);
			//
			// } else if (!sessionBean.getUser().isAnIbo() && !allReply) {
			// entity.setReplyMessage(content);
			// service.replyQuestion(entity);
			// invoice.setHaveQuestion(1);
			//
			// }

			content = null;
			showAddMessage = false;
			messageBoards = null;

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

	public boolean isShowAddMessage() {
		return showAddMessage;
	}

	public void setShowAddMessage(boolean showAddMessage) {
		this.showAddMessage = showAddMessage;
	}

	public MessageBoardDataModel getLazyModel() {
		return lazyModel;
	}

	@Override
	public MessageBoardService getService() {
		return service;
	}

	public void setService(MessageBoardService service) {
		this.service = service;
	}

	public void setLazyModel(MessageBoardDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	@Override
	public MessageBoard getEntity() {
		if (entity == null) {
			entity = new MessageBoard();
		}
		return entity;
	}

	public boolean isDeactiveState() {
		return deactiveState;
	}

	public void setDeactiveState(boolean deactiveState) {
		this.deactiveState = deactiveState;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public String getSubject() {
		return subject;
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

	public List<MessageBoard> getMessageBoards() {

		if (messageBoards == null && invoice != null
				&& StringUtils.isNotBlank(invoice.getId())) {
			messageBoards = service.getDataByInvoice(invoice);
		}
		return messageBoards;
	}

	public void setMessageBoards(List<MessageBoard> messageBoards) {
		this.messageBoards = messageBoards;
	}

	private boolean showButtonIbo = true;

	public boolean isShowButtonIbo() {

		User user = sessionBean.getUser();

		showButtonIbo = user.isAnIbo() && invoice != null
				&& invoice.getUser() != null
				&& invoice.getUser().getId().equals(user.getIbo().getId());

		return showButtonIbo;
	}

	public boolean isShowButtonAddMessage() {
		// showButtonAddMessage = showAddMessage == false
		// && invoice != null
		// && invoice.isEditInvoice()
		// && ((isShowButtonIbo() && !invoice.isHavePendingReply()) ||
		// (!sessionBean
		// .getUser().isAnIbo() && invoice.isHavePendingReply()));
		return showButtonAddMessage;
	}

	public void setShowButtonIbo(boolean showButtonIbo) {
		this.showButtonIbo = showButtonIbo;
	}

	public void setShowButtonAddMessage(boolean showButtonAddMessage) {
		this.showButtonAddMessage = showButtonAddMessage;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public MessageBoardFilter getFilter() {
		return filter;
	}

	public void setFilter(MessageBoardFilter filter) {
		this.filter = filter;
	}

}
