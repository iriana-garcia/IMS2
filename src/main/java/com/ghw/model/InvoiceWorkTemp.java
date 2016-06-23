package com.ghw.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "roles")
public class InvoiceWorkTemp {

	@Id
	@Column(name = "iwt_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "skill_id")
	private String skillId;

	@Column(name = "skill_name")
	private String skillName;

	@Column(name = "iwt_login")
	private Date login;

	@Column(name = "iwt_logout")
	private Date logout;

	@Column(name = "iwt_sch_start_time")
	private Date schStartTime;

	@Column(name = "iwt_sch_end_time")
	private Date schEndTime;

	@Column(name = "iwt_no_show")
	private Integer noShow;

	@Column(name = "iwt_non_posted")
	private Integer nonPosted;

	@Column(name = "iwt_acw_time")
	private Integer acw;

	@Column(name = "iwt_total_unavailable_time")
	private Integer totalUnavailableTime;

	@Column(name = "iwt_total_not_ready_time")
	private Integer totalNotReadyTime;

	@Column(name = "iwt_time_in_other_rc")
	private Integer timeInOtherRc;

	@Column(name = "iwt_ibo_id")
	private String iboId;

	@Column(name = "iwt_first_name")
	private String firstName;

	@Column(name = "iwt_last_name")
	private String lastName;

	@Column(name = "iwt_agent_id")
	private String agentId;

	@Column(name = "iwt_email")
	private String email;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Date getLogin() {
		return login;
	}

	public void setLogin(Date login) {
		this.login = login;
	}

	public Date getLogout() {
		return logout;
	}

	public void setLogout(Date logout) {
		this.logout = logout;
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

	public Integer getNoShow() {
		return noShow;
	}

	public void setNoShow(Integer noShow) {
		this.noShow = noShow;
	}

	public Integer getNonPosted() {
		return nonPosted;
	}

	public void setNonPosted(Integer nonPosted) {
		this.nonPosted = nonPosted;
	}

	public Integer getAcw() {
		return acw;
	}

	public void setAcw(Integer acw) {
		this.acw = acw;
	}

	public Integer getTotalUnavailableTime() {
		return totalUnavailableTime;
	}

	public void setTotalUnavailableTime(Integer totalUnavailableTime) {
		this.totalUnavailableTime = totalUnavailableTime;
	}

	public Integer getTotalNotReadyTime() {
		return totalNotReadyTime;
	}

	public void setTotalNotReadyTime(Integer totalNotReadyTime) {
		this.totalNotReadyTime = totalNotReadyTime;
	}

	public Integer getTimeInOtherRc() {
		return timeInOtherRc;
	}

	public void setTimeInOtherRc(Integer timeInOtherRc) {
		this.timeInOtherRc = timeInOtherRc;
	}

	public String getIboId() {
		return iboId;
	}

	public void setIboId(String iboId) {
		this.iboId = iboId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}