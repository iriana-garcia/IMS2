package com.ghw.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.ghw.dao.InvoiceDAO;
import com.ghw.filter.FilterBase;
import com.ghw.filter.InvoiceFilter;
import com.ghw.model.Corporation;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceState;
import com.ghw.model.PayMethod;
import com.ghw.model.PayType;
import com.ghw.model.Profile;
import com.ghw.model.User;

@Repository("invoiceDAO")
public class InvoiceDAOImpl extends GenericHibernateDAO<Invoice, String>
		implements InvoiceDAO {

	/**
	 * Get the data for the table with filter
	 */
	@Override
	public List<Invoice> getData(FilterBase filter) throws Exception {

		Criteria criteria = formCriteriaByListAndCount(filter);

		criteria.setProjection(getProjectionsByType((InvoiceFilter) filter));

		// default filter
		if (StringUtils.isEmpty(filter.getSortField())) {
			criteria.addOrder(Order.desc("start"));
		} else if (filter.getSortField().equalsIgnoreCase("lastDateSubmitted")) {
			if (StringUtils.equalsIgnoreCase(filter.getSortOrder().name(),
					"ascending")) {
				criteria.addOrder(Order.asc("approval")).addOrder(
						Order.asc("submitted"));
			} else {
				criteria.addOrder(Order.desc("approval")).addOrder(
						Order.desc("submitted"));
			}

		} else if (filter.getSortField().equalsIgnoreCase("note")) {
			if (StringUtils.equalsIgnoreCase(filter.getSortOrder().name(),
					"ascending")) {
				criteria.addOrder(Order.asc("haveQ"));
			} else {
				criteria.addOrder(Order.desc("haveQ"));
			}
		} else if (filter.getSortField().equalsIgnoreCase("user.nameTable")) {

			if (StringUtils.equalsIgnoreCase(filter.getSortOrder().name(),
					"ascending")) {
				criteria.addOrder(Order.asc("u.firstName"))
						.addOrder(Order.asc("u.middleName"))
						.addOrder(Order.asc("u.lastName"));
			} else {
				criteria.addOrder(Order.desc("u.firstName"))
						.addOrder(Order.desc("u.middleName"))
						.addOrder(Order.desc("u.lastName"));
			}

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

		List<Object[]> list = criteria.list();

		return getInvoicesByObject(list, (InvoiceFilter) filter);

	}

	private List<Invoice> getInvoicesByObject(List<Object[]> list,
			InvoiceFilter filter) {

		List<Invoice> invoices = new ArrayList<Invoice>();
		for (Object[] o : list) {
			Invoice invoice = new Invoice();
			invoice.setId((String) o[0]);
			invoice.setStart((Date) o[1]);
			invoice.setEnd((Date) o[2]);
			invoice.setState(new InvoiceState((String) o[3], (String) o[4]));
			invoice.setAdminFee((Double) o[6]);
			invoice.setHoursAdded((Double) o[7]);
			invoice.setTotalIncentive((Double) o[8]);
			invoice.setServiceRevenue((Double) o[9]);
			invoice.setActualService((Double) o[10]);
			invoice.setImportTotal((Double) o[11]);
			invoice.setSubmitted((Date) o[12]);
			invoice.setApproval((Date) o[13]);
			invoice.setNote((String) o[14]);

			switch (filter.getTypeReport()) {
			case 1: {

				invoice.setUser(new Profile((String) o[15], new User(
						(String) o[19], (String) o[5], (String) o[20],
						(String) o[21]), (String) o[16]));

				// invoice.setHaveQuestion((Integer) o[15]);
				invoice.setPayDate((Date) o[17]);
				invoice.setNumber((String) o[18]);

			}
				break;
			case 2: {

				invoice.setUser(new Profile((String) o[15], new User(
						(String) o[5], (String) o[18], (String) o[19],
						(String) o[20]), (String) o[16]));

				invoice.setPayDate((Date) o[17]);
			}
				break;
			}

			invoices.add(invoice);
		}
		return invoices;

	}

	private Projection getProjectionsByType(InvoiceFilter filter) {

		ProjectionList projection = Projections.projectionList();

		switch (filter.getTypeReport()) {
		case 1: {
			projection.add(Property.forName("id"));
			projection.add(Property.forName("start"));
			projection.add(Property.forName("end"));
			projection.add(Property.forName("state.id"));
			projection.add(Property.forName("state.name"));
			// projection.add(Property.forName("corporation.corporationName"));
			projection.add(Property.forName("u.firstName"));
			projection.add(Property.forName("adminFee"));
			projection.add(Property.forName("hoursAdded"));
			projection.add(Property.forName("totalIncentive"));
			projection.add(Property.forName("serviceRevenue"));
			projection.add(Property.forName("actualService"));
			projection.add(Property.forName("importTotal"));
			projection.add(Property.forName("submitted"));
			projection.add(Property.forName("approval"));
			projection.add(Property.forName("note"));
			// projection
			// .add(Projections.alias(
			// Projections
			// .sqlProjection(
			// "(select case when mb.mss_reply_date is null then 2  else 1 end from message_board mb where mb.inv_id={alias}.inv_id order by mb.mss_date DESC  limit 1)  as haveE",
			// new String[] { "haveE" },
			// new Type[] { new IntegerType() }),
			// "haveE"), "haveE");
			projection.add(Property.forName("user.id"));
			projection.add(Property.forName("user.number"));
			projection.add(Property.forName("payDate"));
			projection.add(Property.forName("number"));
			projection.add(Property.forName("u.id"));
			// projection.add(Property.forName("corporation.id"));
			// projection.add(Property.forName("u.firstName"));
			projection.add(Property.forName("u.middleName"));
			projection.add(Property.forName("u.lastName"));
			projection
					.add(Projections.alias(
							Projections
									.sqlProjection(
											"case when LENGTH(this_.inv_note) > 0 then false else true end as haveQ",
											new String[] { "haveQ" },
											new Type[] { new BooleanType() }),
							"haveQ"), "haveQ");
		}
			break;
		case 2: {

			projection.add(Property.forName("id"));
			projection.add(Property.forName("start"));
			projection.add(Property.forName("end"));
			projection.add(Property.forName("state.id"));
			projection.add(Property.forName("state.name"));
			// projection.add(Property.forName("corporation.corporationName"));
			projection.add(Property.forName("u.id"));
			projection.add(Property.forName("adminFee"));
			projection.add(Property.forName("hoursAdded"));
			projection.add(Property.forName("totalIncentive"));
			projection.add(Property.forName("serviceRevenue"));
			projection.add(Property.forName("actualService"));
			projection.add(Property.forName("importTotal"));
			projection.add(Property.forName("submitted"));
			projection.add(Property.forName("approval"));
			projection.add(Property.forName("note"));
			projection.add(Property.forName("user.id"));
			projection.add(Property.forName("user.number"));
			projection.add(Property.forName("payDate"));
			// projection.add(Property.forName("corporation.id"));
			projection.add(Property.forName("u.firstName"));
			projection.add(Property.forName("u.middleName"));
			projection.add(Property.forName("u.lastName"));

		}
			break;
		}

		return projection;
	}

	@Override
	public Criteria formCriteriaByListAndCount(FilterBase filter)
			throws Exception {

		Criteria criteria = super.formCriteriaByListAndCount(filter);
		criteria.createAlias("state", "state");
		criteria.createAlias("user", "user");
		criteria.createAlias("user.user", "u");
		// criteria.createAlias("user.corporation", "corporation");

		InvoiceFilter invFilter = (InvoiceFilter) filter;

		// if type == 1 need the invoices from actual week and the invoices in
		// state pending or submitted from weeks before
		if (invFilter.getTypeReport() == 1) {

			// the canceled invoices NEVER are show
			criteria.add(Restrictions.ne("state.id", "4"));

			criteria.add(Restrictions.or(Restrictions.in("state.id",
					new String[] { "1", "2" }), Restrictions.sqlRestriction(
					"date(inv_start) = ? ",
					new Date[] { invFilter.getStartDate() },
					new Type[] { StandardBasicTypes.DATE })));

			criteria.add(Restrictions.sqlRestriction("date(inv_start) <= ? ",
					new Date[] { invFilter.getStartDate() },
					new Type[] { StandardBasicTypes.DATE }));

			// if (invFilter.getStartDate() != null
			// && invFilter.getEndDate() != null)
			// criteria.add(Restrictions.or(Restrictions.in("state.id",
			// new String[] { "1", "2" }), Restrictions
			// .sqlRestriction("date(inv_start) between ? and ?",
			// new Date[] { invFilter.getStartDate(),
			// invFilter.getEndDate() }, new Type[] {
			// StandardBasicTypes.DATE,
			// StandardBasicTypes.DATE })));

		} else {

			if (invFilter.getStartDate() != null
					&& invFilter.getEndDate() == null) {
				criteria.add(Restrictions.sqlRestriction(
						"date(inv_start) = ? ",
						new Date[] { invFilter.getStartDate() },
						new Type[] { StandardBasicTypes.DATE }));
			}
			if (invFilter.getStartDate() != null
					&& invFilter.getEndDate() != null) {
				criteria.add(Restrictions.sqlRestriction(
						"date(inv_start) between ? and ?",
						new Date[] { invFilter.getStartDate(),
								invFilter.getEndDate() }, new Type[] {
								StandardBasicTypes.DATE,
								StandardBasicTypes.DATE }));
			}
		}

		if (invFilter.getStartDateSubmitted() != null
				&& invFilter.getEndDateSubmitted() == null) {
			criteria.add(Restrictions
					.sqlRestriction(
							"date(case when inv_date_approval is null then inv_date_submitted else inv_date_approval end) = ? ",
							new Date[] { invFilter.getStartDateSubmitted() },
							new Type[] { StandardBasicTypes.DATE }));
		}

		if (invFilter.getStartDateSubmitted() != null
				&& invFilter.getEndDateSubmitted() != null) {
			criteria.add(Restrictions
					.sqlRestriction(
							"date(case when inv_date_approval is null then inv_date_submitted else inv_date_approval end) between ? and ?",
							new Date[] { invFilter.getStartDateSubmitted(),
									invFilter.getEndDateSubmitted() },
							new Type[] { StandardBasicTypes.DATE,
									StandardBasicTypes.DATE }));
		}

		if (invFilter.getStartDatePay() != null
				&& invFilter.getEndDatePay() == null) {
			criteria.add(Restrictions.eq("payDate", invFilter.getStartDatePay()));
		}

		if (invFilter.getStartDatePay() != null
				&& invFilter.getEndDatePay() != null) {
			criteria.add(Restrictions.between("payDate",
					invFilter.getStartDatePay(), invFilter.getEndDatePay()));
		}

		if (invFilter.getNote() != null && invFilter.getNote() != -1) {
			criteria.add(invFilter.getNote() == 0 ? Restrictions.isNull("note")
					: Restrictions.isNotNull("note"));
		}

		if (StringUtils.isNotBlank(invFilter.getIboId())) {
			criteria.add(Restrictions.eq("user.id", invFilter.getIboId()));
		}
		if (invFilter.getTypeImport() != null
				&& invFilter.getTypeImport() != -1) {
			if (invFilter.getTypeImport() == 2)
				criteria.add(Restrictions.ge("importTotal", 0.0));
			else
				criteria.add(Restrictions.lt("importTotal", 0.0));

		}

		if (invFilter.getSentFinance() != null
				&& invFilter.getSentFinance() != 0) {
			criteria.add(Restrictions.eq("payProcessed",
					invFilter.getSentFinance() == 1));
		}

		if (invFilter.getStartDateFinance() != null
				&& invFilter.getEndDateFinance() == null) {
			criteria.add(Restrictions.sqlRestriction(
					"date(inv_date_oracle) = ? ",
					new Date[] { invFilter.getStartDateFinance() },
					new Type[] { StandardBasicTypes.DATE }));
		}

		if (invFilter.getStartDateFinance() != null
				&& invFilter.getEndDateFinance() != null) {
			criteria.add(Restrictions.sqlRestriction(
					"date(inv_date_oracle) between ? and ?",
					new Date[] { invFilter.getStartDateFinance(),
							invFilter.getEndDateFinance() }, new Type[] {
							StandardBasicTypes.DATE, StandardBasicTypes.DATE }));
		}
		return criteria;
	}

	@Override
	public Invoice getTotalInvoices(InvoiceFilter filter) throws Exception {

		Criteria criteria = formCriteriaByListAndCount(filter);

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sum("adminFee"), "adminFee");
		projection.add(Projections.sum("hoursAdded"), "hoursAdded");
		projection.add(Projections.sum("totalIncentive"), "totalIncentive");
		projection.add(Projections.sum("serviceRevenue"), "serviceRevenue");
		projection.add(Projections.sum("actualService"), "actualService");

		projection
				.add(Projections.alias(
						Projections
								.sqlProjection(
										"sum(case WHEN inv_import_total > 0 then inv_import_total else 0.0 end ) as importTotal",
										new String[] { "importTotal" },
										new Type[] { new DoubleType() }),
						"importTotal"), "importTotal");

		criteria.setProjection(projection);

		criteria.setResultTransformer(new AliasToBeanResultTransformer(
				Invoice.class));

		return (Invoice) criteria.uniqueResult();
	}

	/**
	 * validate if an user has an invoice in state pending or submitted
	 */
	@Override
	public boolean validateAssociateIbo(String idUser) {

		Criteria criteria = getSession().createCriteria(Invoice.class)
				.createAlias("user", "p");
		criteria.add(Restrictions.eq("p.user.id", idUser));
		criteria.add(Restrictions.or(Restrictions.eq("state.id", "1"),
				Restrictions.eq("state.id", "2")));

		criteria.setProjection(Projections.rowCount());

		return (Long) criteria.list().get(0) > 0 ? true : false;
	}

	@Override
	public boolean validateIfExists(Date dateStart, Date dateEnd) {
		Query query = getSession()
				.createQuery(
						"select count(*) from Invoice i where DATE(i.start) = :start and DATE(i.end) = :end");
		query.setDate("start", dateStart);
		query.setDate("end", dateEnd);
		return (Long) query.list().get(0) > 0 ? true : false;
	}

	@Override
	public boolean validateIfExists(Date dateStart, Date dateEnd, Profile ibo) {
		// Criteria criteria = getSession().createCriteria(Invoice.class);
		// criteria.add(Restrictions.eq("start", dateStart));
		// criteria.add(Restrictions.eq("end", dateEnd));
		// criteria.add(Restrictions.eq("user.id", ibo.getId()));
		//
		// criteria.setProjection(Projections.rowCount());
		//
		// return (Long) criteria.list().get(0) > 0 ? true : false;

		Query query = getSession()
				.createQuery(
						"select count(*) from Invoice i where DATE(i.start) = :start and DATE(i.end) = :end and user.id = :userId");
		query.setDate("start", dateStart);
		query.setDate("end", dateEnd);
		query.setString("userId", ibo.getId());
		return (Long) query.list().get(0) > 0 ? true : false;
	}

	@Override
	public List<Invoice> getDataActiveOracle() {

		Criteria criteria = getSession()
				.createCriteria(Invoice.class)
				.createAlias("user", "p")
				.createAlias("p.user", "user")
				.createAlias("p.corporation", "corporation")
				.createAlias("corporation.bankInformation", "b",
						JoinType.LEFT_OUTER_JOIN)
				.setFetchMode("p", FetchMode.JOIN)
				.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("b", FetchMode.JOIN);

		// with the supplier number is null, comment to add in the log the
		// problem
		// criteria.add(Restrictions.isNotNull("corporation.supplierNumber"));
		// in state Approval
		criteria.add(Restrictions.eq("state.id", "3"));
		// not send to oracle
		criteria.add(Restrictions.eq("payProcessed", false));

		criteria.add(Restrictions.eq("corporation.payMethod",
				PayMethod.DIRECT_DEPOSIT));

		return criteria.list();

	}

	@Override
	public List<Invoice> getDataActivePayPal() {

		Query query = getSession()
				.createQuery(
						"select i.importTotal, user.email,i.id "
								+ "from Invoice i join i.user ibo "
								+ "join ibo.user user "
								+ "where user.email is not null and i.importTotal > 0 and i.state.id = 3 and i.payProcessed = false and ibo.payType = '"
								+ PayType.USER + "' and ibo.payMethod = '"
								+ PayMethod.PAYPAL + "' group by user.email");

		List<Object[]> list = query.list();
		List<Invoice> invoices = new ArrayList<Invoice>();
		for (Object[] o : list) {
			invoices.add(new Invoice((Double) o[0], (String) o[1],
					(String) o[2]));
		}

		return invoices;

	}

	/**
	 * update the field need updated to false when is exported
	 */
	@Override
	public void updateNeedUpdatedFalse(List<Invoice> invoices) {
		if (invoices != null && invoices.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (Invoice s : invoices)
				ids.add(s.getId());

			Query query = getSession()
					.createQuery(
							"update Invoice p set p.payProcessed=true, p.dateProcessed = NOW() where p.id IN(:ids)");

			query.setParameterList("ids", ids);

			query.executeUpdate();
		}

	}

	/**
	 * Get current invoice by User, last invoice in state Pending, if dont have
	 * show the last one
	 * 
	 */
	@Override
	public Invoice getCurrentInvoice(Profile user) {

		Invoice invoice = null;
		if (user != null && StringUtils.isNotBlank(user.getId())) {

			Criteria criteria = getSession().createCriteria(Invoice.class);
			criteria.createAlias("state", "state")
					.createAlias("user", "user")
					.createAlias("user.user", "u")
					.createAlias("user.corporation", "corporation",
							JoinType.LEFT_OUTER_JOIN);
			// get the user invoice
			criteria.add(Restrictions.eq("user.id", user.getId()));
			// get the fist invoice pending
			criteria.add(Restrictions.eq("state.id", "1"));
			criteria.addOrder(Order.asc("start"));
			criteria.setMaxResults(1);

			criteria.setProjection(getProjectionsCurrentInvoice());

			// get the last invoice in state Pending, if don't have show the
			// last
			// one
			Object[] o = (Object[]) criteria.uniqueResult();

			// get the last invoice in state Pending, if dont have show the last
			// one

			if (o == null) {
				// get the last invoice
				criteria = getSession().createCriteria(Invoice.class);
				criteria.createAlias("user", "user")
						.createAlias("state", "state")
						.createAlias("user.user", "u")
						.createAlias("user.corporation", "corporation",
								JoinType.LEFT_OUTER_JOIN);
				criteria.add(Restrictions.eq("user.id", user.getId()));
				criteria.add(Restrictions.ne("state.id", "4"));
				criteria.addOrder(Order.desc("start"));
				criteria.setMaxResults(1);

				criteria.setProjection(getProjectionsCurrentInvoice());

				o = (Object[]) criteria.uniqueResult();
			}

			invoice = formInvoiceObject(o);

		}

		return invoice;

	}

	private ProjectionList getProjectionsCurrentInvoice() {
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("id"));
		projection.add(Property.forName("start"));
		projection.add(Property.forName("end"));
		projection.add(Property.forName("state.id"));
		projection.add(Property.forName("state.name"));
		projection.add(Property.forName("adminFee"));
		projection.add(Property.forName("importTotal"));
		projection.add(Property.forName("user.id"));
		projection.add(Property.forName("corporation.name"));
		projection.add(Property.forName("payDate"));
		projection.add(Property.forName("number"));
		projection.add(Property.forName("user.totalSubmit"));
		projection.add(Property.forName("user.number"));
		projection.add(Property.forName("u.firstName"));
		projection.add(Property.forName("u.middleName"));
		projection.add(Property.forName("u.lastName"));
		projection.add(Property.forName("corporation.id"));
		projection.add(Property.forName("u.id"));

		// projection
		// .add(Projections.alias(
		// Projections
		// .sqlProjection(
		// "(select count(*) from message_board s where s.inv_id={alias}.inv_id and s.mss_reply_date is null) as totalPending",
		// new String[] { "totalPending" },
		// new Type[] { new IntegerType() }),
		// "totalPending"), "totalPending");

		// projection
		// .add(Projections.alias(
		// Projections
		// .sqlProjection(
		// "(select case when mb.mss_reply_date is null then 2  else 1 end from message_board mb where mb.inv_id={alias}.inv_id order by mb.mss_date DESC  limit 1)  as haveE",
		// new String[] { "haveE" },
		// new Type[] { new IntegerType() }),
		// "haveE"), "haveE");

		return projection;

	}

	private Invoice formInvoiceObject(Object[] o) {
		Invoice invoice = null;
		if (o != null) {
			invoice = new Invoice();
			invoice.setId((String) o[0]);
			invoice.setStart((Date) o[1]);
			invoice.setEnd((Date) o[2]);
			invoice.setState(new InvoiceState((String) o[3], (String) o[4]));
			invoice.setAdminFee((Double) o[5]);
			invoice.setImportTotal((Double) o[6]);

			Profile profile = new Profile((String) o[7], new Corporation(
					(String) o[16], (String) o[8]));
			profile.setNumber((String) o[12]);
			profile.setTotalSubmit((Integer) o[11]);
			profile.setUser(new User((String) o[17], (String) o[13],
					(String) o[14], (String) o[15]));

			invoice.setUser(profile);
			invoice.setPayDate((Date) o[9]);
			invoice.setNumber((String) o[10]);

			// invoice.setHaveQuestion((Integer) o[16]);

		}
		return invoice;
	}

	/**
	 * Get current invoice by Invoice Id
	 * 
	 */
	@Override
	public Invoice getCurrentInvoice(String invoiceId) {

		Invoice invoice = null;
		if (StringUtils.isNotBlank(invoiceId)) {

			Criteria criteria = getSession().createCriteria(Invoice.class);
			criteria.createAlias("user", "user")
					.createAlias("state", "state")
					.createAlias("user.user", "u")
					.createAlias("user.corporation", "corporation",
							JoinType.LEFT_OUTER_JOIN);
			// get the user invoice
			criteria.add(Restrictions.eq("id", invoiceId));

			criteria.setProjection(getProjectionsCurrentInvoice());

			Object[] o = (Object[]) criteria.uniqueResult();
			invoice = formInvoiceObject(o);

		}

		return invoice;

	}

	/**
	 * update the field need updated to false when is exported
	 */
	@Override
	public void updateSendQuestion(Invoice invoice) {

		Query query = getSession().createQuery(
				"update Invoice p set p.haveQuestion=true where p.id =:id");

		query.setString("id", invoice.getId());

		query.executeUpdate();

	}

	/**
	 * Update the invoice's state
	 * 
	 * @param invoice
	 */
	@Override
	public void changeState(Invoice invoice, String state, User user) {
		Query query = getSession()
				.createQuery(
						"update Invoice set "
								+ (state.equals("2") ? "submitted "
										: "approval")
								+ " =:date, state.id=:state, updateDate=:date "
								+ (user != null && user.getId() != null ? ",userUpdated.id=:idUser"
										: "") + "  where id=:id ");
		query.setString("id", invoice.getId());
		query.setTimestamp("date", new Date());
		if (user != null && user.getId() != null)
			query.setString("idUser", user.getId());
		query.setString("state", state);

		query.executeUpdate();
	}

	/**
	 * Update the invoice's to state Submitted to all the international IBO
	 * 
	 * @param invoice
	 */
	@Override
	public int submitInternationInvoices() {
		Query query2 = getSession()
				.createQuery(
						"select i.id from Invoice i join i.user u where cast(i.startDate as date) < :date and u.typeContract ='INTERNATIONAL' and i.state = '1' ");
		query2.setDate("date", new Date());
		List<String> ids = query2.list();

		int total = 0;

		if (ids != null && ids.size() > 0) {

			Query query = getSession().createQuery(
					"update Invoice set submitted =:date, state.id=:state, updateDate=:date "
							+ "where id IN (:ids) ");

			query.setTimestamp("date", new Date());
			query.setString("state", InvoiceState.SUBMITTED);
			query.setParameterList("ids", ids);

			total = query.executeUpdate();
		}

		return total;
	}

	@Override
	public void updateAdminFee(Invoice invoice, User user) {
		Query query = getSession()
				.createQuery(
						"update Invoice set  adminFee=:adminFee, updateDate=:date,userUpdated.id=:idUser where id=:id ");
		query.setString("id", invoice.getId());
		query.setTimestamp("date", new Date());
		query.setString("idUser", user.getId());
		query.setDouble("adminFee", invoice.getAdminFee());

		query.executeUpdate();
	}

	@Override
	public void updateNotes(Invoice invoice, User user) {
		Query query = getSession()
				.createQuery(
						"update Invoice set  note=:note, updateDate=:date,userUpdated.id=:idUser where id=:id ");
		query.setString("id", invoice.getId());
		query.setTimestamp("date", new Date());
		query.setString("idUser", user.getId());
		query.setString("note", invoice.getNote());

		query.executeUpdate();
	}

	/**
	 * Search all the Ibos's invoices pending
	 * 
	 * @param idIbo
	 * @return
	 */
	@Override
	public List<Invoice> getDataPendingByIbo(String idIbo) {

		Criteria criteria = getSession().createCriteria(Invoice.class)
				.createAlias("user", "u").setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("u.user", FetchMode.JOIN);
		criteria.add(Restrictions.eq("state.id", "1"));
		criteria.add(Restrictions.eq("u.id", idIbo));

		return criteria.list();
	}

	/**
	 * Defined the invoice NOT send to oracle
	 * 
	 * @param invoice
	 */
	@Override
	public void resubmitInvoice(Invoice invoice, User user) {
		Query query = getSession()
				.createQuery(
						"update Invoice set  payProcessed=false, dateProcessed=null, updateDate=:date,userUpdated.id=:idUser  where id=:id ");
		query.setString("id", invoice.getId());
		query.setTimestamp("date", new Date());
		query.setString("idUser", user.getId());

		query.executeUpdate();
	}

	/**
	 * Load all the User data
	 */
	@Override
	public Invoice loadAllById(Invoice entity) {
		Criteria criteria = getSession().createCriteria(Invoice.class);
		criteria.createCriteria("state");
		criteria.createCriteria("userUpdated", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("user");
		criteria.createCriteria("user.user");
		criteria.createCriteria("user.corporation", JoinType.LEFT_OUTER_JOIN);
		criteria.setFetchMode("state", FetchMode.JOIN)
				.setFetchMode("userUpdated", FetchMode.JOIN)
				.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("user.user", FetchMode.JOIN)
				.setFetchMode("user.corporation", FetchMode.JOIN);

		criteria.add(Restrictions.eq("id", entity.getId()));

		return (Invoice) criteria.uniqueResult();
	}

	public int cancelInvoices(Date date) {

		Query query = getSession()
				.createQuery(
						"update Invoice set state.id = 4 where state.id = 1 and startDate < :date "
								+ "and hoursAdded = 0 and totalIncentive = 0 and serviceRevenue <=0 and actualService = 0 and importTotal <=0");

		query.setDate("date", date);

		int cantCanceled = query.executeUpdate();

		return cantCanceled;

	}

	// @Override
	// public List<Invoice> getDataForWeek(Date dateStartWeek) {
	// Query query = getSession().createQuery(
	// "select * from Invoice i where DATE(i.start) = :start");
	// query.setDate("start", dateStartWeek);
	// return query.list();
	// }

	@Override
	public Invoice getDataForWeek(Date dateStartWeek, String userId) {
		Query query = getSession()
				.createQuery(
						"select i from Invoice i join i.user p where DATE(i.start) = :start and p.user.id = :idUser and i.state.id IN ('1','2')");
		query.setDate("start", dateStartWeek);
		query.setString("idUser", userId);

		return (Invoice) query.uniqueResult();
	}

	@Override
	public Invoice getDataForEndWeek(Date dateEndWeek, String userId) {
		Query query = getSession()
				.createQuery(
						"select i from Invoice i join i.user p where DATE(i.end) = :end and p.user.id = :idUser and i.state.id IN ('1','2')");
		query.setDate("end", dateEndWeek);
		query.setString("idUser", userId);

		return (Invoice) query.uniqueResult();
	}

	@Override
	public Map<String, String> getDataForWeek(Date dateStartWeek) {
		// Query query = getSession()
		// .createQuery(
		// "select i.id,p.user.id from Invoice i join i.user p where DATE(i.start) >= :start and DATE(i.end) <= :start and i.state.id IN ('1','2')");

		Query query = getSession()
				.createQuery(
						"select i.id,p.user.id from Invoice i join i.user p where :start between DATE(i.start) and DATE(i.end) and i.state.id IN ('1','2')");

		query.setDate("start", dateStartWeek);
		List<Object[]> list = query.list();
		Map<String, String> mapInvoices = new HashMap<String, String>();
		for (Object[] o : list) {
			mapInvoices.put((String) o[1], (String) o[0]);
		}

		return mapInvoices;
	}

	public List<Invoice> getDataPendingOrSubmitted(String userId,
			Date dateInvoiceWork) {

		Query query = getSession()
				.createQuery(
						"select i from Invoice i join i.user p where i.state.id IN ('1','2') and p.user.id =:idUser and :date between i.start and i.end");
		query.setString("idUser", userId);
		query.setDate("date", dateInvoiceWork);

		return query.list();
	}
}