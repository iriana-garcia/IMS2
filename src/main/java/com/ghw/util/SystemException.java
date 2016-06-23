package com.ghw.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

/**
 * This type of exception is not inserted in the log system
 * 
 * @author ifernandez
 * 
 */
public class SystemException extends Exception {

	private String message;

	private String idComponent;

	private Severity severity = FacesMessage.SEVERITY_ERROR;

	private List<SystemException> exceptions = new ArrayList<SystemException>();

	private static final long serialVersionUID = 1L;

	public SystemException(String mensage) {
		this.message = mensage;

	}

	public SystemException(String mensage, String idComponent) {
		this.message = mensage;
		this.idComponent = idComponent;

	}

	public SystemException(List<SystemException> exceptions) {
		super();
		this.exceptions = exceptions;
	}

	public SystemException(String message, Severity severity) {
		super();
		this.message = message;
		this.severity = severity;
	}

	public SystemException(String message, String idComponent, Severity severity) {
		super();
		this.message = message;
		this.idComponent = idComponent;
		this.severity = severity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIdComponent() {
		return idComponent;
	}

	public void setIdComponent(String idComponent) {
		this.idComponent = idComponent;
	}

	public List<SystemException> getExceptions() {
		return exceptions;
	}

	public void setExceptions(List<SystemException> exceptions) {
		this.exceptions = exceptions;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

}
