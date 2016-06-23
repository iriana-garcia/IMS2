package com.ghw.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "event")
public class Event {

	@Id
	@Column(name = "eve_id", length = 36, columnDefinition = "CHAR(36)")
	private String id;

	@Column(name = "eve_name", nullable = false, unique = true)
	private String name;

	@Column(name = "eve_id_pipkins", nullable = false)
	private String idPipkins;

	@ManyToOne(cascade = { CascadeType.ALL }, targetEntity = ClientApplication.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_id", nullable = true)
	private ClientApplication clientApplication;

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

	public String getIdPipkins() {
		return idPipkins;
	}

	public void setIdPipkins(String idPipkins) {
		this.idPipkins = idPipkins;
	}

	public ClientApplication getClientApplication() {
		return clientApplication;
	}

	public void setClientApplication(ClientApplication clientApplication) {
		this.clientApplication = clientApplication;
	}

	@Override
	public String toString() {
		return "Name: " + name;
	}	

	public Event() {
		super();
	}

	public Event(String id) {
		super();
		this.id = id;
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
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}
