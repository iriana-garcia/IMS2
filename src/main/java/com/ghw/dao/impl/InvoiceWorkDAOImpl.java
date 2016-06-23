package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.DoubleType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.ghw.dao.InvoiceWorkDAO;
import com.ghw.model.AgentStateDetail;
import com.ghw.model.ClientApplication;
import com.ghw.model.Corporation;
import com.ghw.model.Event;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceHoursAdded;
import com.ghw.model.InvoiceState;
import com.ghw.model.InvoiceWork;
import com.ghw.model.Profile;
import com.ghw.model.User;
import com.ghw.model.UserType;

@Repository("invoiceWorkDAO")
public class InvoiceWorkDAOImpl extends
		GenericHibernateDAO<InvoiceWork, String> implements InvoiceWorkDAO {

	/**
	 * validate if a client application has an invoices in state pending or
	 * submitted
	 */
	@Override
	public boolean validateAssociateCA(ClientApplication ca) {
		Criteria criteria = getSession().createCriteria(InvoiceWork.class)
				.createAlias("invoice", "inv").createAlias("inv.user", "ibo");
		criteria.add(Restrictions.eq("cliId", ca.getId()));
		criteria.add(Restrictions.or(Restrictions.eq("inv.state.id", "1"),
				Restrictions.eq("inv.state.id", "2")));
		criteria.add(Restrictions.le("ibo.amount", 0.0));

		criteria.setProjection(Projections.rowCount());

		return (Long) criteria.list().get(0) > 0 ? true : false;
	}

	public int updateInvoice(Invoice invoice, User userModify) {

		int cantUpdated = 0;

		// first update the ONLY Program to the invoice that has amount
		Query query = getSession()
				.createQuery(
						"update InvoiceWork set invoice.id=:invId "
								+ (userModify != null ? " , userUpdated.id=:idUser "
										: "")
								+ "where invoice.id is null and cast(schStartTime as date) between :dateIni and :dateEnd and user.id =:userId");

		query.setString("invId", invoice.getId());
		query.setDate("dateIni", invoice.getStart());
		query.setDate("dateEnd", invoice.getEnd());
		query.setString("userId", invoice.getUser().getUser().getId());
		if (userModify != null)
			query.setString("idUser", userModify.getId());
		cantUpdated = query.executeUpdate();

		return cantUpdated;
	}

	public int updateInvoice(Invoice invoice, String workId, User userModify) {

		int cantUpdated = 0;

		// first update the ONLY Program to the invoice that has amount
		Query query = getSession().createQuery(
				"update InvoiceWork set invoice.id=:invId, userUpdated.id=:idUser "
						+ "where invoice.id is null and id =:id");

		query.setString("invId", invoice.getId());
		query.setString("id", workId);
		query.setString("idUser", userModify.getId());

		cantUpdated = query.executeUpdate();

		return cantUpdated;
	}

	public int updateEvents(ClientApplication ca, List<Event> events,
			User userModify) {

		int cantUpdated = 0;
		if (events.size() > 0) {

			List<String> listEvents = new ArrayList<String>();
			for (Event event : events) {
				listEvents.add(event.getId());
			}

			// first update the ONLY Program to the invoice that has amount
			Query query = getSession()
					.createQuery(
							"update InvoiceWork set cliId=:cliId, cliName=:name "
									+ (userModify != null ? " , userUpdated.id=:idUser "
											: "")
									+ "where cliId is null and amount > 0 and skillId IN(:idEvents) ");

			query.setString("name", ca.getName());
			query.setString("cliId", ca.getId());
			query.setParameterList("idEvents", listEvents);

			if (userModify != null) {
				query.setString("idUser", userModify.getId());
			}

			cantUpdated = query.executeUpdate();

			// update all the invoice work cli_id is null for the event list
			query = getSession()
					.createQuery(
							"update InvoiceWork set cliId=:cliId, cliName=:name, amount =:amount "
									+ (userModify != null ? " , userUpdated.id=:idUser "
											: "")
									+ "where cliId is null and amount <= 0 and skillId IN(:idEvents) ");

			query.setString("name", ca.getName());
			query.setDouble("amount", ca.getAmount());
			query.setString("cliId", ca.getId());
			query.setParameterList("idEvents", listEvents);
			if (userModify != null) {
				query.setString("idUser", userModify.getId());
			}

			cantUpdated += query.executeUpdate();

		}

		return cantUpdated;
	}

	/**
	 * update the client application data in the invoice before change the CA,
	 * validate the IBO does not have amount
	 */
	@Override
	public int updateClientApplication(ClientApplication ca, Date dateMod,
			boolean modifyDescription, User userModify) {

		int cantUpdated = 0;
		// Get the invoices ID before
		Query query = getSession()
				.createQuery(
						"select i.id from Invoice i join i.user ibo where i.state.id IN (1,2) and (ibo.amount is null or ibo.amount <=0)");
		List<String> listInv = query.list();

		if (listInv.size() > 0) {
			// update amount in the invoices where the IBO does not amount
			// defined
			query = getSession()
					.createQuery(
							"update InvoiceWork set cliName=:name, amount =:amount "
									+ (userModify != null ? " , userUpdated.id=:idUser "
											: "")
									+ "where cliId=:cliId and cast(schStartDate as date)  >=:date "
									+ "and invoice.id IN (:idInv) ) ");

			query.setString("name", ca.getName());
			query.setDouble("amount", ca.getAmount());
			query.setString("cliId", ca.getId());
			query.setDate("date", dateMod);
			query.setParameterList("idInv", listInv);
			if (userModify != null) {
				query.setString("idUser", userModify.getId());
			}

			cantUpdated = query.executeUpdate();
		}

		// update with the event added

		int cantUpdatedDescript = 0;
		// update only the description in the rest
		if (modifyDescription) {
			query = getSession()
					.createQuery(
							"update InvoiceWork set cliName=:name "
									+ (userModify != null ? " , userUpdated.id=:idUser "
											: "")
									+ "where cliId=:cliId "
									+ "and cast(schStartDate as date) >=:date "
									+ "and invoice.id IN (select i.id from Invoice i join i.user ibo where i.state.id IN (1,2) and (ibo.amount is not null and ibo.amount > 0) ) ");

			query.setString("name", ca.getName());
			query.setString("cliId", ca.getId());
			query.setDate("date", dateMod);
			if (userModify != null) {
				query.setString("idUser", userModify.getId());
			}

			cantUpdatedDescript = query.executeUpdate();
		}

		return cantUpdated + cantUpdatedDescript;

	}

	@Override
	public int updateProfileRate(Profile profile, User userModify) {

		// int cantUpdated = 0;
		// Get the invoices ID before
		Query query = getSession()
				.createQuery(
						"select i.id from Invoice i join i.user ibo where i.state.id IN (1,2) and ibo.id = :idIbo");
		query.setString("idIbo", profile.getId());
		List<String> listInv = query.list();

		if (listInv.size() > 0) {
			// update amount in the invoices where the IBO does not amount
			// defined
			query = getSession()
					.createQuery(
							"update InvoiceWork set amount =:amount "
									+ (userModify != null ? " , userUpdated.id=:idUser "
											: "")
									+ "where cast(schStartDate as date)  >=:date "
									+ "and invoice.id IN (:idInv) ) ");

			query.setDouble("amount", profile.getAmount());
			query.setDate("date", profile.getDateModification());
			query.setParameterList("idInv", listInv);
			if (userModify != null) {
				query.setString("idUser", userModify.getId());
			}

			// cantUpdated =
			query.executeUpdate();
		}

		return listInv.size();

	}

	public int updateActualService(InvoiceWork iw, User userModify) {

		// first update the ONLY Program to the invoice that has amount
		Query query = getSession()
				.createQuery(
						"update InvoiceWork set actualServiceMili=:actualServiceMili, actualService=:actualService,"
								+ "totalNotReadyTimeMili=:totalNotReadyTimeMili, totalNotReadyTime=:totalNotReadyTime, serviceRevenue=:serviceRevenue  "
								+ (userModify != null ? " , userUpdated.id=:idUser "
										: "") + "where id =:id ");

		query.setString("id", iw.getId());
		query.setInteger("actualServiceMili", iw.getActualServiceMili());
		query.setDouble("actualService", iw.getActualService());
		query.setInteger("totalNotReadyTimeMili", iw.getTotalNotReadyTimeMili());
		query.setDouble("totalNotReadyTime", iw.getTotalNotReadyTime());
		query.setDouble("serviceRevenue", iw.getServiceRevenue());

		if (userModify != null)
			query.setString("idUser", userModify.getId());

		return query.executeUpdate();

	}

	/**
	 * Get all the invoice related to a client application in state pending or
	 * submitted
	 */
	@Override
	public List<InvoiceWork> getDataAffectedByCAModification(
			ClientApplication ca, Date dateMod) {
		Criteria criteria = getSession()
				.createCriteria(InvoiceWork.class)
				.createAlias("invoice", "inv")
				.createAlias("inv.state", "state")
				.createAlias("inv.user", "ibo")
				.createAlias("ibo.user", "user")
				.createAlias("ibo.corporation", "corporation",
						JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("cliId", ca.getId()));
		criteria.add(Restrictions.or(Restrictions.eq("inv.state.id", "1"),
				Restrictions.eq("inv.state.id", "2")));
		criteria.add(Restrictions.le("ibo.amount", 0.0));

		if (dateMod != null)
			criteria.add(Restrictions.sqlRestriction(
					"date(iwo_sch_start_time) <= ? ", new Date[] { dateMod },
					new Type[] { StandardBasicTypes.DATE }));

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.distinct(Projections.property("inv.id")));
		// projection.add(Property.forName("id"));
		projection.add(Property.forName("inv.start"));
		projection.add(Property.forName("inv.end"));
		projection.add(Property.forName("state.name"));
		projection.add(Property.forName("user.firstName"));
		projection.add(Property.forName("user.lastName"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("ibo.number"));
		projection.add(Property.forName("inv.importTotal"));
		projection.add(Property.forName("corporation.id"));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("inv.start"));

		List<InvoiceWork> invoiceWorks = new ArrayList<InvoiceWork>();
		List<Object[]> list = criteria.list();
		for (Object[] o : list) {
			InvoiceWork iw = new InvoiceWork();
			// iw.setId((String) o[0]);
			// iw.setLogin((Date) o[1]);
			// iw.setLogout((Date) o[2]);

			Profile profile = new Profile();
			profile.setUser(new User("", (String) o[4], (String) o[5], "",
					new UserType()));
			profile.setCorporation(new Corporation((String) o[9], (String) o[6]));
			profile.setNumber((String) o[7]);

			Invoice invoice = new Invoice();
			invoice.setUser(profile);
			invoice.setStart((Date) o[1]);
			invoice.setEnd((Date) o[2]);
			invoice.setState(new InvoiceState(null, (String) o[3]));
			invoice.setImportTotal((Double) o[8]);
			iw.setInvoice(invoice);

			invoiceWorks.add(iw);
		}

		return invoiceWorks;
	}

	/**
	 * Get the list invoice work
	 * 
	 */
	@Override
	public List<InvoiceWork> getDataByInvoice(Invoice invoice) {

		List<InvoiceWork> invoiceWorks = null;
		if (invoice != null && StringUtils.isNotBlank(invoice.getId())) {

			Criteria criteria = getSession().createCriteria(InvoiceWork.class);
			criteria.add(Restrictions.eq("invoice.id", invoice.getId()));
			criteria.addOrder(Order.asc("schStartTime"));
			criteria.addOrder(Order.asc("schEndTime"));

			invoiceWorks = criteria.list();

			// search all the invoice hours

			// for (InvoiceWork invoiceWork : invoiceWorks) {
			criteria = getSession().createCriteria(InvoiceHoursAdded.class)
					.setFetchMode("category", FetchMode.JOIN)
					.createAlias("invoiceWork", "inw");
			criteria.add(Restrictions.eq("inw.invoice.id", invoice.getId()));

			List<InvoiceHoursAdded> listHours = criteria.list();
			for (InvoiceWork iw : invoiceWorks) {
				iw.setHoursAddeds(new ArrayList<InvoiceHoursAdded>());
				for (InvoiceHoursAdded ha : listHours) {
					if (iw.getId().equals(ha.getInvoiceWork().getId()))
						iw.getHoursAddeds().add(ha);
				}
			}

			// invoiceWork.setHoursAddeds(criteria.list());
			// }

		}

		return invoiceWorks;

	}

	/**
	 * Get the list invoice work
	 * 
	 */
	@Override
	public List<InvoiceWork> getDataByInvoice(List<Invoice> invoices) {

		List<InvoiceWork> invoiceWorks = null;
		if (invoices != null && invoices.size() > 0) {

			Criteria criteria = getSession().createCriteria(InvoiceWork.class);
			criteria.add(Restrictions.in("invoice", invoices));
			criteria.addOrder(Order.asc("schStartTime"));
			criteria.addOrder(Order.asc("schEndTime"));

			ProjectionList projection = Projections.projectionList();
			projection.add(Property.forName("invoice.id"), "id");
			projection.add(Property.forName("actualService"), "actualService");
			projection.add(Property.forName("amount"), "amount");
			projection
					.add(Projections.alias(
							Projections
									.sqlProjection(
											"(select SUM(h.iho_hours) from invoice_hours_added h where h.iwo_id={alias}.iwo_id) as hoursExtra",
											new String[] { "hoursExtra" },
											new Type[] { new DoubleType() }),
							"hoursExtra"), "hoursExtra");

			criteria.setProjection(projection);

			criteria.setResultTransformer(new AliasToBeanResultTransformer(
					InvoiceWork.class));

			invoiceWorks = criteria.list();

			// for (InvoiceWork invoiceWork : invoiceWorks) {
			// criteria = getSession().createCriteria(InvoiceHoursAdded.class)
			// .setFetchMode("category", FetchMode.JOIN);
			// criteria.add(Restrictions.eq("invoiceWork.id",
			// invoiceWork.getId()));
			//
			// invoiceWork.setHoursAddeds(criteria.list());
			// }

		}

		return invoiceWorks;

	}

	public List<InvoiceWork> getDataByDateUser(AgentStateDetail asd) {

		Query query = getSession()
				.createQuery(
						"select i from InvoiceWork i join i.invoice inv "
								+ "where i.user.id=:userId and inv.state.id IN ('1','2') and "
								+ " (:dateIni between i.schStartTime and i.schEndTime or :dateFin between i.schStartTime and i.schEndTime) order by i.schStartTime ASC");

		query.setString("userId", asd.getUser().getId());
		query.setTimestamp("dateIni", asd.getStartDate());
		query.setTimestamp("dateFin", asd.getEndDate());

		return query.list();

	}

}