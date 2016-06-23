package com.ghw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "category")
public class Category implements Serializable, IEntityEditable {

	@Id
	@Column(name = "cat_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "cat_name", nullable = false, unique = true)
	private String name;

	@Column(name = "cat_state", nullable = false)
	private boolean state = true;

	@Column(name = "cat_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "cat_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	private List<InvoiceHoursAdded> listHoursAddeds = new ArrayList<InvoiceHoursAdded>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<InvoiceHoursAdded> getListHoursAddeds() {
		return listHoursAddeds;
	}

	public void setListHoursAddeds(List<InvoiceHoursAdded> listHoursAddeds) {
		this.listHoursAddeds = listHoursAddeds;
	}

	public Category() {
		super();
	}

	public Category(String id) {
		super();
		this.id = id;
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
		if (!(obj instanceof Category))
			return false;
		Category other = (Category) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "Name: " + name;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	@Transient
	private String fieldAdicional = "";

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

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	@Override
	public int compare(Object oldObj) {
		Category o = (Category) oldObj;

		if (!getName().equals(o.getName()))
			fieldAdicional += " Old name: " + o.getName();

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
	}

}
