package com.ghw.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.controller.SessionBean;
import com.ghw.dao.MessageBoardDAO;
import com.ghw.model.Invoice;
import com.ghw.model.MessageBoard;
import com.ghw.model.Profile;
import com.ghw.service.EmailService;
import com.ghw.service.MessageBoardService;

@Service("messageBoardService")
@Transactional(readOnly = false)
public class MessageBoardServiceImpl extends
		ServiceImpl<MessageBoard, String, MessageBoardDAO> implements
		MessageBoardService {

	private MessageBoardDAO dao;

	@Autowired
	private EmailService emailService;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	public void setDAO(MessageBoardDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	/**
	 * Send a question to IBOC from IBO and update in DB that has a question
	 * 
	 * @param invoice
	 * @throws Exception
	 */
	@Loggable
	@PreAuthorize("hasAnyRole('IBO')")
	@Override
	public MessageBoard sendQuestion(Invoice invoice, Profile user,
			String message) throws Exception {

		String contentEmail = "content ";

		MessageBoard messageBoard = new MessageBoard();
		messageBoard.setInvoice(invoice);
		messageBoard.setMessage(message);
		messageBoard.setDateMessage(new Date());

		// update field question in Invoice
		dao.makePersistent(messageBoard);

		messageBoard.setFieldAdicional("User: " + user.toString()
				+ " Invoice: " + invoice.toString());

		return messageBoard;

	}

	// @Loggable
	// @PreAuthorize("hasAnyRole('IBO')")
	// public MessageBoard sendQuestion(Invoice invoice, Profile user,
	// String from, String to, String bcc, String subject, String body)
	// throws Exception {
	//
	// String contentEmail = "content ";
	//
	// MessageBoard messageBoard = new MessageBoard();
	// messageBoard.setInvoice(invoice);
	// messageBoard.setMessage(body);
	// messageBoard.setDateMessage(new Date());
	//
	// // update field question in Invoice
	// dao.makePersistent(messageBoard);
	//
	// messageBoard.setFieldAdicional("User: " + user.toString()
	// + " Invoice: " + invoice.toString());
	//
	// // send an email
	// emailService.sendQuestion(from, to, bcc, subject, contentEmail);
	//
	// return messageBoard;
	//
	// }

	/**
	 * Reply a question to IBO from IBOC and update in DB that has a question
	 * 
	 * @param invoice
	 * @throws Exception
	 */

	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','MESSAGE_M')")
	@Override
	public MessageBoard replyQuestion(MessageBoard messageBoard)
			throws Exception {

		String contentEmail = "content ";

		messageBoard.setUserReply(sessionBean.getUser());
		messageBoard.setDateReplyMessage(new Date());

		// update field question in Invoice
		dao.update(messageBoard);

		messageBoard.setFieldAdicional("User: Number: "
				+ messageBoard.getInvoice().getUser().getNumber()
				+ " Company name: "
				+ messageBoard.getInvoice().getUser().getCorporationName()
				+ " Invoice: " + messageBoard.getInvoice().getNumber());

		return messageBoard;

	}
	// @Loggable
	// @PreAuthorize("hasAnyRole('SUPER','MESSAGE_M')")
	// @Override
	// public MessageBoard replyQuestion(MessageBoard messageBoard, String from,
	// String to, String subject) throws Exception {
	//
	// String contentEmail = "content ";
	//
	// messageBoard.setUserReply(sessionBean.getUser());
	// messageBoard.setDateReplyMessage(new Date());
	//
	// // update field question in Invoice
	// dao.update(messageBoard);
	//
	// messageBoard.setFieldAdicional("User: Number: "
	// + messageBoard.getInvoice().getUser().getNumber()
	// + " Company name: "
	// + messageBoard.getInvoice().getUser().getCorporationName()
	// + " Invoice: " + messageBoard.getInvoice().getNumber());
	//
	// // send an email
	// emailService.sendQuestion(from, to, null, subject, contentEmail);
	//
	// return messageBoard;
	//
	// }

	@Override
	public List<MessageBoard> getDataByInvoice(Invoice invoice) {
		return dao.getDataByInvoice(invoice);
	}
}