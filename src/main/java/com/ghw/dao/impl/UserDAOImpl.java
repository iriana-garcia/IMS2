package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.jasypt.hibernate4.encryptor.HibernatePBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ghw.constant.Constant;
import com.ghw.dao.UserDAO;
import com.ghw.filter.FilterBase;
import com.ghw.filter.UserFilter;
import com.ghw.model.Address;
import com.ghw.model.Corporation;
import com.ghw.model.CorporationType;
import com.ghw.model.Country;
import com.ghw.model.Gender;
import com.ghw.model.Groups;
import com.ghw.model.IboState;
import com.ghw.model.IboType;
import com.ghw.model.OwnerType;
import com.ghw.model.PayMethod;
import com.ghw.model.PayType;
import com.ghw.model.Profile;
import com.ghw.model.Rol;
import com.ghw.model.States;
import com.ghw.model.TypeContract;
import com.ghw.model.User;
import com.ghw.model.UserType;
import com.ghw.model.UserUtil;

@Repository("userDAO")
public class UserDAOImpl extends GenericHibernateDAO<User, String> implements
		UserDAO {

	@Autowired
	HibernatePBEStringEncryptor hibernateStringEncryptor;

	/**
	 * Get the data for the table with filter
	 */
	@Override
	public List<User> getData(FilterBase filter) throws Exception {

		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("name"));
		projection.add(Property.forName("firstName"));
		projection.add(Property.forName("lastName"));
		projection.add(Property.forName("email"));
		projection.add(Property.forName("lastLogin"));
		projection.add(Property.forName("group.name"));
		projection.add(Property.forName("rol.name"));
		projection.add(Property.forName("state"));
		projection.add(Property.forName("superAdmin"));
		projection.add(Property.forName("type.id"));
		projection.add(Property.forName("type.name"));

		criteria.setProjection(projection);

		// criteria.setResultTransformer(new AliasToBeanResultTransformer(
		// User.class));

		if (StringUtils.isEmpty(filter.getSortField())) {
			criteria.addOrder(Order.asc("name"));
		} else {
			if (StringUtils.equalsIgnoreCase(filter.getSortOrder().name(),
					"ascending")) {
				criteria.addOrder(Order.asc(filter.getSortField()));
			} else {
				criteria.addOrder(Order.desc(filter.getSortField()));
			}
		}

		if (filter.getNumberOfRows() > 0) {
			criteria.setMaxResults(filter.getNumberOfRows());
			criteria.setFirstResult(filter.getFirstRow());
		}

		List<Object[]> objects = criteria.list();
		List<User> users = new ArrayList<User>();
		for (Object[] o : objects) {
			User user = new User((String) o[0], (String) o[1], (String) o[2],
					(String) o[3], (String) o[4], (Boolean) o[8]);
			user.setLastLogin((Date) o[5]);
			user.setGroupName((String) o[6]);
			user.setRol(new Rol(null, (String) o[7]));
			user.setSuperAdmin((Boolean) o[9]);
			user.setType(new UserType((String) o[10], (String) o[11]));

			users.add(user);
		}
		return users;

	}

	@Override
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		Criteria criteria = super.formCriteriaByListAndCount(filter)
				.createAlias("type", "type");
		criteria.createAlias("profile", "p", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("p.group", "group", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("rol", "rol", JoinType.LEFT_OUTER_JOIN);

		UserFilter userFilter = (UserFilter) filter;
		if (userFilter.getStartDate() != null
				&& userFilter.getEndDate() == null) {
			criteria.add(Restrictions.sqlRestriction(
					"date(use_last_login) = ? ",
					new Date[] { userFilter.getStartDate() },
					new Type[] { StandardBasicTypes.DATE }));
		}

		if (userFilter.getStartDate() != null
				&& userFilter.getEndDate() != null) {
			criteria.add(Restrictions.sqlRestriction(
					"date(use_last_login) between ? and ?",
					new Date[] { userFilter.getStartDate(),
							userFilter.getEndDate() }, new Type[] {
							StandardBasicTypes.DATE, StandardBasicTypes.DATE }));
		}

		return criteria;
	}

	/**
	 * Get the user data by name in active state
	 */
	@Override
	public User getUser(String name) {
		Criteria criteria = getSession().createCriteria(User.class)
				.createAlias("profile", "p", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.eq("state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("name"));
		projection.add(Property.forName("firstName"));
		projection.add(Property.forName("lastName"));
		projection.add(Property.forName("superAdmin"));
		projection.add(Property.forName("rol.id"));
		projection.add(Property.forName("type.id"));
		projection.add(Property.forName("p.id"));
		projection.add(Property.forName("p.totalSubmit"));
		projection.add(Property.forName("email"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("name"));

		List<Object[]> result = criteria.list();
		User user = null;

		if (result.size() > 0) {
			Object[] o = result.get(0);
			user = new User();
			user.setId((String) o[0]);
			user.setName((String) o[1]);
			user.setFirstName((String) o[2]);
			user.setLastName((String) o[3]);
			user.setSuperAdmin((Boolean) o[4]);
			user.setRol(new Rol((String) o[5]));
			user.setType(new UserType((String) o[6]));
			Profile profile = new Profile((String) o[7]);
			profile.setTotalSubmit((Integer) o[8]);
			user.setEmail((String) o[9]);
			user.setIbo(profile);
		}

		return user;

	}

	@Override
	public User getUserLogin(String name) {
		Criteria criteria = getSession().createCriteria(User.class)
				.createAlias("profile", "p", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("name", name));
		// criteria.add(Restrictions.eq("state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("name"));
		projection.add(Property.forName("firstName"));
		projection.add(Property.forName("lastName"));
		projection.add(Property.forName("superAdmin"));
		projection.add(Property.forName("rol.id"));
		projection.add(Property.forName("type.id"));
		projection.add(Property.forName("p.id"));
		projection.add(Property.forName("p.totalSubmit"));
		projection.add(Property.forName("email"));
		projection.add(Property.forName("state"));
		projection.add(Property.forName("stateDate"));
		projection.add(Property.forName("p.ownerType"));
		projection.add(Property.forName("p.region"));
		projection.add(Property.forName("p.typeContract"));
		projection.add(Property.forName("p.payMethod"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("name"));

		List<Object[]> result = criteria.list();
		User user = null;

		if (result.size() > 0) {
			Object[] o = result.get(0);
			user = new User();
			user.setId((String) o[0]);
			user.setName((String) o[1]);
			user.setFirstName((String) o[2]);
			user.setLastName((String) o[3]);
			user.setSuperAdmin((Boolean) o[4]);
			user.setRol(new Rol((String) o[5]));
			user.setType(new UserType((String) o[6]));
			Profile profile = new Profile((String) o[7]);
			profile.setTotalSubmit((Integer) o[8]);
			profile.setOwnerType((OwnerType) o[12]);
			profile.setRegion((String) o[13]);
			profile.setTypeContract((TypeContract) o[14]);
			profile.setPayMethod((PayMethod) o[15]);
			user.setEmail((String) o[9]);
			user.setState((Boolean) o[10]);
			user.setStateDate((Date) o[11]);
			user.setIbo(profile);
		}

		return user;

	}

	/**
	 * update the date of the login
	 */
	@Override
	public User updateLastLogin(User user) {
		Query query = getSession().createQuery(
				"update User set lastLogin=:lastLogin where id=:id");
		query.setString("id", user.getId());
		query.setTimestamp("lastLogin", new Date());

		query.executeUpdate();

		return user;

	}

	/**
	 * Get the user data by name
	 */
	public User getUserByName(String name) {
		Criteria criteria = getSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("name", name));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("name"));
		projection.add(Property.forName("firstName"));
		projection.add(Property.forName("lastName"));
		projection.add(Property.forName("superAdmin"));
		projection.add(Property.forName("rol.id"));
		projection.add(Property.forName("type.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("name"));

		List<Object[]> result = criteria.list();
		User user = null;

		if (result.size() > 0) {
			Object[] o = result.get(0);
			user = new User();
			user.setId((String) o[0]);
			user.setName((String) o[1]);
			user.setFirstName((String) o[2]);
			user.setLastName((String) o[3]);
			user.setSuperAdmin((Boolean) o[4]);
			user.setRol(new Rol((String) o[5]));
			user.setType(new UserType((String) o[6]));
		}

		return user;

	}

	/**
	 * Change the state and save the date and the user that make the action
	 */
	@Override
	public int changeState(User user, User userModify) {

		Query query = getSession().createQuery(
				"update User set state =:state, updateDate=:dateUpdate,"
						+ (userModify == null ? "userUpdated=null"
								: "userUpdated=:user")
						+ (user.isState() ? "" : "")
						+ " ,stateDate=NOW()  where id=:id");
		query.setString("id", user.getId());
		query.setBoolean("state", !user.isState());
		query.setTimestamp("dateUpdate", new Date());
		if (userModify != null)
			query.setEntity("user", userModify);

		return query.executeUpdate();
	}

	/**
	 * Change the user's state
	 */
	@Override
	public int changeState(String userName) {
		Query query = getSession()
				.createQuery(
						"update User set state =false,stateDate=NOW() where name=:name");
		query.setString("name", userName);
		return query.executeUpdate();
	}

	/**
	 * get all the user's name
	 */
	@Override
	public List<String> getDataName() {

		Criteria criteria = getSession().createCriteria(User.class);
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("name"));

		criteria.setProjection(projection);

		return criteria.list();
	}

	/**
	 * get all the user's name except super user
	 */
	@Override
	public List<String> getDataNameExceptSuper() {

		Criteria criteria = getSession().createCriteria(User.class).add(
				Restrictions.eq("superAdmin", false));
		criteria.add(Restrictions.eq("state", true));
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("name"));

		criteria.setProjection(projection);

		return criteria.list();
	}

	/**
	 * validate the super admin credencials
	 */
	@Override
	public boolean validateSuperAdmin(User user, String password) {
		Criteria criteria = getSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("name", user.getName()));
		criteria.add(Restrictions.eq("state", true));
		User u = (User) criteria.uniqueResult();

		return u != null && u.getPassword() != null && password != null
				&& u.getPassword().equals(password);
	}

	/**
	 * Get all the user does not have groups associate
	 */
	@Override
	public List<User> getDataWithoutGroup(Groups group) {

		Criteria criteria = getSession().createCriteria(User.class)
				.createAlias("groups", "g", JoinType.LEFT_OUTER_JOIN)
				.createAlias("profile", "p", JoinType.LEFT_OUTER_JOIN)
				.addOrder(Order.asc("firstName"))
				.addOrder(Order.asc("lastName"));

		if (group != null) {
			criteria.add(Restrictions.or(Restrictions.isNull("g.id"),
					Restrictions.eqOrIsNull("g.id", group.getId())));
		} else {
			criteria.add(Restrictions.isNull("g.id"));
		}

		criteria.add(Restrictions.or(Restrictions.eq("type.id", UserType.PA),
				Restrictions.eq("p.type.id", IboType.PA)));
		criteria.add(Restrictions.eq("state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("firstName"), "firstName");
		projection.add(Property.forName("lastName"), "lastName");
		projection.add(Property.forName("state"), "state");
		projection.add(Property.forName("name"), "name");

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("name"));

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				User.class));

		return criteria.list();
	}

	/**
	 * Load all the User data
	 */
	@Override
	public User loadAllById(User entity) {
		Criteria criteria = getSession().createCriteria(User.class);
		criteria.createCriteria("rol", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("userCreated", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("userUpdated", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("groups", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("type");

		criteria.add(Restrictions.eq("id", entity.getId()));

		return (User) criteria.uniqueResult();
	}

	@Override
	public User loadAllByIdToEdit(User entity) {
		Criteria criteria = getSession().createCriteria(User.class);
		criteria.createCriteria("rol", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("groups", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("type");

		criteria.add(Restrictions.eq("id", entity.getId()));

		return (User) criteria.uniqueResult();

	}

	public User loadAllByIdToEditVersion2(User entity) {

		Query query = getSession()
				.createQuery(
						"select u.id, u.name,u.firstName,u.middleName, u.lastName, u.email, u.state,u.phone, rol.id, rol.name,group.id, group.name,type.id, type.name,"
								+ "pro.id,pro.princTitle,pro.region, pro.amount,pro.number, pro.ownerType, pro.birthday, pro.gender, pro.payType, pro.payMethod, pro.typeContract,"
								+ " iboType.id, iboType.name,iboState.id, iboState.name, groupIbo.id, groupIbo.name, country.id, country.name,"
								+ "cor.id, cor.name, cor.ein, cor.type, cor.payMethod,"
								+ "add.id, add.type, add.description, add.zipCode, add.city, states.id, states.name  "
								+ "from User u  "
								+ "left join u.rol as rol "
								+ "left join u.groups as group "
								+ "left join u.type as type "
								+ "left join u.profile as pro "
								+ "left join pro.type as iboType "
								+ "left join pro.iboState as iboState "
								+ "left join pro.group as groupIbo "
								+ "left join pro.country as country "
								+ "left join pro.corporation as cor "
								+ "left join cor.address as add "
								+ "left join add.states as states "
								+ "where u.id=:id");
		query.setString("id", entity.getId());

		Object[] o = (Object[]) query.uniqueResult();

		User user = new User((String) o[0], (String) o[1], (String) o[2],
				(String) o[3], (String) o[4], (String) o[5], (Boolean) o[6],
				(String) o[7]);
		user.setRol(new Rol((String) o[8], (String) o[9]));
		List<Groups> groups = new ArrayList<Groups>();
		groups.add(new Groups((String) o[10], (String) o[11]));
		user.setGroups(groups);
		user.setType(new UserType((String) o[12], (String) o[13]));

		if (o[14] != null) {
			Profile profile = new Profile((String) o[14], (String) o[15],
					(String) o[16], (Double) o[17], (String) o[18],
					(OwnerType) o[19], (Date) o[20], (Gender) o[21],
					(PayType) o[22], (PayMethod) o[23], (TypeContract) o[24]);

			profile.setType(new IboType((String) o[25], (String) o[26]));
			profile.setIboState(new IboState((String) o[27], (String) o[28]));

			if (o[29] != null) {
				profile.setGroup(new Groups((String) o[29], (String) o[30]));
			}
			if (o[31] != null) {
				profile.setCountry(new Country((String) o[31], (String) o[32]));
			}
			if (o[33] != null) {
				profile.setCorporation(new Corporation((String) o[33],
						(String) o[34], (String) o[35],
						(CorporationType) o[36], (PayMethod) o[37]));
				List<Address> addresses = new ArrayList<Address>();
				addresses.add(new Address((String) o[38], (String) o[39],
						(String) o[40], (String) o[41], (String) o[42],
						new States((String) o[43], (String) o[44]),
						new Country(Constant.usCountryId)));
				profile.getCorporation().setAddress(addresses);
			}

			user.setIbo(profile);
		}

		return user;

	}

	/**
	 * Validate if the email exists in system
	 */
	@Override
	public boolean validateIfExistsEmail(String email, String id) {
		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.add(Restrictions.ilike("email", email));
		if (StringUtils.isNotBlank(id)) {
			criteria.add(Restrictions.ne("id", id));
		}

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.rowCount());

		criteria.setProjection(projection);

		return (Long) criteria.list().get(0) > 0 ? true : false;
	}

	/**
	 * Get all the user for comparin with AD
	 */
	public Map<String, User> getUserForAd() {
		Criteria criteria = getSession().createCriteria(User.class);

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("name"));
		projection.add(Property.forName("firstName"));
		projection.add(Property.forName("lastName"));
		projection.add(Property.forName("superAdmin"));
		projection.add(Property.forName("rol.id"));
		projection.add(Property.forName("type.id"));
		projection.add(Property.forName("state"));

		criteria.setProjection(projection);

		List<Object[]> result = criteria.list();
		Map<String, User> listUser = new HashMap<String, User>();

		for (Object[] o : result) {
			User user = new User();
			user.setId((String) o[0]);
			user.setName((String) o[1]);
			user.setFirstName((String) o[2]);
			user.setLastName((String) o[3]);
			user.setSuperAdmin((Boolean) o[4]);
			user.setRol(new Rol((String) o[5]));
			user.setType(new UserType((String) o[6]));
			user.setState((Boolean) o[7]);

			listUser.put(user.getName(), user);
		}

		return listUser;

	}

	public void updatePassword(User user) {

		String encrypPass = hibernateStringEncryptor
				.encrypt(user.getPassword());

		Query query = getSession().createQuery(
				"update User set password=:password where id=:id");
		query.setString("password", encrypPass);
		query.setString("id", user.getId());
		query.executeUpdate();
	}

	/**
	 * Get all the profile to export to Oracle, all need to updated and the
	 * profile does not have number
	 * 
	 * @return
	 */
	@Override
	public List<User> getDataActiveOracle() {

		// I need only the adddress form the United states
		Criteria criteria = getSession().createCriteria(User.class)
				.createAlias("profile", "p").createAlias("p.corporation", "c");
		// Get the user type = IBO
		criteria.add(Restrictions.eq("type.id", "2"));
		// get user active
		criteria.add(Restrictions.eq("state", true));

		// with the supplier number is null (need to be inserted in oracle), or
		// need to be updated
		criteria.add(Restrictions.or(Restrictions.isNull("c.supplierNumber"),
				Restrictions.eq("needUpdate", true)));

		// only the IBO from the US
		criteria.add(Restrictions.eq("p.region", "D"));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"), "id");
		projection.add(Property.forName("c.name"), "corporationName");
		projection.add(Property.forName("firstName"), "firstName");
		projection.add(Property.forName("lastName"), "lastName");
		projection.add(Property.forName("email"), "email");
		projection.add(Property.forName("phone"), "phone");
		projection.add(Property.forName("needUpdate"), "needUpdate");

		criteria.setProjection(projection);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				User.class));

		return criteria.list();

	}

	/**
	 * update the field need updated to false when is exported
	 */
	@Override
	public void updateNeedUpdatedFalse(List<User> users) {
		if (users != null && users.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (User s : users)
				ids.add(s.getId());

			Query query = getSession().createQuery(
					"update User p set p.needUpdate=false where p.id IN(:ids)");

			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	public void updateAD(User user, UserUtil userUtil) {

		Query query = getSession()
				.createQuery(
						"update User set email =:email, firstName =:firstName, middleName =:middleName, lastName =:lastName where id =:id");

		query.setString("id", user.getId());
		query.setString("email", userUtil.getEmail());
		query.setString("firstName", userUtil.getFirstName());
		query.setString("middleName", userUtil.getMiddleName());
		query.setString("lastName", userUtil.getLastName());

		query.executeUpdate();
	}

}