package com.ghw.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Contains all the reason code that we pay when the IBO has an event in Not
 * Ready
 * 
 * @author ifernandez
 * 
 */
@Entity
@Table(name = "reason_code_pay")
public class ReasonCodePay {

	@Id
	@Column(name = "rc_id")
	private String id;

	@Column(name = "rc_place")
	private Short place = 1;

	@Column(name = "rc_code")
	private Integer code;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Short getPlace() {
		return place;
	}

	public void setPlace(Short place) {
		this.place = place;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

}