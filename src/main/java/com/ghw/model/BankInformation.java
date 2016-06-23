package com.ghw.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "bank_information")
public class BankInformation implements Serializable, IEntityEditable {

	@Id
	@Column(name = "ban_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@ManyToOne(targetEntity = Corporation.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cor_id", nullable = false)
	private Corporation corporation;

	@Column(name = "ban_name", nullable = true)
	@Type(type = "encryptedString")
	private String name;

	@Column(name = "ban_number", nullable = false)
	@Type(type = "encryptedString")
	private String number;

	@Column(name = "ban_routing", nullable = false)
	@Type(type = "encryptedString")
	private String routing;

	@Column(name = "ban_need_update")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean needUpdate = false;

	@Transient
	private String routingConfirm, numberConfirm, supplierNumber, email;

	@Column(name = "ban_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "ban_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@Transient
	private String fieldAdicional = "";

	public Corporation getCorporation() {
		return corporation;
	}

	public void setCorporation(Corporation corporation) {
		this.corporation = corporation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getRouting() {
		return routing;
	}

	public void setRouting(String routing) {
		this.routing = routing;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoutingConfirm() {
		return routingConfirm;
	}

	public void setRoutingConfirm(String routingConfirm) {
		this.routingConfirm = routingConfirm;
	}

	public String getNumberConfirm() {
		return numberConfirm;
	}

	public void setNumberConfirm(String numberConfirm) {
		this.numberConfirm = numberConfirm;
	}

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	public String getSupplierNumber() {
		return supplierNumber;
	}

	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = supplierNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BankInformation))
			return false;
		BankInformation other = (BankInformation) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
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

	@Override
	public int compare(Object oldObj) {
		BankInformation o = (BankInformation) oldObj;

		if (o == null)
			fieldAdicional += " Routing number, Account Number ";
		else {

			if (!getRouting().equals(o.getRouting()))
				fieldAdicional += " Routing number, ";

			if (!getNumber().equals(o.getNumber()))
				fieldAdicional += " Account Number ";
		}

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
	}

}
