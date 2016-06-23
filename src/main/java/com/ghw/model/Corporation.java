package com.ghw.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.hibernate.annotations.Type;

@Entity
@Table(name = "corporation")
public class Corporation implements IEntityEditable {

	@Id
	@Column(name = "cor_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "cor_name")
	private String name;

	@Column(name = "cor_ein")
	private String ein;

	@Column(name = "cor_supplier_number", updatable = false)
	private String supplierNumber;

	@Column(name = "cor_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", updatable = false)
	private User userCreated;

	@Column(name = "cor_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@Column(name = "cor_need_update", updatable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean needUpdate = false;

	@Column(name = "cor_type")
	@Enumerated(EnumType.STRING)
	private CorporationType type;

	@Column(name = "cor_pay_method")
	@Enumerated(EnumType.STRING)
	private PayMethod payMethod;

	@OneToMany(mappedBy = "corporation", fetch = FetchType.LAZY)
	private List<BankInformation> bankInformation = new ArrayList<BankInformation>();

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "corporation", fetch = FetchType.LAZY)
	private List<Address> address = new ArrayList<Address>();

	@OneToMany(mappedBy = "corporation", fetch = FetchType.LAZY)
	private List<Profile> profiles = new ArrayList<Profile>();

	@Transient
	private BankInformation bank;

	@Transient
	private Profile ibo;

	@Transient
	private Address addressCorporation;

	@Transient
	private String idPrincipalOwner;

	@Transient
	private String problemId;

	public String getAddressFormat() {
		String a = "";
		if (address != null && address.size() > 0) {
			a = address.get(0).getDescription() + " <br/>"
					+ address.get(0).getCity() + " "
					+ address.get(0).getStateDescription() + " "
					+ address.get(0).getZipCode();
		}
		return a;
	}

	public String getAddressCountryFormat() {
		String a = "";
		if (address != null && address.size() > 0) {
			a = address.get(0).getDescription() + " <br/>"
					+ address.get(0).getCity() + " "
					+ address.get(0).getStateDescription() + " "
					+ address.get(0).getZipCode() + " "
					+ address.get(0).getCountry().getName();
		}
		return a;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEin() {
		return ein;
	}

	public void setEin(String ein) {
		this.ein = ein;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public User getUserUpdated() {
		return userUpdated;
	}

	public void setUserUpdated(User userUpdated) {
		this.userUpdated = userUpdated;
	}

	public List<BankInformation> getBankInformation() {
		return bankInformation;
	}

	public void setBankInformation(List<BankInformation> bankInformation) {
		this.bankInformation = bankInformation;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
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

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return " Company name: " + name;
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

	public String getSupplierNumber() {
		return supplierNumber;
	}

	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = supplierNumber;
	}

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	@Override
	public int compare(Object old) {
		Corporation o = (Corporation) old;

		if (!name.equals(o.getName())) {
			fieldAdicional += " New corporation name: " + name
					+ " Old corporation name: " + o.getName();
			needUpdate = true;
		}

		if (ein != null && !ein.equals(o.getEin())) {
			fieldAdicional += " New EIN: " + ein + " Old EIN: " + o.getEin();
			needUpdate = true;
		}

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
	}

	public Corporation() {
		super();
	}

	public Corporation(String id) {
		super();
		this.id = id;
	}

	public Corporation(String id, String name, String ein,
			CorporationType type, PayMethod payMethod) {
		super();
		this.id = id;
		this.name = name;
		this.ein = ein;
		this.type = type;
		this.payMethod = payMethod;
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
		if (!(obj instanceof Corporation))
			return false;

		Corporation other = (Corporation) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Corporation(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public BankInformation getBank() {
		return bank;
	}

	public void setBank(BankInformation bank) {
		this.bank = bank;
	}

	public Profile getIbo() {
		return ibo;
	}

	public void setIbo(Profile ibo) {
		this.ibo = ibo;
	}

	public Address getAddressCorporation() {
		if (addressCorporation == null && address != null && address.size() > 0)
			addressCorporation = address.get(0);
		return addressCorporation;
	}

	public void setAddressCorporation(Address addressCorporation) {
		this.addressCorporation = addressCorporation;
	}

	public String getIdPrincipalOwner() {
		return idPrincipalOwner;
	}

	public void setIdPrincipalOwner(String idPrincipalOwner) {
		this.idPrincipalOwner = idPrincipalOwner;
	}

	public boolean isHasPrincipalOwner() {
		return StringUtils.isNotBlank(idPrincipalOwner);
	}

	public CorporationType getType() {
		return type;
	}

	public void setType(CorporationType type) {
		this.type = type;
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public String getProblemId() {
		return problemId;
	}

	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	public String getProblemDescription() {
		return ProblemCorporation.valueOf(problemId).getValor();
	}

}
