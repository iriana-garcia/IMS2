package com.ghw.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "log_system")
public class LogSystem implements Serializable, IEntity {

	@Id
	@Column(name = "log_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "log_date")
	private Date date = new Date();

	@Column(name = "log_class")
	private String className;

	@Column(name = "log_method")
	private String method;

	@Column(name = "log_detail")
	private String detail;

	@Column(name = "log_ip")
	private String ip;

	@Column(name = "log_error", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean error = false;

	@Column(name = "log_class_id")
	private String classId;

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "use_id", nullable = true)
	private User user;

	@Transient
	private String userName;

	@Column(name = "log_processed", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean processed = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public boolean getError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public String getErrorDescription() {

		return error ? "Yes" : "No";
	}

	public LogSystem() {
		super();
	}

	public LogSystem(String className, String method, String detail, String ip,
			boolean error, String classId, User user) {
		super();
		this.className = className;
		this.method = method;
		this.detail = detail;
		this.ip = ip;
		this.error = error;
		this.classId = classId;
		this.user = user;
	}

	public LogSystem(String className, String method, String detail, String ip,
			boolean error, String classId, User user, boolean processed) {
		super();
		this.className = className;
		this.method = method;
		this.detail = detail;
		this.ip = ip;
		this.error = error;
		this.classId = classId;
		this.user = user;
		this.processed = processed;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDateFormat() {
		return (new SimpleDateFormat("E, M-d-yy h:mm a")).format(date);
	}

	@Override
	public String getIdentity() {
		return getId().toString();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isCutOff() {
		return StringUtils.isNotBlank(detail) && detail.length() >= 500;
	}

	public String getDetailTable() {
		return isCutOff() ? detail.substring(0, 450) + " ..." : detail;
	}
}
