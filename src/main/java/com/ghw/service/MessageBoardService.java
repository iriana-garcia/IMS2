package com.ghw.service;

import java.util.List;

import com.ghw.model.Invoice;
import com.ghw.model.MessageBoard;
import com.ghw.model.Profile;

public interface MessageBoardService extends Service<MessageBoard, String> {

	// public MessageBoard sendQuestion(Invoice invoice, Profile user,
	// String from, String to, String bcc, String subject, String body)
	// throws Exception;
	//
	// public MessageBoard replyQuestion(MessageBoard messageBoard, String from,
	// String to, String subject) throws Exception;

	public MessageBoard sendQuestion(Invoice invoice, Profile user,
			String message) throws Exception;

	public MessageBoard replyQuestion(MessageBoard messageBoard)
			throws Exception;

	public List<MessageBoard> getDataByInvoice(Invoice invoice);

}