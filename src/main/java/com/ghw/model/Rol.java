package com.ghw.model;

import java.io.Serializable;
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
@Table(name = "roles")
public class Rol implements Serializable, IEntityEditable {

	@Id
	@Column(name = "rol_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "rol_name", nullable = false, unique = true)
	private String name;

	@Column(name = "rol_description")
	private String descripcion;

	@Column(name = "rol_state", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean state = true;

	@Column(name = "rol_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", nullable = false, updatable = false)
	private User userCreated;

	@Column(name = "rol_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
	private List<User> users = new ArrayList<User>();

	@OneToMany(mappedBy = "rol", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private List<RolPermission> permissions = new ArrayList<RolPermission>();

	/* Attributes comodin */
	@Transient
	private boolean userAssociate;

	@Transient
	private String fieldAdicional = "";


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<RolPermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<RolPermission> permissions) {
		this.permissions = permissions;
	}

	public Rol(String id) {
		super();
		this.id = id;
	}
	
	

	public Rol(String id, String name) {
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

	public Rol() {
		super();
	}

	@Override
	public String toString() {
		return "Name: " + name + " State: " + getStateDescription(state);
	}

	public String getStateDescription() {
		return getStateDescription(state);
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

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isUserAssociate() {
		return userAssociate;
	}

	public void setUserAssociate(boolean userAssociate) {
		this.userAssociate = userAssociate;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	@Override
	public int compare(Object o2) {

		Rol o = (Rol) o2;
		fieldAdicional = "";
		if (!getName().equals(o.getName()))
			fieldAdicional += " Old name: " + o.getName();

		if (isState() != o.isState())
			fieldAdicional += " Old state: " + getStateDescription(o.isState());

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
		if (!(obj instanceof Rol))
			return false;
		Rol other = (Rol) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
