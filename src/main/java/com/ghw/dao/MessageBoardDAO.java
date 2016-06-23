package com.ghw.dao;

import java.util.List;

import com.ghw.model.Invoice;
import com.ghw.model.MessageBoard;

public interface MessageBoardDAO extends GenericDAO<MessageBoard, String> {

	public List<MessageBoard> getDataByInvoice(Invoice invoice);
}