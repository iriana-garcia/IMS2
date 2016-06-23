package com.ghw.model;

import java.io.Serializable;
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
@Table(name = "configuration_general")
public class ConfigurationGeneral implements IEntityEditable, Serializable {

	@Id
	@Column(name = "con_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "con_ldap_authenti")
	private String ldapAuthenti;

	@Column(name = "con_ldap_server")
	private String ldapServer;

	@Column(name = "con_ldap_port")
	private String ldapPort;

	@Column(name = "con_ldap_dn")
	private String ldapDn;

	@Column(name = "con_ldap_dn_ibo")
	private String ldapDnIbo;

	@Column(name = "con_ldap_dn_idc")
	private String ldapDnIdc;
	
	@Column(name = "con_ldap_dn_inter")
	private String ldapDnInternational;

	@Column(name = "con_ldap_dn_sme")
	private String ldapDnSme;

	@Column(name = "con_ldap_user")
	private String ldapUser;

	@Column(name = "con_ldap_pass")
	@Type(type = "encryptedString")
	private String ldapPass;

	@Column(name = "con_ldap_sec_type")
	private String ldapSecType = "simple";

	@Column(name = "con_ldap_ssl", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean ldapSsl;

	@Column(name = "con_last_pipkins")
	private Date lastPipkins;

	@Column(name = "con_path_pipkins")
	private String pathPipkins;

	@Column(name = "con_path_oracle")
	private String pathOracle;

	@Column(name = "con_ftp_port")
	private String portOracle;

	@Column(name = "con_ftp_user")
	private String userOracle;

	@Column(name = "con_ftp_password")
	@Type(type = "encryptedString")
	private String passwordOracle;

	@Column(name = "con_retention")
	private Integer retention;

	@Column(name = "con_email_host")
	private String emailHost;

	@Column(name = "con_email_port")
	private Integer emailPort;

	@Column(name = "con_email_protocol")
	private String emailProtocol = "smtp";

	@Column(name = "con_email_from")
	private String emailFrom;

	@Column(name = "con_email_system")
	private String emailSystem;

	@Column(name = "con_email_auth")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean emailAuth = false;

	@Column(name = "con_email_user")
	private String emailUser;

	@Column(name = "con_email_pass")
	@Type(type = "encryptedString")
	private String emailPass;

	@Column(name = "con_email_max_size")
	private Integer emailMaxSize;

	@Column(name = "con_update_date")
	private Date updateDate;

	@Column(name = "con_webservice")
	private String webservice;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLdapAuthenti() {
		return ldapAuthenti;
	}

	public void setLdapAuthenti(String ldapAuthenti) {
		this.ldapAuthenti = ldapAuthenti;
	}

	public String getLdapServer() {
		return ldapServer;
	}

	public void setLdapServer(String ldapServer) {
		this.ldapServer = ldapServer;
	}

	public String getLdapPort() {
		return ldapPort;
	}

	public void setLdapPort(String ldapPort) {
		this.ldapPort = ldapPort;
	}

	public String getLdapDn() {
		return ldapDn;
	}

	public void setLdapDn(String ldapDn) {
		this.ldapDn = ldapDn;
	}

	public String getLdapUser() {
		return ldapUser;
	}

	public void setLdapUser(String ldapUser) {
		this.ldapUser = ldapUser;
	}

	public String getLdapPass() {
		return ldapPass;
	}

	public void setLdapPass(String ldapPass) {
		this.ldapPass = ldapPass;
	}

	public String getLdapSecType() {
		return ldapSecType;
	}

	public void setLdapSecType(String ldapSecType) {
		this.ldapSecType = ldapSecType;
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

	public boolean isLdapSsl() {
		return ldapSsl;
	}

	public void setLdapSsl(boolean ldapSsl) {
		this.ldapSsl = ldapSsl;
	}

	public String getLdapDnIbo() {
		return ldapDnIbo;
	}

	public void setLdapDnIbo(String ldapDnIbo) {
		this.ldapDnIbo = ldapDnIbo;
	}

	public Date getLastPipkins() {
		return lastPipkins;
	}

	public void setLastPipkins(Date lastPipkins) {
		this.lastPipkins = lastPipkins;
	}

	public String getPathPipkins() {
		return pathPipkins;
	}

	public void setPathPipkins(String pathPipkins) {
		this.pathPipkins = pathPipkins;
	}

	public String getPathOracle() {
		return pathOracle;
	}

	public void setPathOracle(String pathOracle) {
		this.pathOracle = pathOracle;
	}

	public Integer getRetention() {
		return retention;
	}

	public void setRetention(Integer retention) {
		this.retention = retention;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public Integer getEmailPort() {
		return emailPort;
	}

	public void setEmailPort(Integer emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailProtocol() {
		return emailProtocol;
	}

	public void setEmailProtocol(String emailProtocol) {
		this.emailProtocol = emailProtocol;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public boolean isEmailAuth() {
		return emailAuth;
	}

	public void setEmailAuth(boolean emailAuth) {
		this.emailAuth = emailAuth;
	}

	public Integer getEmailMaxSize() {
		return emailMaxSize;
	}

	public void setEmailMaxSize(Integer emailMaxSize) {
		this.emailMaxSize = emailMaxSize;
	}

	public String getLdapDnSme() {
		return ldapDnSme;
	}

	public void setLdapDnSme(String ldapDnSme) {
		this.ldapDnSme = ldapDnSme;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	public String getEmailPass() {
		return emailPass;
	}

	public void setEmailPass(String emailPass) {
		this.emailPass = emailPass;
	}

	public String getEmailSystem() {
		return emailSystem;
	}

	public void setEmailSystem(String emailSystem) {
		this.emailSystem = emailSystem;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public String getIdentity() {
		return getId();
	}

	@Override
	public String getFieldAdicional() {
		return "";
	}

	@Override
	public int compare(Object o2) {
		return 0;
	}

	public String getPortOracle() {
		return portOracle;
	}

	public void setPortOracle(String portOracle) {
		this.portOracle = portOracle;
	}

	public String getUserOracle() {
		return userOracle;
	}

	public void setUserOracle(String userOracle) {
		this.userOracle = userOracle;
	}

	public String getPasswordOracle() {
		return passwordOracle;
	}

	public void setPasswordOracle(String passwordOracle) {
		this.passwordOracle = passwordOracle;
	}

	public String getWebservice() {
		return webservice;
	}

	public void setWebservice(String webservice) {
		this.webservice = webservice;
	}

	public String getLdapDnIdc() {
		return ldapDnIdc;
	}

	public void setLdapDnIdc(String ldapDnIdc) {
		this.ldapDnIdc = ldapDnIdc;
	}
	
	

	public String getLdapDnInternational() {
		return ldapDnInternational;
	}

	public void setLdapDnInternational(String ldapDnInternational) {
		this.ldapDnInternational = ldapDnInternational;
	}

	@Override
	public void setCreatedDate(Date date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUserCreated(User user) {
		// TODO Auto-generated method stub

	}

}
