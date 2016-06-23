package com.ghw.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "invoice_history")
public class InvoiceHistory {

	@Id
	@Column(name = "ihi_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@ManyToOne(targetEntity = Profile.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id", nullable = true)
	private Profile user;

	@Column(name = "ihi_import_total")
	private Double importTotal;

	@Column(name = "ihi_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "ihi_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Profile getUser() {
		return user;
	}

	public void setUser(Profile user) {
		this.user = user;
	}

	public Double getImportTotal() {
		return importTotal;
	}

	public void setImportTotal(Double importTotal) {
		this.importTotal = importTotal;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	}

	public User getUserUpdated() {
		return userUpdated;
	}

	public void setUserUpdated(User userUpdated) {
		this.userUpdated = userUpdated;
	}

}
