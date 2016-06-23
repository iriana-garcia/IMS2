package com.ghw.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "users")
public class User implements Serializable, IEntityEditable {

	@Id
	@Column(name = "use_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "use_name", nullable = false, unique = true)
	private String name;

	@Column(name = "use_first_name", nullable = false)
	private String firstName;

	@Column(name = "use_middle_name")
	private String middleName;

	@Column(name = "use_last_name", nullable = false)
	private String lastName;

	@Column(name = "use_email", nullable = true)
	private String email;

	@Column(name = "use_state", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean state = true;

	@Column(name = "use_super", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean superAdmin = false;

	@Column(name = "use_phone")
	private String phone;

	@Column(name = "use_password")
	@Type(type = "encryptedString")
	private String password;

	@Column(name = "use_created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "use_update_date")
	private Date updateDate;

	@Column(name = "use_last_login", nullable = true)
	private Date lastLogin;

	@Column(name = "use_date_state")
	private Date stateDate;

	@Column(name = "use_need_update")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean needUpdate = false;

	@ManyToOne(targetEntity = Rol.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "rol_id", nullable = true)
	private Rol rol;

	@ManyToOne(targetEntity = UserType.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "typ_id", nullable = true)
	private UserType type;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_created", updatable = false)
	private User userCreated;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Profile> profile = new ArrayList<Profile>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Groups> groups = new ArrayList<Groups>();

	@Transient
	private Profile ibo = new Profile();

	@Transient
	private List<String> permissions = new ArrayList<String>();

	@Transient
	// save if the user has bank account in system, used in login to redirect to
	// defaul page or bank account page, only matter for the Domestic
	private boolean goToBank = false;

	@Transient
	private String ip, groupName, rolName, distinguishedName, typeName,
			corporationName, userId;

	@Transient
	private String problemLogin = null;

	@Transient
	private String fieldAdicional = "";

	public String getLastLoginFormat() {
		return lastLogin != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(lastLogin) : "";
	}

	public String getUpdateDateFormat() {
		return updateDate != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(updateDate) : "";
	}

	public String getCreatedDateFormat() {
		return createdDate != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(createdDate) : "";
	}

	public String getFullName() {
		return (StringUtils.isNoneBlank(firstName) ? " " + firstName : "")
				+ (StringUtils.isNoneBlank(middleName) ? " " + middleName : "")
				+ (StringUtils.isNoneBlank(lastName) ? " " + lastName : "");
	}

	public boolean isAnIbo() {
		return type != null && type.getId() != null && type.getId().equals("2");
	}

	public boolean isAnPA() {
		return type != null && type.getId() != null && type.getId().equals("3");
	}

	private String getStateDescription(boolean state) {
		return state ? "Active" : "Inactive";
	}

	public String getStateDescription() {
		return getStateDescription(state);
	}

	/**
	 * Constructors
	 */

	public User() {
		super();
	}

	public User(String id) {
		super();
		this.id = id;
	}

	public User(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public User(String id, String firstName, String lastName) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public User(String id, String firstName, String middleName, String lastName) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
	}

	public User(String id, String firstName, String lastName, String email,
			UserType type) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.type = type;
	}

	public User(String id, String name, String firstName, String lastName,
			String email, boolean state) {
		super();
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.state = state;
	}

	public User(String id, String firstName, String middleName,
			String lastName, String email) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
	}

	public User(String id, String name, String firstName, String lastName,
			String email, boolean state, String phone, boolean needUpdate) {
		super();
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.state = state;
		this.phone = phone;
		this.needUpdate = needUpdate;
	}

	public User(String id, String name, String firstName, String middleName,
			String lastName, String email, boolean state, String phone) {
		super();
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.state = state;
		this.phone = phone;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	}

	public User getUserUpdated() {
		return userUpdated;
	}

	public void setUserUpdated(User userUpdated) {
		this.userUpdated = userUpdated;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isSuperAdmin() {
		return superAdmin;
	}

	public void setSuperAdmin(boolean superAdmin) {
		this.superAdmin = superAdmin;
	}

	public List<Profile> getProfile() {
		return profile;
	}

	public void setProfile(List<Profile> profile) {
		this.profile = profile;
	}

	public List<Groups> getGroups() {
		return groups;
	}

	public void setGroups(List<Groups> groups) {
		this.groups = groups;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getRolName() {
		return rolName;
	}

	public void setRolName(String rolName) {
		this.rolName = rolName;
	}

	public Groups getGroup() {
		if (groups != null && groups.size() > 0) {
			return groups.get(0);
		}
		return null;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public Profile getIbo() {
		return ibo;
	}

	public void setIbo(Profile ibo) {
		this.ibo = ibo;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	public String getCorporationName() {
		return corporationName;
	}

	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}

	public boolean isGoToBank() {
		return goToBank;
	}

	public void setGoToBank(boolean goToBank) {
		this.goToBank = goToBank;
	}

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Date getStateDate() {
		return stateDate;
	}

	public void setStateDate(Date stateDate) {
		this.stateDate = stateDate;
	}

	public String getProblemLogin() {
		return problemLogin;
	}

	public void setProblemLogin(String problemLogin) {
		this.problemLogin = problemLogin;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String getIdentity() {
		return getId().toString();
	}

	@Override
	public String toString() {
		return (StringUtils.isNotEmpty(name) ? "User name: " + name : "")
				+ (StringUtils.isNotEmpty(firstName) ? " First name: "
						+ firstName : "")
				+ (StringUtils.isNotEmpty(middleName) ? " Middle name: "
						+ middleName : "")
				+ (StringUtils.isNotEmpty(lastName) ? " Last name: " + lastName
						: "")
				+ (StringUtils.isNotEmpty(email) ? " Email: " + email : "")
				+ " State: " + getStateDescription(state);
	}

	@Override
	public int compare(Object old) {
		User o = (User) old;

		if (!getName().equals(o.getName()))
			fieldAdicional += " Old name: " + o.getName();

		if (isState() != o.isState())
			fieldAdicional += " Old state: " + getStateDescription(o.isState());

		if (!getPhone().equals(o.getPhone()))
			fieldAdicional += " New telephone number: " + phone
					+ " Old telephone number: " + o.getPhone();

		if (rol != null && !rol.equals(o.getRol()))
			fieldAdicional += " New rol: "
					+ (rol == null ? "Undefined" : getRol().toString())
					+ " Old rol: "
					+ (o.getRol() == null ? "Undefined" : o.getRol().toString());

		if (type != null && !type.equals(o.getType()))
			fieldAdicional += " New type: "
					+ (type == null ? "Undefined" : type.toString())
					+ " Old type: "
					+ (o.getType() == null ? "Undefined" : o.getType()
							.toString());

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
	}

	public boolean compareAD(Object old) {
		UserUtil userAd = (UserUtil) old;

		if (!firstName.equals(userAd.getFirstName()))
			return false;

		if ((middleName == null && userAd.getMiddleName() != null)
				|| (middleName != null && userAd.getMiddleName() == null)
				|| (middleName != null && userAd.getMiddleName() != null && !middleName
						.equals(userAd.getMiddleName())))
			return false;

		if (lastName != null && userAd.getLastName() != null
				&& !lastName.equals(userAd.getLastName()))
			return false;

		if (email != null && userAd.getEmail() != null
				&& !email.equals(userAd.getEmail()))
			return false;

		return true;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}