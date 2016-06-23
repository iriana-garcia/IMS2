package com.ghw.model;

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

import org.apache.commons.math.util.MathUtils;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "invoice_hours_added")
public class InvoiceHoursAdded implements IEntityEditable, Cloneable {

	@Id
	@Column(name = "iho_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "iho_hours", nullable = false)
	private Double hours = 0.0;

	@Column(name = "iho_description")
	private String description;

	@ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cat_id", nullable = false)
	private Category category;

	@ManyToOne(targetEntity = InvoiceWork.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "iwo_id", nullable = false)
	private InvoiceWork invoiceWork;

	@Column(name = "iho_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "iho_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@Transient
	private Integer minutes = 0;
	@Transient
	private Double hour = 0.0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getHours() {
		return hours;
	}

	public void setHours(Double hours) {
		this.hours = hours;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public InvoiceWork getInvoiceWork() {
		return invoiceWork;
	}

	public void setInvoiceWork(InvoiceWork invoiceWork) {
		this.invoiceWork = invoiceWork;
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

	@Override
	public String getIdentity() {
		return id;
	}

	@Override
	public String getFieldAdicional() {
		return null;
	}

	@Override
	public void setUpdateDate(Date updateDate) {
	}

	@Override
	public void setUserUpdated(User userUpdated) {
	}

	@Override
	public int compare(Object oldObj) {
		return 0;
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
		if (!(obj instanceof InvoiceHoursAdded))
			return false;
		InvoiceHoursAdded other = (InvoiceHoursAdded) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public boolean equalsAll(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof InvoiceHoursAdded))
			return false;
		InvoiceHoursAdded other = (InvoiceHoursAdded) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		else if (!hours.equals(other.getHours()))
			return false;
		else if (!category.equals(other.getCategory()))
			return false;
		else if ((description == null && other.getDescription() != null)
				|| (description != null && other.getDescription() == null)
				|| !description.equals(other.getDescription()))
			return false;
		return true;
	}

	@Override
	public InvoiceHoursAdded clone() {
		InvoiceHoursAdded clone = null;
		try {

			clone = (InvoiceHoursAdded) super.clone();

		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e); // won't happen
		}
		return clone;
	}

	@Override
	public String toString() {
		return "Hours: " + hours + " Category: " + category + " Description: "
				+ description;
	}

	public void calculate() {
		if (hours != null) {
			minutes = new Double((hours * 60) % 60).intValue();
			hour = new Double(Math.floor((hours * 60) / 60));

		}

	}

	public void calculateHours() {
		if (hour != null && minutes != null) {
			setHours(MathUtils.round(getHour()
					+ (new Double(getMinutes()) / 60), 2));

		}

	}

	public Integer getMinutes() {
		return minutes;
	}

	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}

	public Double getHour() {
		return hour;
	}

	public void setHour(Double hour) {
		this.hour = hour;
	}

}
