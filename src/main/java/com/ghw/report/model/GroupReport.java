package com.ghw.report.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ghw.model.IEntity;

@Entity
@Table(name = "report_group")
public class GroupReport implements Serializable, IEntity {

	@Id
	@Column(name = "gro_id")
	private String id;

	@Column(name = "gro_description")
	private String description;

	@Column(name = "gro_class_service")
	private String classService;

	@Column(name = "gro_filtro_bean")
	private String filterBean;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private List<Report> reports;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClassService() {
		return classService;
	}

	public void setClassService(String classService) {
		this.classService = classService;
	}

	public String getFilterBean() {
		return filterBean;
	}

	public void setFilterBean(String filterBean) {
		this.filterBean = filterBean;
	}

	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	@Override
	public String getIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

}
