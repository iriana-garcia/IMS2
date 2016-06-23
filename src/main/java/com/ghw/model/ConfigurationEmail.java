package com.ghw.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "configuration_email")
public class ConfigurationEmail implements IEntityEditable {

	@Id
	@Column(name = "ema_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "ema_type", nullable = false)
	private String type = "W";

	@Column(name = "ema_subject", nullable = false)
	private String subject;

	@Column(name = "ema_bcc", nullable = true)
	private String bcc;

	@Column(name = "ema_html")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean html = false;

	@Column(name = "ema_content")
	private String content;

	@Column(name = "ema_to")
	private String to;

	@Column(name = "ema_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public boolean getHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public User getUserUpdated() {
		return userUpdated;
	}

	public void setUserUpdated(User userUpdated) {
		this.userUpdated = userUpdated;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	@Override
	public String getFieldAdicional() {
		return "";
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public int compare(Object oldObj) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public void setCreatedDate(Date date) {
	}

	@Override
	public void setUserCreated(User user) {
		
	}

	

}
