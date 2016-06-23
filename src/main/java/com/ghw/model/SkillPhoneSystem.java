package com.ghw.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "skill_phone_system")
public class SkillPhoneSystem implements Serializable, IEntity, IEntityEditable {

	@Id
	@Column(name = "sps_id", length = 36, columnDefinition = "CHAR(36)")
	private String id;

	@Column(name = "sps_name")
	private String name;

	@Column(name = "sps_place")
	private String place;

	@Column(name = "sps_phone_system")
	private Integer phoneSystemId;

	@Column(name = "sps_state", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean state = true;

	@Column(name = "sps_type")
	private Short type;

	@ManyToOne(targetEntity = Skill.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "ski_id")
	private Skill skill;

	@Column(name = "sps_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@Transient
	private String fieldAdicional = "";

	public SkillPhoneSystem() {
		super();
	}

	public SkillPhoneSystem(String id) {
		super();
		this.id = id;
	}

	public SkillPhoneSystem(String id, String name, String idSkill,
			String place, Integer phoneSystemId) {
		super();
		this.id = id;
		this.name = name;
		this.skill = new Skill(idSkill);
		this.place = place;
		this.phoneSystemId = phoneSystemId;
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

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Integer getPhoneSystemId() {
		return phoneSystemId;
	}

	public void setPhoneSystemId(Integer phoneSystemId) {
		this.phoneSystemId = phoneSystemId;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
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

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	public String getTypeName() {
		return TypeSkill.getTypeName(type);
	}

	@Override
	public String toString() {
		return "Name: " + name + " Place: " + place;
	}
	
	private String getStateDescription(boolean state) {
		return state ? "Active" : "Inactive";
	}

	public String getStateDescription() {
		return getStateDescription(state);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((phoneSystemId == null) ? 0 : phoneSystemId.hashCode());
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkillPhoneSystem other = (SkillPhoneSystem) obj;

		if (phoneSystemId == null) {
			if (other.phoneSystemId != null)
				return false;
		} else if (!phoneSystemId.equals(other.phoneSystemId))
			return false;
		if (place == null) {
			if (other.place != null)
				return false;
		} else if (!place.equals(other.place))
			return false;
		return true;
	}

	@Override
	public void setCreatedDate(Date date) {

	}

	@Override
	public void setUserCreated(User user) {
	}

	@Override
	public int compare(Object oldObj) {
		SkillPhoneSystem o = (SkillPhoneSystem) oldObj;

		if (!getType().equals(o.getType()))
			fieldAdicional += " Old type: " + o.getTypeName();

		if (skill != null && o.getSkill() != null
				&& !skill.equals(o.getSkill()))
			fieldAdicional += " New CA: " + skill.toString() + " Old CA: "
					+ o.getSkill().toString();
		else if (skill != null && o.getSkill() == null)
			fieldAdicional += " New CA: " + skill.toString()
					+ " Old CA: Undefined";
		else if (skill == null && o.getSkill() != null)
			fieldAdicional += " New CA: Undefined" + " Old CA: "
					+ o.getSkill().toString();

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;

	}

	@Override
	public String getIdentity() {
		return id;
	}

}
