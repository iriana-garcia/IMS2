package com.ghw.model;

import java.text.SimpleDateFormat;
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

@Entity
@Table(name = "incentive")
public class Incentive implements IEntityEditable {

	@Id
	@Column(name = "inc_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "inc_date", nullable = false)
	private Date date;

	@Column(name = "inc_amount", nullable = false)
	private Double amount = 0.0;

	@Column(name = "inc_description")
	private String description;

	@Column(name = "inc_type")
	private String type = "I";

	@ManyToOne(targetEntity = Skill.class)
	@JoinColumn(name = "ski_id", nullable = true)
	private Skill skill;

	@ManyToOne(targetEntity = Invoice.class)
	@JoinColumn(name = "inv_id", nullable = true)
	private Invoice invoice;

	@Column(name = "inc_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "inc_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@Transient
	private String fieldAdicional = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	@Override
	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public User getUserUpdated() {
		return userUpdated;
	}
	
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setUserUpdated(User userUpdated) {
		this.userUpdated = userUpdated;
	}

	@Override
	public String toString() {
		return "Amount: "
				+ amount
				+ " Type: "
				+ type
				+ " Client Application: "
				+ (skill != null ? skill.toString() : "")
				+ " Date: "
				+ (date != null ? (new SimpleDateFormat("E, M-d-yy"))
						.format(date) : "");
	}

	@Override
	public int compare(Object old) {

		Incentive o = (Incentive) old;

		if (!getAmount().equals(o.getAmount()))
			fieldAdicional += " Old amount: " + o.getAmount();

		if (StringUtils.isNotBlank(description)
				&& StringUtils.isBlank(o.getDescription()))
			fieldAdicional += " New description: " + description;

		if (StringUtils.isBlank(description)
				&& StringUtils.isNotBlank(o.getDescription()))
			fieldAdicional += " Old description: " + o.getDescription();

		if (StringUtils.isNotBlank(description)
				&& StringUtils.isNotBlank(o.getDescription())
				&& !description.equals(o.getDescription()))
			fieldAdicional += " New description: " + description
					+ " Old description: " + o.getDescription();

		if (!type.equals(o.getType()))
			fieldAdicional += " Old type: " + o.getType();

		if ((skill != null && !skill.equals(o.getSkill()))
				|| (skill == null && o.getSkill() != null))
			fieldAdicional += " Old CA: "
					+ (o.getSkill() == null ? "Undefined" : o.getSkill()
							.toString());

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
	}

}
