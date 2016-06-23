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
@Table(name = "address")
public class Address implements IEntityEditable, Serializable {

	@Id
	@Column(name = "add_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "add_type")
	private String type = "C";

	@Column(name = "add_description")
	private String description;

	@Column(name = "add_zip_code")
	private String zipCode;

	@Column(name = "add_city")
	private String city;

	@Column(name = "add_state")
	private String state;

	@Column(name = "add_need_update")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean needUpdate = false;

	@ManyToOne(targetEntity = Corporation.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cor_id", nullable = true)
	private Corporation corporation;

	@ManyToOne(targetEntity = Country.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cou_id", nullable = true)
	private Country country;

	@ManyToOne(targetEntity = States.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "sta_id", nullable = true)
	private States states;

	@Column(name = "add_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "add_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@Transient
	private String corporationName;

	public String getStateDescription() {

		return StringUtils.isNotBlank(state) ? state : (states == null ? ""
				: states.getName());
	}

	public String getLastPart() {
		return city + " " + getStateDescription() + ", " + zipCode + " "
				+ getCountry().getName();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Corporation getCorporation() {
		return corporation;
	}

	public void setCorporation(Corporation corporation) {
		this.corporation = corporation;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public States getStates() {
		return states;
	}

	public void setStates(States states) {
		this.states = states;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	public String getCorporationName() {
		return corporationName;
	}

	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public String getIdentity() {
		return id;
	}

	public Address(String description, String zipCode, String city,
			String state, boolean needUpdate) {
		super();
		this.description = description;
		this.zipCode = zipCode;
		this.city = city;
		this.state = state;
		this.needUpdate = needUpdate;
	}

	public Address() {
		super();
	}

	public Address(String id, String type, String description, String zipCode,
			String city, States states, Country country) {
		super();
		this.id = id;
		this.type = type;
		this.description = description;
		this.zipCode = zipCode;
		this.city = city;
		this.states = states;
		this.country = country;
	}

	@Transient
	private String fieldAdicional = "";

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	@Override
	public int compare(Object oldObj) {
		Address o = (Address) oldObj;

		if (description != null && !getDescription().equals(o.getDescription())) {
			fieldAdicional += " New address: " + description + " Old address: "
					+ o.getDescription();
			needUpdate = true;
		}

		if (country != null && !country.equals(o.getCountry()))
			fieldAdicional += " New country: " + country.toString()
					+ " Old country: " + o.getCountry().toString();

		if (!getStateDescription().equals(o.getStateDescription())) {
			fieldAdicional += " New state: " + getStateDescription()
					+ " Old state: " + o.getStateDescription();
			needUpdate = true;
		}

		if (city != null && !city.equals(o.getCity())) {
			fieldAdicional += " New city: " + city + " Old city: "
					+ o.getCity();
			needUpdate = true;
		}

		if (zipCode != null && !zipCode.equals(o.getZipCode())) {
			fieldAdicional += " New zip code: " + zipCode + " Old zip code: "
					+ o.getZipCode();
			needUpdate = true;
		}

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
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
		if (!(obj instanceof Address))
			return false;

		Address other = (Address) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
