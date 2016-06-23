package com.ghw.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "vw_onboarding")
public class UserUtil implements IEntity {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "use_name")
	private String userName;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "ibo_state")
	private String status;

	@Column(name = "ibo_state_id")
	private String statusId;

	@Column(name = "number")
	private String number;

	@Column(name = "state")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean state = true;

	@Transient
	private String telephoneNumber, distinguishedName;

	@Column(name = "corporation_name")
	private String corporationName;

	@Column(name = "add_description")
	private String address;

	@Column(name = "add_zip_code")
	private String zipCode;

	@Column(name = "add_city")
	private String city;

	@Column(name = "cou_name", nullable = false, unique = true)
	private String country;

	@Column(name = "sta_name", nullable = false, unique = true)
	private String state1;

	@Column(name = "add_state", nullable = false, unique = true)
	private String state2;

	@Column(name = "temporal")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean temporal;

	@Column(name = "has_bank")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean hasBankInformation;

	@Column(name = "type_contract")
	@Enumerated(EnumType.STRING)
	private TypeContract typeContract;

	public String getFull() {
		return userName + " - " + firstName + " - " + lastName + " - " + email;
	}

	public String getAddressFull() {
		return StringUtils.isBlank(corporationName) ? "" : (address + " "
				+ (StringUtils.isNotBlank(city) ? city + ", " : " ")
				+ getStateDescription() + " " + zipCode);
	}

	public String getStateDescription() {
		return StringUtils.isNotBlank(state2) ? state2 : state1;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public boolean isTemporal() {
		return temporal;
	}

	public void setTemporal(boolean temporal) {
		this.temporal = temporal;
	}

	private String getStateDescription(boolean state) {
		return state ? "Active" : "Inactive";
	}

	public String getStateUserDescription() {
		return getStateDescription(state);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((distinguishedName == null) ? 0 : distinguishedName
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserUtil))
			return false;
		UserUtil other = (UserUtil) obj;
		if (distinguishedName == null) {
			if (other.distinguishedName != null)
				return false;
		} else if (!distinguishedName.equals(other.distinguishedName))
			return false;
		return true;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCorporationName() {
		return corporationName;
	}

	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState1() {
		return state1;
	}

	public void setState1(String state1) {
		this.state1 = state1;
	}

	public String getState2() {
		return state2;
	}

	public void setState2(String state2) {
		this.state2 = state2;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	@Override
	public String getIdentity() {
		return id + "-" + userName;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public boolean getHasBankInformation() {
		return hasBankInformation;
	}

	public void setHasBankInformation(boolean hasBankInformation) {
		this.hasBankInformation = hasBankInformation;
	}

	public TypeContract getTypeContract() {
		return typeContract;
	}

	public void setTypeContract(TypeContract typeContract) {
		this.typeContract = typeContract;
	}

}
