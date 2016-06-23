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
@Table(name = "groups")
public class Groups implements IEntityEditable, Serializable {

	@Id
	@Column(name = "grp_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "grp_name", nullable = false, unique = true)
	private String name;

	@Column(name = "grp_state", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean state = true;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id")
	private User user;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private List<Profile> profile = new ArrayList<Profile>();

	@Column(name = "grp_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "grp_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@Transient
	private String userName;

	@Transient
	private String userId;

	@Transient
	private String fieldAdicional = "";

	@Transient
	private Integer totalIbo = 0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Profile> getProfile() {
		return profile;
	}

	public void setProfile(List<Profile> profile) {
		this.profile = profile;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	public Integer getTotalIbo() {
		return totalIbo;
	}

	public void setTotalIbo(Integer totalIbo) {
		this.totalIbo = totalIbo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	private String getStateDescription(boolean state) {
		return state ? "Active" : "Inactive";
	}

	public String getStateDescription() {
		return getStateDescription(state);
	}

	@Override
	public int compare(Object oldObj) {
		Groups o = (Groups) oldObj;

		if (!getName().equals(o.getName()))
			fieldAdicional += " Old name: " + o.getName();

		if (isState() != o.isState())
			fieldAdicional += " Old state: " + getStateDescription(o.isState());

		if (getUser() != null && o.getUser() != null
				&& !getUser().equals(o.getUser()))
			fieldAdicional += " New user: " + user.toString() + " Old user: "
					+ o.getUser().toString();
		else if (getUser() != null && o.getUser() == null)
			fieldAdicional += " New user: " + user.toString()
					+ " Old user: Undefined";
		else if (getUser() == null && o.getUser() != null)
			fieldAdicional += " New user: Undefined  Old user: "
					+ o.getUser().toString();

		// CompareList<Profile> p = new CompareList<Profile>();
		// List<Profile> newList = p.excludeInAAllB(profile, o.getProfile());
		// List<Profile> removeList = p.excludeInAAllB(o.getProfile(), profile);

		List<Profile> newList = new ArrayList<Profile>();
		newList.addAll(profile);
		newList.removeAll(o.getProfile());

		List<Profile> removeList = new ArrayList<Profile>();
		removeList.addAll(o.getProfile());
		removeList.removeAll(profile);

		if (newList.size() > 0)
			fieldAdicional += " Added IBOs:" + newList.toString();
		if (removeList.size() > 0)
			fieldAdicional += "  Eliminated IBOs: " + removeList.toString();

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
	}

	@Override
	public String toString() {
		return "Name: " + name + " State: " + getStateDescription(state);
		// + " User: "
		// + (user == null || StringUtils.isNotBlank(user.getId()) ? "Undefined"
		// : user.toString());
	}

	public String getDateCreatedFormat() {
		return (new SimpleDateFormat("E, M-d-yy h:mm a")).format(createdDate);
	}

	public String getDateUpdatedFormat() {
		return updateDate != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(updateDate) : "";
	}

	public Groups() {
		super();
	}

	public Groups(String id) {
		super();
		this.id = id;
	}
	
	public Groups(String id, String name) {
		super();
		this.id = id;
		this.name = name;
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
		if (!(obj instanceof Groups))
			return false;
		Groups other = (Groups) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
