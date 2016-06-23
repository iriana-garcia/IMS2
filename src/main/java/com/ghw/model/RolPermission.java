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

@Entity
@Table(name = "roles_permissions")
public class RolPermission implements Serializable, IEntity {

	@Id
	@Column(name = "rop_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@ManyToOne(targetEntity = Rol.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "rol_id", nullable = false)
	private Rol rol;

	@ManyToOne(targetEntity = Permission.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "per_id", nullable = false)
	private Permission permission;

	@Column(name = "rop_access")
	private String access;

	@Transient
	private boolean accessAdd = false, modify = false, add = false,
			changeState = false, delete = false;

	public boolean isAccessAdd() {
		return accessAdd;
	}

	public void setAccessAdd(boolean accessAdd) {
		this.accessAdd = accessAdd;
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	public boolean isAdd() {
		return add;
	}

	public void setAdd(boolean add) {
		this.add = add;
	}

	public boolean isChangeState() {
		return changeState;
	}

	public void setChangeState(boolean changeState) {
		this.changeState = changeState;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public RolPermission() {
		super();
	}

	public RolPermission(Permission permission) {
		super();
		this.permission = permission;
	}

	public void analizeAccess() {
		if (StringUtils.isNotEmpty(access)) {
			accessAdd= access.contains("R");
			add = access.contains("A");
			modify = access.contains("M");
			changeState = access.contains("C");
			delete = access.contains("D");
		}
	}

	@Override
	public String toString() {
		return "Permission: " + permission.getName() + " Access: "
				+ getAccess();
	}

	@Override
	public String getIdentity() {
		return id.toString();
	}

}
