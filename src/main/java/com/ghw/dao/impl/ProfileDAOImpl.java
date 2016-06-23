package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import com.ghw.dao.ProfileDAO;
import com.ghw.model.ClientApplication;
import com.ghw.model.Corporation;
import com.ghw.model.Groups;
import com.ghw.model.IboState;
import com.ghw.model.IboType;
import com.ghw.model.OwnerType;
import com.ghw.model.Profile;
import com.ghw.model.User;
import com.ghw.model.UserType;

@Repository("profileDAO")
public class ProfileDAOImpl extends GenericHibernateDAO<Profile, String>
		implements ProfileDAO {

	/**
	 * Get all the IBO associate to a client application
	 */
	@Override
	public List<Profile> getDataByCA(ClientApplication ca) {
		Criteria criteria = getSession()
				.createCriteria(Profile.class)
				.createAlias("clientApplications", "lca")
				.createAlias("user", "u")
				.createAlias("corporation", "corporation",
						JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("lca.clientApplication.id", ca.getId()));
		criteria.add(Restrictions.eq("u.type.id", "2"));
		criteria.add(Restrictions.eq("u.state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("u.firstName"));
		projection.add(Property.forName("u.lastName"));
		projection.add(Property.forName("u.email"));
		projection.add(Property.forName("u.name"));
		projection.add(Property.forName("u.state"));
		projection.add(Property.forName("number"));
		projection.add(Property.forName("corporation.id"));
		projection.add(Property.forName("u.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("number"));

		List<Profile> ibos = new ArrayList<Profile>();
		List<Object[]> list = criteria.list();
		for (Object[] o : list) {
			Profile pro = new Profile();
			pro.setId((String) o[0]);
			pro.setCorporation(new Corporation((String) o[8], (String) o[1]));
			pro.setUser(new User((String) o[9], (String) o[5], (String) o[2],
					(String) o[3], (String) o[4], (Boolean) o[6]));
			pro.setNumber((String) o[7]);

			ibos.add(pro);
		}

		return ibos;
	}

	/**
	 * Get all the IBO y the association or not with the client application
	 */
	@Override
	public List<Profile> getDataAllIboByCA(ClientApplication ca) {

		Criteria criteria = getSession()
				.createCriteria(Profile.class)
				.createAlias("clientApplications", "lca",
						JoinType.LEFT_OUTER_JOIN,
						Restrictions.eq("lca.clientApplication.id", ca.getId()))
				.createAlias("user", "u")
				.createAlias("corporation", "corporation",
						JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("u.type.id", "2"));
		criteria.add(Restrictions.eq("u.state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("u.firstName"));
		projection.add(Property.forName("u.lastName"));
		projection.add(Property.forName("u.email"));
		projection.add(Property.forName("u.name"));
		projection.add(Property.forName("u.state"));
		projection.add(Property.forName("lca.id"));
		projection.add(Property.forName("number"));
		projection.add(Property.forName("corporation.id"));
		projection.add(Property.forName("u.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("number"));

		List<Profile> ibos = new ArrayList<Profile>();
		List<Object[]> list = criteria.list();
		for (Object[] o : list) {
			Profile pro = new Profile();
			pro.setId((String) o[0]);
			pro.setCorporation(new Corporation((String) o[9], (String) o[1]));
			pro.setUser(new User((String) o[10], (String) o[5], (String) o[2],
					(String) o[3], (String) o[4], (Boolean) o[6]));
			pro.setHasCa(o[7] != null);
			pro.setNumber((String) o[8]);

			ibos.add(pro);
		}

		return ibos;
	}

	/**
	 * Get all the IBO does not have group associate in state active
	 */
	@Override
	public List<Profile> getDataWithoutGroup(Groups group) {
		Criteria criteria = getSession()
				.createCriteria(Profile.class)
				.createAlias("user", "user")
				.createAlias("corporation", "corporation",
						JoinType.LEFT_OUTER_JOIN)
				.createAlias("group", "group", JoinType.LEFT_OUTER_JOIN)
				.addOrder(Order.asc("user.firstName"))
				.addOrder(Order.asc("user.lastName"));
		criteria.add(Restrictions.eq("user.type.id", UserType.IBO));
		criteria.add(Restrictions.ne("type.id", IboType.PA));
		criteria.add(Restrictions.eq("user.state", true));

		if (group != null) {
			criteria.add(Restrictions.or(Restrictions.isNull("group.id"),
					Restrictions.eqOrIsNull("group.id", group.getId())));
		} else {
			criteria.add(Restrictions.isNull("group"));
		}

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("user.firstName"));
		projection.add(Property.forName("user.lastName"));
		projection.add(Property.forName("user.email"));
		projection.add(Property.forName("group.id"));
		projection.add(Property.forName("user.name"));
		projection.add(Property.forName("user.state"));
		projection.add(Property.forName("number"));
		projection.add(Property.forName("corporation.id"));
		projection.add(Property.forName("user.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("number"));

		List<Profile> ibos = new ArrayList<Profile>();
		List<Object[]> list = criteria.list();
		for (Object[] o : list) {
			Profile pro = new Profile();
			pro.setId((String) o[0]);
			pro.setCorporation(new Corporation((String) o[9], (String) o[1]));
			pro.setUser(new User((String) o[10], (String) o[6], (String) o[2],
					(String) o[3], (String) o[4], (Boolean) o[7]));
			if ((String) o[5] != null) {
				pro.setGroup(new Groups((String) o[5]));
			}

			pro.setNumber((String) o[8]);

			ibos.add(pro);
		}

		return ibos;
	}

	/**
	 * Get all the IBO associate the a Group
	 */
	@Override
	public List<Profile> getDataByGroup(Groups group) {
		Criteria criteria = getSession()
				.createCriteria(Profile.class)
				.createAlias("user", "u")
				.createAlias("corporation", "corporation",
						JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("group.id", group.getId()));
		criteria.add(Restrictions.eq("u.type.id", "2"));
		// criteria.add(Restrictions.eq("u.state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("u.firstName"));
		projection.add(Property.forName("u.lastName"));
		projection.add(Property.forName("u.email"));
		projection.add(Property.forName("u.id"));
		projection.add(Property.forName("u.name"));
		projection.add(Property.forName("u.state"));
		projection.add(Property.forName("number"));
		projection.add(Property.forName("corporation.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("number"));

		List<Profile> ibos = new ArrayList<Profile>();
		List<Object[]> list = criteria.list();
		for (Object[] o : list) {
			Profile pro = new Profile();
			pro.setId((String) o[0]);
			pro.setCorporation(new Corporation((String) o[9], (String) o[1]));
			pro.setUser(new User((String) o[5], (String) o[6], (String) o[2],
					(String) o[3], (String) o[4], (Boolean) o[7]));

			pro.setNumber((String) o[8]);
			ibos.add(pro);
		}

		return ibos;
	}

	/**
	 * Get the total IBO associate to a Group
	 */
	@Override
	public Long getCountByGroup(Groups group) {
		Criteria criteria = getSession().createCriteria(Profile.class);
		criteria.add(Restrictions.eq("group.id", group.getId()));

		return (Long) criteria.setProjection(Projections.rowCount()).list()
				.get(0);
	}

	/**
	 * Remove the group in the IBO
	 */
	@Override
	public void clearGroup(Groups group) {
		if (group != null) {

			Query query = getSession()
					.createQuery(
							"update Profile p set p.group.id=null where p.group.id =:id");
			query.setString("id", group.getId());

			query.executeUpdate();
		}

	}

	/**
	 * update the Group in the list of IBO
	 */
	@Override
	public void update(List<Profile> ibos, Groups group) {
		if (ibos != null && ibos.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Profile s : ibos)
				ids.add(s.getId());

			Query query = getSession()
					.createQuery(
							"update Profile p set p.group.id=:group where p.id IN(:ids)");
			query.setString("group", group.getId());
			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	/**
	 * remove the group in the IBO list
	 */
	@Override
	public void clearGroup(List<Profile> ibos) {
		if (ibos != null && ibos.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Profile s : ibos)
				ids.add(s.getId());

			Query query = getSession().createQuery(
					"update Profile p set p.group.id=null where p.id IN(:ids)");

			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	/**
	 * remove the group in the IBO list
	 */
	@Override
	public void clearGroup(Profile ibo) {

		Query query = getSession().createQuery(
				"update Profile p set p.group.id=null where p.id =:id");

		query.setString("id", ibo.getId());

		query.executeUpdate();

	}

	/**
	 * remove the group in the IBO list
	 */
	@Override
	public void clearGroup(String idUser) {

		Query query = getSession().createQuery(
				"update Profile p set p.group.id=null where p.user.id =:id");

		query.setString("id", idUser);

		query.executeUpdate();

	}

	/**
	 * Update the IBO's status
	 */
	@Override
	public void update(Profile profile, String idState) {

		Query query = getSession().createQuery(
				"update Profile p set p.iboState.id=:state where p.id =:id ");
		query.setString("state", idState);
		query.setString("id", profile.getId());

		query.executeUpdate();

	}

	/**
	 * Load all the profile data
	 */
	@Override
	public Profile loadAllById(String idUser) {

		// Query query = getSession()
		// .createQuery(
		// "select new Profile(p,type,uc.id, uc.name,uu.id, uu.name, group.id, group.name,iboState,corporation,country) "
		// + "from Profile p "
		// + "left join p.type type "
		// + "left join p.userCreated uc "
		// + "left join p.userUpdated uu "
		// + "left join p.group group "
		// + "left join p.iboState iboState "
		// + "left join p.corporation corporation "
		// + "left join p.country country "
		// + "where p.id =:id");
		// query.setString("id", idUser);
		//
		// return (Profile) query.uniqueResult();

		Criteria criteria = getSession().createCriteria(Profile.class);
		criteria.createCriteria("type", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("userCreated", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("userUpdated", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("group", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("iboState", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("corporation", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("country", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("user.id", idUser));

		return (Profile) criteria.uniqueResult();
	}

	@Override
	public Profile loadAllByIdToEdit(String idUser) {

		Criteria criteria = getSession().createCriteria(Profile.class);
		criteria.createCriteria("type", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("group", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("iboState", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("corporation", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("country", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("user.id", idUser));

		return (Profile) criteria.uniqueResult();
	}

	/**
	 * Get necessary data to send a welcome email
	 */
	@Override
	public Profile getDataBySendEmail(String idUser) {
		Criteria criteria = getSession().createCriteria(Profile.class);
		criteria.createCriteria("type", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("iboState", JoinType.INNER_JOIN).setFetchMode(
				"iboState", FetchMode.JOIN);
		criteria.createCriteria("user");
		criteria.createCriteria("corporation", JoinType.LEFT_OUTER_JOIN)
				.setFetchMode("corporation", FetchMode.JOIN);
		criteria.add(Restrictions.eq("user.id", idUser));

		return (Profile) criteria.uniqueResult();
	}

	/**
	 * Get all the IBO active
	 */
	@Override
	public List<Profile> getDataActiveIbo() {
		Criteria criteria = getSession()
				.createCriteria(Profile.class)
				.createAlias("user", "u")
				.createAlias("corporation", "corporation",
						JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("u.type.id", "2"));
		criteria.add(Restrictions.eq("u.state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("u.firstName"));
		projection.add(Property.forName("u.lastName"));
		projection.add(Property.forName("u.email"));
		projection.add(Property.forName("u.name"));
		projection.add(Property.forName("u.state"));
		projection.add(Property.forName("number"));
		projection.add(Property.forName("corporation.id"));
		projection.add(Property.forName("u.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("number"));

		List<Profile> ibos = new ArrayList<Profile>();
		List<Object[]> list = criteria.list();
		for (Object[] o : list) {
			Profile pro = new Profile();
			pro.setId((String) o[0]);
			pro.setCorporation(new Corporation((String) o[8], (String) o[1]));
			pro.setUser(new User((String) o[9], (String) o[5], (String) o[2],
					(String) o[3], (String) o[4], (Boolean) o[6]));

			pro.setNumber((String) o[7]);

			ibos.add(pro);
		}

		return ibos;
	}

	/**
	 * Get all the IBO active Domestic
	 */
	@Override
	public List<Profile> getDataActiveIboDomestic() {
		Criteria criteria = getSession()
				.createCriteria(Profile.class)
				.createAlias("user", "u")
				.createAlias("corporation", "corporation",
						JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("u.type.id", "2"));
		criteria.add(Restrictions.eq("u.state", true));
		criteria.add(Restrictions.eq("region", "D"));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("u.firstName"));
		projection.add(Property.forName("u.lastName"));
		projection.add(Property.forName("u.email"));
		projection.add(Property.forName("u.name"));
		projection.add(Property.forName("u.state"));
		projection.add(Property.forName("number"));
		projection.add(Property.forName("corporation.id"));
		projection.add(Property.forName("u.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("number"));

		List<Profile> ibos = new ArrayList<Profile>();
		List<Object[]> list = criteria.list();
		for (Object[] o : list) {
			Profile pro = new Profile();
			pro.setId((String) o[0]);
			pro.setCorporation(new Corporation((String) o[8], (String) o[1]));
			pro.setUser(new User((String) o[9], (String) o[5], (String) o[2],
					(String) o[3], (String) o[4], (Boolean) o[6]));

			pro.setNumber((String) o[7]);

			ibos.add(pro);
		}

		return ibos;
	}

	@Override
	public List<Profile> getDataAllActiveIbo() {
		Criteria criteria = getSession()
				.createCriteria(Profile.class)
				.createAlias("user", "u")
				.createAlias("corporation", "corporation",
						JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("u.type.id", "2"));
		criteria.add(Restrictions.eq("u.state", true));
		criteria.add(Restrictions.eq("iboState.id", IboState.OK));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("u.firstName"));
		projection.add(Property.forName("u.lastName"));
		projection.add(Property.forName("u.email"));
		projection.add(Property.forName("u.name"));
		projection.add(Property.forName("u.state"));
		projection.add(Property.forName("number"));
		projection.add(Property.forName("corporation.id"));
		projection.add(Property.forName("u.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("number"));

		List<Profile> ibos = new ArrayList<Profile>();
		List<Object[]> list = criteria.list();
		for (Object[] o : list) {
			Profile pro = new Profile();
			pro.setId((String) o[0]);
			pro.setCorporation(new Corporation((String) o[8], (String) o[1]));
			pro.setUser(new User((String) o[9], (String) o[5], (String) o[2],
					(String) o[3], (String) o[4], (Boolean) o[6]));

			pro.setNumber((String) o[7]);

			ibos.add(pro);
		}

		return ibos;
	}

	/**
	 * Get all the IBO ready to create invoice
	 */
	@Override
	public List<Profile> getDataIboToInvoice() {
		// must to have bank account information and CA assigned
		Criteria criteria = getSession()
				.createCriteria(Profile.class)
				.createAlias("user", "u")
				.createAlias("corporation", "corporation",
						JoinType.LEFT_OUTER_JOIN)
				.createAlias("bankInformation", "ba")
				.createAlias("clientApplications", "ca");
		criteria.add(Restrictions.eq("u.type.id", "2"));
		criteria.add(Restrictions.eq("u.state", true));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("u.firstName"));
		projection.add(Property.forName("u.lastName"));
		projection.add(Property.forName("u.email"));
		projection.add(Property.forName("u.name"));
		projection.add(Property.forName("u.state"));
		projection.add(Property.forName("number"));
		projection.add(Property.forName("corporation.id"));
		projection.add(Property.forName("u.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("number"));

		List<Profile> ibos = new ArrayList<Profile>();
		List<Object[]> list = criteria.list();
		for (Object[] o : list) {
			Profile pro = new Profile();
			pro.setId((String) o[0]);
			pro.setCorporation(new Corporation((String) o[8], (String) o[1]));
			pro.setUser(new User((String) o[9], (String) o[5], (String) o[2],
					(String) o[3], (String) o[4], (Boolean) o[6]));

			pro.setNumber((String) o[7]);

			ibos.add(pro);
		}

		return ibos;
	}

	/**
	 * Get all the profile to export to Oracle, all need to updated and the
	 * profile does not have number
	 * 
	 * @return
	 */
	// @Override
	// public List<Profile> getDataActiveOracle() {
	//
	// // I need only the adddress form the United states
	// Criteria criteria = getSession().createCriteria(Profile.class)
	// .createAlias("user", "u");
	// // Get the user type = IBO
	// criteria.add(Restrictions.eq("u.type.id", "2"));
	// // get user active
	// criteria.add(Restrictions.eq("u.state", true));
	//
	// // with the supplier number is null (need to be inserted in oracle), or
	// // need to be updated
	// criteria.add(Restrictions.or(Restrictions.isNull("supplierNumber"),
	// Restrictions.eq("needUpdate", true)));
	//
	// // only the IBO from th US
	// criteria.add(Restrictions.eq("region", "D"));
	//
	// ProjectionList projection = Projections.projectionList();
	// projection.add(Property.forName("id"), "id");
	// projection.add(Property.forName("name"), "corporationName");
	// projection.add(Property.forName("supplierNumber"), "supplierNumber");
	// projection.add(Property.forName("needUpdate"), "needUpdate");
	// projection.add(Property.forName("ein"), "ein");
	//
	// criteria.setProjection(projection);
	// criteria.setResultTransformer(new AliasToBeanResultTransformer(
	// Profile.class));
	//
	// return criteria.list();
	//
	// }

	// /**
	// * update the field need updated to false when is exported
	// */
	// @Override
	// public void updateNeedUpdatedFalse(List<Profile> ibos) {
	// if (ibos != null && ibos.size() > 0) {
	// List<String> ids = new ArrayList<String>();
	// for (Profile s : ibos)
	// ids.add(s.getId());
	//
	// Query query = getSession()
	// .createQuery(
	// "update Profile p set p.needUpdate=false where p.id IN(:ids)");
	//
	// query.setParameterList("ids", ids);
	//
	// query.executeUpdate();
	// }
	//
	// }

	@Override
	public String getProfileIdByUserId(String idUser) {

		Criteria criteria = getSession().createCriteria(Profile.class).add(
				Restrictions.eq("user.id", idUser));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));

		criteria.setProjection(projection);

		return (String) criteria.uniqueResult();

	}

	@Override
	public String getCorporationIdByUserId(String idUser) {

		Criteria criteria = getSession().createCriteria(Profile.class).add(
				Restrictions.eq("user.id", idUser));

		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("corporation.id"));

		criteria.setProjection(projection);

		return (String) criteria.uniqueResult();

	}

	/**
	 * Valida if need to show the bank information when the IBO logged, only
	 * when is domestic and do not have bank information
	 */
	@Override
	public boolean validateIfGoToBank(String userId) {

		Criteria criteria = getSession().createCriteria(Profile.class);
		criteria.createAlias("corporation", "c", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("c.bankInformation", "bank",
				JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("user.id", userId));

		ProjectionList projection = Projections.projectionList();
		// projection.add(Property.forName("region"));
		projection.add(Property.forName("bank.id"));

		criteria.setProjection(projection);

		List<String> l = criteria.list();
		if (l.isEmpty())
			return false;

		String result = l.get(0);

		return StringUtils.isBlank(result);
	}

	/**
	 * Get the user email and the group email to send a Invoice issue
	 * 
	 * @param iboId
	 * @return
	 */
	@Override
	public String[] getEmailIboAndGroup(String iboId) {

		Query query = getSession()
				.createQuery(
						"select u.email, gu.email from Profile p join p.user u left join p.group g left join g.user gu where p.id=:id");
		query.setString("id", iboId);
		Object[] o = (Object[]) query.uniqueResult();
		return new String[] { (String) o[0], (String) o[1] };
	}

	/**
	 * Get the user email and the group email to send a Invoice issue
	 * 
	 * @param iboId
	 * @return
	 */
	@Override
	public Profile getEmailIbo(String iboId) {

		Query query = getSession()
				.createQuery(
						"select new Profile(p.id,c.name, p.number, p.typeContract,u.id, u.email, u.firstName, u.middleName,u.lastName) from Profile p join p.user u left join p.corporation c where p.id=:id");
		query.setString("id", iboId);

		return (Profile) query.uniqueResult();
	}

	@Override
	public void updateTotalSumbit(String idProfile) {

		Query query = getSession()
				.createQuery(
						"update Profile set totalSubmit = totalSubmit + 1 where id =:id");
		query.setString("id", idProfile);

		query.executeUpdate();
	}

	@Override
	public void resetTotalSumbit() {

		Query query = getSession().createQuery(
				"update Profile set totalSubmit = 0");
		query.executeUpdate();
	}

	/**
	 * get all the IBO associate to the Corporation and the state Active and
	 * status different OK
	 */
	@Override
	public List<Profile> getDataByCorporationActiveNoOk(String corporationId) {

		Criteria criteria = getSession().createCriteria(Profile.class)
				.createAlias("user", "user");
		criteria.add(Restrictions.eq("corporation.id", corporationId));
		criteria.add(Restrictions.eq("user.state", true));
		criteria.add(Restrictions.ne("iboState.id", "4"));

		return criteria.list();
	}

	@Override
	public List<Profile> getDataByCorporationActive(String corporationId) {

		Query query = getSession()
				.createQuery(
						"select new Profile(p.id,p.number,p.ownerType,u.id,u.firstName, u.middleName,u.lastName, u.email) "
								+ "from Profile p join p.user u "
								+ "where p.corporation.id=:idCorporation and u.state =:state");
		query.setString("idCorporation", corporationId);
		query.setBoolean("state", true);

		return query.list();
	}

	public void updateIBOToPrincipal(Profile profile) {
		Query query = getSession().createQuery(
				"update Profile set ownerType =:ownerType where id=:id ");
		query.setString("id", profile.getId());
		query.setString("ownerType", OwnerType.PRINCIPAL.name());
		query.executeUpdate();
	}

	public void updateOwnerType(Profile profile, OwnerType ownerType) {
		Query query = getSession().createQuery(
				"update Profile set ownerType =:ownerType where id=:id ");
		query.setString("id", profile.getId());
		query.setString("ownerType", ownerType.name());
		query.executeUpdate();
	}

	public void updateOwnerType(String userId, OwnerType ownerType) {
		Query query = getSession().createQuery(
				"update Profile set ownerType =:ownerType where user.id=:id ");
		query.setString("id", userId);
		query.setString("ownerType", ownerType.name());
		query.executeUpdate();
	}

	@Override
	public Profile getDataForInactive(String userId) {

		Query query = getSession()
				.createQuery(
						"select new Profile(p.id,p.ownerType, (select count(*) from Profile pc where pc.corporation.id = p.corporation.id and pc.id != p.id),"
								+ "(select max(pi.id) from Profile pi where pi.corporation.id = p.corporation.id and pi.id != p.id)) "
								+ "from Profile p where p.user.id=:id");
		query.setString("id", userId);

		return (Profile) query.uniqueResult();
	}

	public Map<String, Double> getRate() {
		Query query = getSession()
				.createQuery(
						"select u.id, p.amount from Profile p join p.user u where u.state = true and p.amount > 0");
		List<Object[]> list = query.list();
		Map<String, Double> mapUser = new HashMap<String, Double>();
		for (Object[] o : list) {
			mapUser.put((String) o[0], (Double) o[1]);
		}
		return mapUser;
	}

	@Override
	public Profile getDataForUser(String userId) {

		Query query = getSession().createQuery(
				"select p from Profile p where p.user.id=:id");
		query.setString("id", userId);

		return (Profile) query.uniqueResult();
	}

	public boolean hasCorporationIboOtherRegion(Corporation corporation,
			Profile profile) {
		Query query = getSession()
				.createQuery(
						"select count(*) from Profile p where p.corporation.id =:idCorporation and p.id != :idIbo and p.region !=:region");

		Criteria criteria = getSession().createCriteria(Profile.class);
		criteria.add(Restrictions.eq("corporation.id", corporation.getId()));
		criteria.add(Restrictions.ne("id", profile.getId()));
		criteria.add(Restrictions.ne("region", profile.getRegion()));

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.rowCount());

		criteria.setProjection(projection);

		return (Long) criteria.list().get(0) > 0 ? true : false;
	}

	public String getProfileNumberByUser(String userID) {
		Query query = getSession().createQuery(
				"select number from Profile where user.id = :idUser")
				.setString("idUser", userID);
		return (String) query.uniqueResult();
	}
}