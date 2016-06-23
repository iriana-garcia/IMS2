package com.ghw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "profesional_skills")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProfesionalSkills implements IEntity {

	@Id
	@Column(name = "prs_id")
	private Integer id;

	@Column(name = "prs_name", nullable = false, unique = true)
	private String name;

	@OneToMany(mappedBy = "profesionalSkill", fetch = FetchType.LAZY)
	private List<Profile> profiles = new ArrayList<Profile>();

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

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
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
		if (!(obj instanceof ProfesionalSkills))
			return false;
		ProfesionalSkills other = (ProfesionalSkills) obj;
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

	public ProfesionalSkills() {
		super();
	}

	public ProfesionalSkills(Integer id) {
		super();
		this.id = id;
	}

	@Override
	public String getIdentity() {
		return id.toString();
	}

}
