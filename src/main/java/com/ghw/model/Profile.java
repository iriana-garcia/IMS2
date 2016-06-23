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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.ghw.constant.Constant;

@Entity
@Table(name = "profile")
public class Profile implements IEntityEditable {

	@Id
	@Column(name = "pro_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id")
	private User user;

	@Column(name = "pro_princ_title")
	private String princTitle;

	@Column(name = "pro_region")
	private String region = "D";

	@Column(name = "pro_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", updatable = false)
	private User userCreated;

	@Column(name = "pro_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@Column(name = "pro_amount")
	private Double amount;

	@Column(name = "pro_number", updatable = false)
	private String number;

	@Column(name = "pro_total_submit")
	private Integer totalSubmit = 0;

	@Column(name = "pro_need_update")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean needUpdate = false;

	@Column(name = "pro_owner_type")
	@Enumerated(EnumType.STRING)
	private OwnerType ownerType;

	@Column(name = "pro_birthday")
	@Temporal(TemporalType.DATE)
	private Date birthday;

	@Column(name = "pro_gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(name = "pro_pay_type")
	@Enumerated(EnumType.STRING)
	private PayType payType;

	@Column(name = "pro_pay_method")
	@Enumerated(EnumType.STRING)
	private PayMethod payMethod;

	@Column(name = "pro_type_contract")
	@Enumerated(EnumType.STRING)
	private TypeContract typeContract;

	@ManyToOne(targetEntity = Corporation.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cor_id", nullable = true)
	private Corporation corporation;

	@ManyToOne(targetEntity = IboType.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "ity_id", nullable = true)
	private IboType type;

	@ManyToOne(targetEntity = IboState.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "ist_id", nullable = true)
	private IboState iboState;

	@ManyToOne(targetEntity = Groups.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "grp_id", nullable = true)
	private Groups group;

	@ManyToOne(targetEntity = Country.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cou_id", nullable = true)
	private Country country = new Country(Constant.usCountryId);

	@ManyToOne(targetEntity = ProfesionalSkills.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "prs_id", nullable = true)
	private ProfesionalSkills profesionalSkill;

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "user", fetch = FetchType.LAZY)
	private List<IbosClientApplications> clientApplications = new ArrayList<IbosClientApplications>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Invoice> invoices = new ArrayList<Invoice>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<InvoiceHistory> invoiceHistory = new ArrayList<InvoiceHistory>();

	@Transient
	private List<ClientApplication> listCa = new ArrayList<ClientApplication>();

	// use to know if has a CA associate in Update CA to fill the IBO picklist
	@Transient
	private boolean hasCa = false;

	@Transient
	private String style = "";

	@Transient
	private String fieldAdicional = "";

	// save the total of IBO exists in the actual IBO's corporation, used in
	// Inactive IBO Owner Type = Principal Owner
	@Transient
	private Long totalIboCorporation;
	// save the new IBO Principal in the actual IBO's corporation, used in
	// Inactive IBO Owner Type = Principal Owner
	@Transient
	private String idNewIboPrincipal;

	@Transient
	// save the date modification invoices
	private Date dateModification = null;

	public String getFullName() {

		return user != null ? user.getFullName() : "";
	}

	public String getAddressFormat() {
		String a = "";
		if (corporation != null) {
			a = corporation.getAddressFormat();
		}
		return a;
	}

	public String getAddressCountryFormat() {
		String a = "";
		if (corporation != null) {
			a = corporation.getAddressCountryFormat();
		}
		return a;
	}

	public String getCorporationName() {
		return corporation != null ? corporation.getName() : "";
	}

	/**
	 * Is the name show in all the selectOneMEnu
	 * 
	 * @return
	 */
	public String getNameSelectOne() {
		return (StringUtils.isNotBlank(number) ? number + " - " : "")
				+ getFullName();
	}

	/**
	 * Is the name show in table when is necessary only one column like invoices
	 * and invoices history screen
	 * 
	 * @return
	 */
	public String getNameTable() {
		return getFullName();
	}

	public String getRegionDescription() {
		return StringUtils.isNotBlank(region) ? Region.valueOf(Region.class,
				region).getValor() : "";
	}

	public boolean isDomestic() {
		return StringUtils.isNotBlank(region) && region.equals("D") ? true
				: false;
	}

	public boolean isAnPA() {
		return type != null && type.isAnPA();
	}

	/**
	 * Constructors
	 * 
	 * @return
	 */

	public Profile() {
		super();
	}

	public Profile(String id) {
		super();
		this.id = id;
	}

	public Profile(User user) {
		super();
		this.user = user;
	}

	public Profile(String id, String number) {
		super();
		this.id = id;
		this.number = number;
	}

	public Profile(String id, Corporation corporation) {
		super();
		this.id = id;
		this.corporation = corporation;
	}

	public Profile(String id, Corporation corporation, String number,
			String userId, String email) {
		super();
		this.id = id;
		this.corporation = corporation;
		this.number = number;
		this.user = new User(userId);
		this.user.setEmail(email);
	}

	public Profile(String id, Corporation corporation, String number,
			String userId, String email, String typeId, String firstName,
			String lastName) {
		super();
		this.id = id;
		this.corporation = corporation;
		this.number = number;
		this.user = new User(userId, firstName, lastName, email, new UserType(
				typeId));
	}

	public Profile(String id, String corporationName, String number,
			TypeContract typeContract, String userId, String email,
			String firstName, String middleName, String lastName) {
		super();
		this.id = id;
		this.corporation = new Corporation(null, corporationName);
		this.number = number;
		this.typeContract = typeContract;
		this.user = new User(userId, firstName, middleName, lastName, email);
	}

	public Profile(String id, User user, String number) {
		super();
		this.id = id;
		this.user = user;
		this.number = number;
	}

	public Profile(String id, User user) {
		super();
		this.id = id;
		this.user = user;
	}

	public Profile(String id, String number, OwnerType ownerType,
			String idUser, String firstName, String middleName,
			String lastName, String email) {
		super();
		this.id = id;
		this.number = number;
		this.ownerType = ownerType;
		this.user = new User(idUser, firstName, middleName, lastName, email);
	}

	public Profile(String id, OwnerType ownerType, Long totalIboCorporation,
			String idNewIboPrincipal) {
		super();
		this.id = id;
		this.ownerType = ownerType;
		this.totalIboCorporation = totalIboCorporation;
		this.idNewIboPrincipal = idNewIboPrincipal;

	}

	public Profile(String email, TypeContract typeContract) {
		super();
		this.user = new User();
		user.setEmail(email);
		this.typeContract = typeContract;
	}

	public Profile(String id, String princTitle, String region, Double amount,
			String number, OwnerType ownerType, Date birthday, Gender gender,
			PayType payType, PayMethod payMethod, TypeContract typeContract) {
		super();
		this.id = id;
		this.princTitle = princTitle;
		this.region = region;
		this.amount = amount;
		this.number = number;
		this.ownerType = ownerType;
		this.birthday = birthday;
		this.gender = gender;
		this.payType = payType;
		this.payMethod = payMethod;
		this.typeContract = typeContract;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPrincTitle() {
		return princTitle;
	}

	public void setPrincTitle(String princTitle) {
		this.princTitle = princTitle;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public IboType getType() {
		return type;
	}

	public void setType(IboType type) {
		this.type = type;
	}

	public IboState getIboState() {
		return iboState;
	}

	public void setIboState(IboState iboState) {
		this.iboState = iboState;
	}

	public Groups getGroup() {
		return group;
	}

	public void setGroup(Groups group) {
		this.group = group;
	}

	public List<IbosClientApplications> getClientApplications() {
		return clientApplications;
	}

	public void setClientApplications(
			List<IbosClientApplications> clientApplications) {
		this.clientApplications = clientApplications;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public List<InvoiceHistory> getInvoiceHistory() {
		return invoiceHistory;
	}

	public void setInvoiceHistory(List<InvoiceHistory> invoiceHistory) {
		this.invoiceHistory = invoiceHistory;
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

	public List<ClientApplication> getListCa() {
		return listCa;
	}

	public void setListCa(List<ClientApplication> listCa) {
		this.listCa = listCa;
	}

	public boolean isHasCa() {
		return hasCa;
	}

	public void setHasCa(boolean hasCa) {
		this.hasCa = hasCa;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Integer getTotalSubmit() {
		return totalSubmit;
	}

	public void setTotalSubmit(Integer totalSubmit) {
		this.totalSubmit = totalSubmit;
	}

	public Corporation getCorporation() {
		return corporation;
	}

	public void setCorporation(Corporation corporation) {
		this.corporation = corporation;
	}

	public OwnerType getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(OwnerType ownerType) {
		this.ownerType = ownerType;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Long getTotalIboCorporation() {
		return totalIboCorporation;
	}

	public void setTotalIboCorporation(Long totalIboCorporation) {
		this.totalIboCorporation = totalIboCorporation;
	}

	public String getIdNewIboPrincipal() {
		return idNewIboPrincipal;
	}

	public void setIdNewIboPrincipal(String idNewIboPrincipal) {
		this.idNewIboPrincipal = idNewIboPrincipal;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public PayType getPayType() {
		return payType;
	}

	public void setPayType(PayType payType) {
		this.payType = payType;
	}

	public ProfesionalSkills getProfesionalSkill() {
		return profesionalSkill;
	}

	public void setProfesionalSkill(ProfesionalSkills profesionalSkill) {
		this.profesionalSkill = profesionalSkill;
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public TypeContract getTypeContract() {
		return typeContract;
	}

	public void setTypeContract(TypeContract typeContract) {
		this.typeContract = typeContract;
	}

	@Override
	public String toString() {
		return (user != null ? user.toString() : " ") + " User ID: " + number;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	@Override
	public int compare(Object old) {
		Profile o = (Profile) old;

		if (type != null && o.getType() != null && !type.equals(o.getType()))
			fieldAdicional += " New type: " + type.toString() + " Old type: "
					+ o.getType().toString();

		if (group != null && o.getGroup() != null
				&& !group.equals(o.getGroup()))
			fieldAdicional += " New group: " + group.toString()
					+ " Old group: " + o.getGroup().toString();
		else if (getGroup() != null && o.getGroup() == null)
			fieldAdicional += " New group: " + group.toString()
					+ " Old group: Undefined";
		else if (getGroup() == null && o.getGroup() != null)
			fieldAdicional += " New group: Undefined" + " Old group: "
					+ o.getGroup().toString();

		if (princTitle != null && !princTitle.equals(o.getPrincTitle()))
			fieldAdicional += " New principal owner title: " + princTitle
					+ " Old principal owner title: " + o.getPrincTitle();

		if (region != null && !region.equals(o.getRegion()))
			fieldAdicional += " New region: " + region + " Old region: "
					+ o.getRegion();

		if (iboState != null && !iboState.equals(o.getIboState()))
			fieldAdicional += " New status: " + iboState + " Old status: "
					+ o.getIboState();

		if (!ownerType.equals(o.getOwnerType()))
			fieldAdicional += " New owner type: " + ownerType
					+ " Old owner type: " + o.getOwnerType();

		amount = amount == null ? 0 : amount;
		o.setAmount(o.getAmount() == null ? 0 : o.getAmount());

		if (!amount.equals(o.getAmount()))
			fieldAdicional += " New amount: " + amount + " Old amount: "
					+ o.getAmount();

		if (country != null && !country.equals(o.getCountry()))
			fieldAdicional += " New country: " + country + " Old country: "
					+ o.getCountry();

		List<ClientApplication> newList = new ArrayList<ClientApplication>();
		newList.addAll(listCa);
		newList.removeAll(o.getListCa());

		List<ClientApplication> removeList = new ArrayList<ClientApplication>();
		removeList.addAll(o.getListCa());
		removeList.removeAll(listCa);

		if (newList.size() > 0)
			fieldAdicional += " Added programs:" + newList.toString();
		if (removeList.size() > 0)
			fieldAdicional += "  Eliminated programs: " + removeList.toString();

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
		if (!(obj instanceof Profile))
			return false;

		Profile other = (Profile) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

}
