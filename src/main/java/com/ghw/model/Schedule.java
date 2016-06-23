package com.ghw.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "schedule")
public class Schedule {

	@Id
	@Column(name = "sch_id", length = 36, columnDefinition = "CHAR(36)")
	private String id;

	@Column(name = "sch_date_start", nullable = false)
	private Date dateStart;

	@Column(name = "sch_date_end", nullable = false)
	private Date dateEnd;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id")
	private User user;

	@ManyToOne(targetEntity = Event.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "eve_id")
	private Event event;

	@ManyToOne(targetEntity = ClientApplication.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_id")
	private ClientApplication clientApplication;
	

	@ManyToOne(targetEntity = InvoiceWork.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "iwo_id")
	private InvoiceWork invoiceWork;

	public Schedule() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public ClientApplication getClientApplication() {
		return clientApplication;
	}

	public void setClientApplication(ClientApplication clientApplication) {
		this.clientApplication = clientApplication;
	}

	public InvoiceWork getInvoiceWork() {
		return invoiceWork;
	}

	public void setInvoiceWork(InvoiceWork invoiceWork) {
		this.invoiceWork = invoiceWork;
	}
	

	
}
