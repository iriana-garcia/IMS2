package com.ghw.report.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ghw.model.IEntity;

@Entity
@Table(name = "report")
public class Report implements Serializable, IEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "rep_id")
	private String id;

	@Column(name = "rep_name")
	private String name;

	@Column(name = "rep_description")
	private String description;

	@Column(name = "rep_method")
	private String method;

	@Column(name = "rep_defaults")
	private Integer typeDefault;

	@Column(name = "rep_jrxml")
	private String jrxml;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = GroupReport.class)
	@JoinColumn(name = "gro_id", nullable = false)
	private GroupReport group;

	@Transient
	private String title;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Integer getTypeDefault() {
		return typeDefault;
	}

	public void setTypeDefault(Integer typeDefault) {
		this.typeDefault = typeDefault;
	}

	public String getJrxml() {
		return jrxml;
	}

	public void setJrxml(String jrxml) {
		this.jrxml = jrxml;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public GroupReport getGroup() {
		return group;
	}

	public void setGroup(GroupReport group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "Id: " + id + " Name: " + name + " File's name: " + jrxml;
	}

	@Override
	public String getIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

}
