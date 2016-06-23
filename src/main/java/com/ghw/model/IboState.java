package com.ghw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ibo_state")
public class IboState {

	@Id
	@Column(name = "ist_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "ist_name", nullable = false, unique = true)
	private String name;

	@OneToMany(mappedBy = "iboState", fetch = FetchType.LAZY)
	private List<Profile> listIbo = new ArrayList<Profile>();

	public static final String EMAIL_SENT = "1";
	public static final String EMAIL_ERROR = "2";
	public static final String NEW = "3";
	public static final String OK = "4";
	public static final String PENDING = "5";

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

	public List<Profile> getListIbo() {
		return listIbo;
	}

	public void setListIbo(List<Profile> listIbo) {
		this.listIbo = listIbo;
	}

	public IboState() {
		super();
	}

	public IboState(String id) {
		super();
		this.id = id;
	}

	public IboState(String id, String name) {
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
		if (!(obj instanceof IboState))
			return false;
		IboState other = (IboState) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Name: " + name;
	}

}
