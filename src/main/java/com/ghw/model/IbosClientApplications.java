package com.ghw.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ibos_client_applications")
public class IbosClientApplications {

	@Id
	@Column(name = "icl_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@ManyToOne(targetEntity = Profile.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_id", nullable = false)
	private Profile user;

	@ManyToOne(targetEntity = ClientApplication.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_id", nullable = false)
	private ClientApplication clientApplication;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Profile getUser() {
		return user;
	}

	public void setUser(Profile user) {
		this.user = user;
	}

	public ClientApplication getClientApplication() {
		return clientApplication;
	}

	public void setClientApplication(ClientApplication clientApplication) {
		this.clientApplication = clientApplication;
	}

}
