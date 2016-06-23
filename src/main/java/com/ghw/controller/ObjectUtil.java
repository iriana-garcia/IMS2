package com.ghw.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Utilitary class for store in session the entity value when add or edit
 * 
 * @author ifernandez
 * 
 */
@SessionScoped
@ManagedBean
public class ObjectUtil implements Serializable {

	private Object object;
	private Integer edit;

	private String action;

	private String idEvent;

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Integer getEdit() {
		return edit;
	}

	public void setEdit(Integer edit) {
		this.edit = edit;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getIdEvent() {
		return idEvent;
	}

	public void setIdEvent(String idEvent) {
		this.idEvent = idEvent;
	}

	public void clean() {
		setObject(null);
		setEdit(null);
		action = null;
		idEvent = null;
	}

}
