package com.ghw.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import org.hibernate.annotations.Type;

@Entity
@Table(name = "client_applications")
public class ClientApplication implements IEntityEditable, Serializable {

	@Id
	@Column(name = "cli_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "cli_name", nullable = false, unique = true)
	private String name;

	@Column(name = "cli_amount")
	private Double amount = 0.0;

	@Column(name = "cli_state", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean state = true;

	@Column(name = "cli_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "cli_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@OneToMany(mappedBy = "clientApplication", fetch = FetchType.LAZY)
	private List<Skill> listSkill = new ArrayList<Skill>();

	@OneToMany(mappedBy = "clientApplication", fetch = FetchType.LAZY)
	private List<Event> events = new ArrayList<Event>();

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "clientApplication", fetch = FetchType.LAZY)
	private List<IbosClientApplications> ibos = new ArrayList<IbosClientApplications>();

	// used in list
	@Transient
	private Integer totalSkills = 0, totalIbo = 0, totalEvent = 0;

	@Transient
	private List<Profile> listIbo = new ArrayList<Profile>();

	// used to disabled state when modify
	@Transient
	private boolean iboAssociate;

	public ClientApplication() {
		super();
	}

	public ClientApplication(String id) {
		super();
		this.id = id;
	}

	public ClientApplication(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
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

	public List<Skill> getListSkill() {
		return listSkill;
	}

	public void setListSkill(List<Skill> listSkill) {
		this.listSkill = listSkill;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	@Transient
	private String fieldAdicional = "";

	@Override
	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	public Integer getTotalSkills() {
		return totalSkills;
	}

	public void setTotalSkills(Integer totalSkills) {
		this.totalSkills = totalSkills;
	}

	public Integer getTotalIbo() {
		return totalIbo;
	}

	public void setTotalIbo(Integer totalIbo) {
		this.totalIbo = totalIbo;
	}

	private String getStateDescription(boolean state) {
		return state ? "Active" : "Inactive";
	}

	public String getStateDescription() {
		return getStateDescription(state);
	}

	public boolean isIboAssociate() {
		return iboAssociate;
	}

	public void setIboAssociate(boolean iboAssociate) {
		this.iboAssociate = iboAssociate;
	}

	public List<IbosClientApplications> getIbos() {
		return ibos;
	}

	public void setIbos(List<IbosClientApplications> ibos) {
		this.ibos = ibos;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public String getDateCreatedFormat() {
		return (new SimpleDateFormat("E, M-d-yy h:mm a")).format(createdDate);
	}

	public String getDateUpdatedFormat() {
		return updateDate != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(updateDate) : "";
	}

	public List<Profile> getListIbo() {
		return listIbo;
	}

	public void setListIbo(List<Profile> listIbo) {
		this.listIbo = listIbo;
	}

	public Integer getTotalEvent() {
		return totalEvent;
	}

	public void setTotalEvent(Integer totalEvent) {
		this.totalEvent = totalEvent;
	}

	@Override
	public int compare(Object o2) {
		ClientApplication o = (ClientApplication) o2;

		if (!getName().equals(o.getName()))
			fieldAdicional += " Old name: " + o.getName();

		if (isState() != o.isState())
			fieldAdicional += " Old state: " + getStateDescription(o.isState());

		if (!getAmount().equals(o.getAmount()))
			fieldAdicional += " Old amount: " + o.getAmount();

		List<Skill> newSkills = new ArrayList<Skill>();
		newSkills.addAll(getListSkill());
		newSkills.removeAll(o.getListSkill());

		List<Skill> removeSkills = new ArrayList<Skill>();
		removeSkills.addAll(o.getListSkill());
		removeSkills.removeAll(getListSkill());

		if (newSkills.size() > 0)
			fieldAdicional += " Added skills: " + newSkills.toString();
		if (removeSkills.size() > 0)
			fieldAdicional += " Eliminated skills: " + removeSkills.toString();

		List<Event> newEvents = new ArrayList<Event>();
		newEvents.addAll(getEvents());
		newEvents.removeAll(o.getEvents());

		List<Event> removeEvent = new ArrayList<Event>();
		removeEvent.addAll(o.getEvents());
		removeEvent.removeAll(getEvents());

		if (newEvents.size() > 0)
			fieldAdicional += " Added events: " + newEvents.toString();
		if (removeEvent.size() > 0)
			fieldAdicional += " Eliminated events: " + removeEvent.toString();

		List<Profile> newIbos = new ArrayList<Profile>();
		newIbos.addAll(listIbo);
		newIbos.removeAll(o.getListIbo());

		List<Profile> removeIbos = new ArrayList<Profile>();
		removeIbos.addAll(o.getListIbo());
		removeIbos.removeAll(listIbo);

		if (newIbos.size() > 0)
			fieldAdicional += " Added Ibos: " + newIbos.toString();
		if (removeIbos.size() > 0)
			fieldAdicional += " Eliminated Ibos: " + removeIbos.toString();

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
	}

	@Override
	public String toString() {
		return "Name: " + name + " Amount: " + amount + " State: "
				+ getStateDescription(state);
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
		if (!(obj instanceof ClientApplication))
			return false;
		ClientApplication other = (ClientApplication) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}