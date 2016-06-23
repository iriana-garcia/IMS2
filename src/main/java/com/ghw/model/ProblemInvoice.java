package com.ghw.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "invoice_problem")
@Immutable
public class ProblemInvoice implements Serializable, IEntity {

	@Id
	private String id;

	@Column(name = "pro_number")
	private String iboNumber;

	@Column(name = "use_first_name")
	private String firstName;

	@Column(name = "use_middle_name")
	private String middleName;

	@Column(name = "use_last_name")
	private String lastName;

	@Column(name = "inv_number")
	private String invoiceNumber;

	@Column(name = "iwo_sch_start_time")
	private Date schStartTime;

	@Column(name = "iwo_sch_end_time")
	private Date schEndTime;

	@Column(name = "cli_id")
	private String cliId;

	@Column(name = "cli_name")
	private String cliName;

	@Column(name = "skill_id")
	private String skillId;

	@Column(name = "skill_name")
	private String skillName;

	@Column(name = "problem_id")
	private String problemId;

	@Column(name = "use_id")
	private String userId;

	@Column(name = "inv_id")
	private String invId;

	@Transient
	private User user;

	@Transient
	private String fieldAdicional;

	public String getProblemDescription() {
		return ProblemId.valueOf(problemId).getValor();
	}

	public String getDetail() {

		String detail = "";

		if (StringUtils.isNotBlank(invoiceNumber))
			detail += " Invoice Number: " + invoiceNumber;

		if (StringUtils.isNotBlank(cliName))
			detail += " Program: " + cliName;

		detail += " Event: " + skillName;

		return detail;
	}

	public String getDetailInvoice() {

		String detail = "";
		if (StringUtils.isNotBlank(cliName))
			detail += " Program: " + cliName;

		detail += " Event: " + skillName;

		return detail;
	}

	public String getSchStartTimeFormat() {
		return schStartTime != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(schStartTime) : null;
	}

	public String getSchEndTimeFormat() {
		return schEndTime != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(schEndTime) : "";
	}

	public String getIboNumber() {
		return iboNumber;
	}

	public void setIboNumber(String iboNumber) {
		this.iboNumber = iboNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getSchStartTime() {
		return schStartTime;
	}

	public void setSchStartTime(Date schStartTime) {
		this.schStartTime = schStartTime;
	}

	public Date getSchEndTime() {
		return schEndTime;
	}

	public void setSchEndTime(Date schEndTime) {
		this.schEndTime = schEndTime;
	}

	public String getCliId() {
		return cliId;
	}

	public void setCliId(String cliId) {
		this.cliId = cliId;
	}

	public String getCliName() {
		return cliName;
	}

	public void setCliName(String cliName) {
		this.cliName = cliName;
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public String getProblemId() {
		return problemId;
	}

	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInvId() {
		return invId;
	}

	public void setInvId(String invId) {
		this.invId = invId;
	}

	@Override
	public String toString() {
		return " Start date: " + getSchStartTimeFormat() + " End date: "
				+ getSchEndTimeFormat() + " User ID: " + iboNumber
				+ " First Name: " + firstName + " Last Name: " + lastName
				+ " Issue: " + getProblemDescription() + " Detail: "
				+ getDetail();
	}

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

}
