package com.ghw.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "updated_register")
public class UpdatedDate implements Serializable {

	@Id
	@Column(name = "up_id")
	private Integer id;

	@Column(name = "up_date")
	private Date dateUpdated;

	@Column(name = "up_state")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean state;

	@Column(name = "up_make_copy")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean makeCopy;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isMakeCopy() {
		return makeCopy;
	}

	public void setMakeCopy(boolean makeCopy) {
		this.makeCopy = makeCopy;
	}

}