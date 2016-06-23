package com.ghw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "permissions")
public class Permission implements Serializable, IEntity {

	@Id
	@Column(name = "per_id")
	private Integer id;

	@Column(name = "per_name", nullable = false, unique = true, updatable = false)
	private String name;

	@Column(name = "per_description", updatable = false)
	private String descripcion;

	@Column(name = "per_const", nullable = false, unique = true, updatable = false)
	private String constSpring;

	@OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
	private List<RolPermission> rolPermissions = new ArrayList<RolPermission>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getConstSpring() {
		return constSpring;
	}

	public void setConstSpring(String constSpring) {
		this.constSpring = constSpring;
	}

	public List<RolPermission> getRolPermissions() {
		return rolPermissions;
	}

	public void setRolPermissions(List<RolPermission> rolPermissions) {
		this.rolPermissions = rolPermissions;
	}

	public Permission() {
		super();
	}

	public Permission(Integer id, String name, String descripcion) {
		super();
		this.id = id;
		this.name = name;
		this.descripcion = descripcion;
	}

	public Permission(Integer id) {
		super();
		this.id = id;
	}

	@Override
	public String toString() {

		return "Name: " + name;
	}

	@Override
	public String getIdentity() {
		return id.toString();
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
		if (!(obj instanceof Permission))
			return false;
		Permission other = (Permission) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
