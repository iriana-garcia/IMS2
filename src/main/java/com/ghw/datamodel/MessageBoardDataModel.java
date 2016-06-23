package com.ghw.datamodel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ghw.filter.FilterBase;
import com.ghw.filter.MessageBoardFilter;
import com.ghw.model.MessageBoard;
import com.ghw.service.MessageBoardService;

@ManagedBean
@ViewScoped
public class MessageBoardDataModel extends
		GenericDataModel<String, MessageBoard, MessageBoardService> {

	@ManagedProperty(value = "#{messageBoardService}")
	private MessageBoardService service;

	@ManagedProperty(value = "#{messageBoardFilter}")
	private MessageBoardFilter filterBase;

	public MessageBoardService getService() {
		return service;
	}

	public void setService(MessageBoardService service) {
		this.service = service;
		super.setService(service);
	}

	@Override
	public void setFilterBase(FilterBase filterBase) {
		this.filterBase = (MessageBoardFilter) filterBase;
		super.setFilterBase(filterBase);

	}

	@Override
	public FilterBase getFilterBase() {
		return filterBase;
	}

}
