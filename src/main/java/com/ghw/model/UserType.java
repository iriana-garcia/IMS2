package com.ghw.model;

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
@Table(name = "user_type")
public class UserType implements IEntity {

	@Id
	@Column(name = "typ_id", length = 1, columnDefinition = "CHAR(1)")
	private String id;

	@Column(name = "typ_name", nullable = false, unique = true)
	private String name;

	@OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
	private List<User> listUser = new ArrayList<User>();
	
	
	public static final String USER = "1";
	public static final String IBO = "2";
	public static final String PA = "3";

	public boolean isAnIbo() {
		return id != null && id.equals("2");
	}

	public boolean isAnPA() {
		return id != null && id.equals("3");
	}
	
	/**
	 * Constructors
	 */
	
	public UserType() {
		super();
	}

	public UserType(String id) {
		super();
		this.id = id;
	}

	public UserType(String id, String name) {
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

	public List<User> getListUser() {
		return listUser;
	}

	public void setListUser(List<User> listUser) {
		this.listUser = listUser;
	}

	

	@Override
	public String toString() {
		return "Name: " + name;
	}

	@Override
	public String getIdentity() {
		return id;
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
		if (!(obj instanceof UserType))
			return false;
		UserType other = (UserType) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
