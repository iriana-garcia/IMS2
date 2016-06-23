package com.ghw.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import org.hibernate.annotations.Type;

@Entity
@Table(name = "skill")
public class Skill implements Serializable, IEntity, IEntityEditable {

	@Id
	@Column(name = "ski_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	// @Column(name = "ski_id_pip", nullable = false, unique = true)
	// private String idPip;

	@Column(name = "ski_name", nullable = false, unique = true)
	private String name;

	@Column(name = "ski_state", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean state = true;

	@ManyToOne(targetEntity = ClientApplication.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_id", nullable = true)
	private ClientApplication clientApplication;

	@Column(name = "ski_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@Column(name = "ski_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@OneToMany(mappedBy = "skill", fetch = FetchType.LAZY)
	private List<SkillPhoneSystem> skillPhoneSystems = new ArrayList<SkillPhoneSystem>();

	@Transient
	private String fieldAdicional = "";

	@Transient
	private Integer totalSkills = 0;

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

	public ClientApplication getClientApplication() {
		return clientApplication;
	}

	public void setClientApplication(ClientApplication clientApplication) {
		this.clientApplication = clientApplication;
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

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
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

	@Override
	public String toString() {
		return "Name: " + name + " State: " + getStateDescription(state);
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
		if (!(obj instanceof Skill))
			return false;
		Skill other = (Skill) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public Skill() {
		super();
	}

	public Skill(String id) {
		super();
		this.id = id;
	}

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	public String getStateDescription() {
		return getStateDescription(state);
	}

	private String getStateDescription(boolean state) {
		return state ? "Active" : "Inactive";
	}

	@Override
	public int compare(Object oldObj) {
		Skill o = (Skill) oldObj;

		if (!getName().equals(o.getName()))
			fieldAdicional += " Old name: " + o.getName();

		if (isState() != o.isState())
			fieldAdicional += " Old state: " + getStateDescription(o.isState());

		if (clientApplication != null && o.getClientApplication() != null
				&& !clientApplication.equals(o.getClientApplication()))
			fieldAdicional += " New Program: " + clientApplication.toString()
					+ " Old Program: " + o.getClientApplication().toString();
		else if (clientApplication != null && o.getClientApplication() == null)
			fieldAdicional += " New Program: " + clientApplication.toString()
					+ " Old Program: Undefined";
		else if (clientApplication == null && o.getClientApplication() != null)
			fieldAdicional += " New Program: Undefined" + " Old Program: "
					+ o.getClientApplication().toString();

		List<SkillPhoneSystem> newSkills = new ArrayList<SkillPhoneSystem>();
		newSkills.addAll(skillPhoneSystems);
		newSkills.removeAll(o.getSkillPhoneSystems());

		List<SkillPhoneSystem> removeSkills = new ArrayList<SkillPhoneSystem>();
		removeSkills.addAll(o.getSkillPhoneSystems());
		removeSkills.removeAll(getSkillPhoneSystems());

		if (newSkills.size() > 0)
			fieldAdicional += " Added skills: " + newSkills.toString();
		if (removeSkills.size() > 0)
			fieldAdicional += " Eliminated skills: " + removeSkills.toString();

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
	}

	@Override
	public String getIdentity() {
		return id;
	}		

	public String getDateCreatedFormat() {
		return createdDate != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(createdDate) : "";
	}

	public String getDateUpdatedFormat() {
		return updateDate != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(updateDate) : "";
	}

	public List<SkillPhoneSystem> getSkillPhoneSystems() {
		return skillPhoneSystems;
	}

	public void setSkillPhoneSystems(List<SkillPhoneSystem> skillPhoneSystems) {
		this.skillPhoneSystems = skillPhoneSystems;
	}

	public Integer getTotalSkills() {
		return totalSkills;
	}

	public void setTotalSkills(Integer totalSkills) {
		this.totalSkills = totalSkills;
	}

}
