package com.ghw.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "server_existing")
public class ServerExisting {

	@Id
	@Column(name = "se_id")
	private Integer id;

	@Column(name = "se_host")
	private String host;

	@Column(name = "se_priority")
	private Integer priority;

	@Column(name = "se_url_webservice")
	private String urlWebservice;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getUrlWebservice() {
		return urlWebservice;
	}

	public void setUrlWebservice(String urlWebservice) {
		this.urlWebservice = urlWebservice;
	}

}