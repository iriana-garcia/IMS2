package com.ghw.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Table(name = "invoice")
public class Invoice implements IEntityEditable {

	@Id
	@Column(name = "inv_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "inv_note")
	private String note;

	// save if the invoice was sending or not to oracle
	@Column(name = "inv_pay_processed", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean payProcessed = false;

	@Column(name = "inv_date_processed")
	private Date dateProcessed;

	@Column(name = "inv_start", nullable = false)
	private Date start;

	@Column(name = "inv_end", nullable = false)
	private Date end;

	// used only for query
	@Column(name = "inv_start", updatable = false, insertable = false)
	@Type(type = "date")
	private Date startDate;

	// datetime when the IBO submit the invoice
	@Column(name = "inv_date_submitted")
	private Date submitted;

	// datetime when the IBOC approval the invoice
	@Column(name = "inv_date_approval")
	private Date approval;

	// date when the IBO recive the payment
	@Column(name = "inv_pay_date")
	private Date payDate;

	// admin fee exists in system in the moment to create the invoice
	@Column(name = "inv_admin_fee")
	private Double adminFee;

	// SUM total hours added to the invoice
	@Column(name = "inv_hours_added")
	private Double hoursAdded;

	// SUM total incentive added to the invoice
	@Column(name = "inv_total_incentive")
	private Double totalIncentive;

	// SUM total import ton actual service concept, NOT included hours added,
	// incentive, admin fee
	@Column(name = "inv_service_revenue")
	private Double serviceRevenue;

	// SUM hours works in phone system, NOT included hours added
	@Column(name = "inv_actual_service")
	private Double actualService;

	// Invoice Import total = ((SUM(hours actual service) + SUM(hours
	// added))*rate)+SUM(incentive)-Admin fee
	@Column(name = "inv_import_total")
	private Double importTotal;

	// invoice number
	@Column(name = "inv_number")
	private String number;

	@ManyToOne(targetEntity = InvoiceState.class)
	@JoinColumn(name = "ivt_id", nullable = true)
	private InvoiceState state;

	@ManyToOne(targetEntity = Profile.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_id", nullable = true)
	private Profile user;

	@Column(name = "inv_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	private List<InvoiceWork> invoiceWork = new ArrayList<InvoiceWork>();

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	private List<Incentive> incentive = new ArrayList<Incentive>();

	@Transient
	private Integer haveQuestion = null;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	private List<MessageBoard> messageBoard = new ArrayList<MessageBoard>();

	@Transient
	private boolean havePendingReply = false;

	@Transient
	private List<AgentStateDetail> phoneSystemDetail = new ArrayList<AgentStateDetail>();

	@Transient
	private String fieldAdicional;

	@Transient
	private List<ProblemInvoice> problemInvoices = new ArrayList<ProblemInvoice>();
	/**
	 * Analize the threshold
	 * 
	 */
	@Transient
	private Map<String, Thresholds> thresholds = new HashMap<String, Thresholds>();

	@Transient
	private String thrTotalHours, thrServiceAmount, thrImportTotal,
			thrIncentive;

	@Transient
	private boolean hasThrTotalHours = false, hasthrServiceAmount = false,
			hasThrImportTotal = false, hasThrIncentive = false;

	public boolean isReadySubmit() {
		return state != null
				&& (state.getId().equals("2") || state.getId().equals("3"));
	}

	public String getButtonDescription() {

		if (haveQuestion == null)
			return "Message Board";
		else if (haveQuestion == 1)
			return "Replied";
		else if (haveQuestion == 2)
			return "Waiting Reply";
		else
			return "Message Board";
	}

	public void recalculateImport() {
		Double totalImport = 0.0;
		for (InvoiceWork iw : getInvoiceWork()) {
			totalImport += (iw.getTotalReadyHours() + iw.getHoursAdded())
					* iw.getAmount();
		}

		for (Incentive ic : getIncentive()) {
			totalImport += ic.getAmount();
		}

		setImportTotal(totalImport - getAdminFee());
	}

	public String getNoteDescription() {

		return StringUtils.isNotBlank(note) ? "Y" : "N";
	}

	public String getQuestionStatus() {
		String questionStatus = "N";
		if (haveQuestion != null)
			questionStatus = haveQuestion.equals(1) ? "Y" : "R";
		return questionStatus;
	}

	public String getQuestionStatusToolTip() {
		String questionStatus = "No";
		if (haveQuestion != null)
			questionStatus = haveQuestion.equals(1) ? "Yes" : "Replied";
		return questionStatus;
	}

	public boolean isEditInvoice() {
		return state != null && state.getId() != null
				&& (state.getId().equals("1") || state.getId().equals("2"));
	}

	public String getDateUpdatedFormat() {
		return updateDate != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(updateDate) : "";
	}

	public String getStartDateFormat() {
		return start == null ? "" : (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(start);
	}

	public String getEndDateFormat() {
		return end == null ? "" : (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(end);
	}

	public String getSubmmitedDateFormat() {
		return submitted == null ? "" : (new SimpleDateFormat(
				"E, M-d-yy h:mm a")).format(submitted);
	}

	public String getApprovalDateFormat() {
		return approval == null ? ""
				: (new SimpleDateFormat("E, M-d-yy h:mm a")).format(approval);
	}

	public String getPayDateFormat() {
		return payDate == null ? "" : (new SimpleDateFormat("E, M-d-yy"))
				.format(payDate);
	}

	public String getDateProcessedFormat() {
		return dateProcessed == null ? "" : (new SimpleDateFormat(
				"E, M-d-yy h:mm a")).format(dateProcessed);
	}

	public String getDateRange() {
		return start != null && end != null ? (new SimpleDateFormat("MM-dd-yy"))
				.format(start)
				+ " to "
				+ (new SimpleDateFormat("MM-dd-yy")).format(end) : "";
	}

	public String getLastDateSubmitted() {
		if (approval != null)
			return (new SimpleDateFormat("E, M-d-yy h:mm a")).format(approval);
		if (submitted != null)
			return (new SimpleDateFormat("E, M-d-yy h:mm a")).format(submitted);
		return "";

	}

	/**
	 * Constructors
	 * 
	 * @return
	 */
	public Invoice() {
		super();
	}

	public Invoice(Double importTotal, String email, String id) {
		super();
		this.importTotal = importTotal;
		User user = new User();
		user.setEmail(email);
		this.user = new Profile(user);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isPayProcessed() {
		return payProcessed;
	}

	public void setPayProcessed(boolean payProcessed) {
		this.payProcessed = payProcessed;
	}

	public Date getDateProcessed() {
		return dateProcessed;
	}

	public void setDateProcessed(Date dateProcessed) {
		this.dateProcessed = dateProcessed;
	}

	public List<Incentive> getIncentive() {
		return incentive;
	}

	public void setIncentive(List<Incentive> incentive) {
		this.incentive = incentive;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public InvoiceState getState() {
		return state;
	}

	public void setState(InvoiceState state) {
		this.state = state;
	}

	public Date getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}

	public Date getApproval() {
		return approval;
	}

	public void setApproval(Date approval) {
		this.approval = approval;
	}

	public Double getAdminFee() {
		return adminFee;
	}

	public void setAdminFee(Double adminFee) {
		this.adminFee = adminFee;
	}

	public Double getActualService() {
		return actualService;
	}

	public void setActualService(Double actualService) {
		this.actualService = actualService;
	}

	public Double getImportTotal() {
		return importTotal;
	}

	public void setImportTotal(Double importTotal) {
		this.importTotal = importTotal;
	}

	public Profile getUser() {
		return user;
	}

	public void setUser(Profile user) {
		this.user = user;
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

	public List<InvoiceWork> getInvoiceWork() {
		return invoiceWork;
	}

	public void setInvoiceWork(List<InvoiceWork> invoiceWork) {
		this.invoiceWork = invoiceWork;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	@Override
	public int compare(Object oldObj) {
		return 0;
	}

	public Map<String, Thresholds> getThresholds() {
		return thresholds;
	}

	public void setThresholds(Map<String, Thresholds> thresholds) {
		this.thresholds = thresholds;
	}

	public List<Thresholds> getListThresholds() {

		return new ArrayList<Thresholds>(getThresholds().values());
	}

	public String getThrIncentive() {
		return thrIncentive;
	}

	public void setThrIncentive(String thrIncentive) {
		this.thrIncentive = thrIncentive;
	}

	public boolean isHasThrIncentive() {
		return hasThrIncentive;
	}

	public void setHasThrIncentive(boolean hasThrIncentive) {
		this.hasThrIncentive = hasThrIncentive;
	}

	public String getThrImportTotal() {
		return thrImportTotal;
	}

	public void setThrImportTotal(String thrImportTotal) {
		this.thrImportTotal = thrImportTotal;
	}

	public boolean isHasThrImportTotal() {
		return hasThrImportTotal;
	}

	public void setHasThrImportTotal(boolean hasThrImportTotal) {
		this.hasThrImportTotal = hasThrImportTotal;
	}

	public String getThrTotalHours() {
		return thrTotalHours;
	}

	public void setThrTotalHours(String thrTotalHours) {
		this.thrTotalHours = thrTotalHours;
	}

	public boolean isHasThrTotalHours() {
		return hasThrTotalHours;
	}

	public void setHasThrTotalHours(boolean hasThrTotalHours) {
		this.hasThrTotalHours = hasThrTotalHours;
	}

	public String getThrServiceAmount() {
		return thrServiceAmount;
	}

	public void setThrServiceAmount(String thrServiceAmount) {
		this.thrServiceAmount = thrServiceAmount;
	}

	public boolean isHasthrServiceAmount() {
		return hasthrServiceAmount;
	}

	public void setHasthrServiceAmount(boolean hasthrServiceAmount) {
		this.hasthrServiceAmount = hasthrServiceAmount;
	}

	public Double getHoursAdded() {
		return hoursAdded;
	}

	public void setHoursAdded(Double hoursAdded) {
		this.hoursAdded = hoursAdded;
	}

	public Double getTotalIncentive() {
		return totalIncentive;
	}

	public void setTotalIncentive(Double totalIncentive) {
		this.totalIncentive = totalIncentive;
	}

	public Double getServiceRevenue() {
		return serviceRevenue;
	}

	public void setServiceRevenue(Double serviceRevenue) {
		this.serviceRevenue = serviceRevenue;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Integer getHaveQuestion() {
		return haveQuestion;
	}

	public void setHaveQuestion(Integer haveQuestion) {
		this.haveQuestion = haveQuestion;
	}

	public List<MessageBoard> getMessageBoard() {
		return messageBoard;
	}

	public void setMessageBoard(List<MessageBoard> messageBoard) {
		this.messageBoard = messageBoard;
	}

	public List<AgentStateDetail> getPhoneSystemDetail() {
		return phoneSystemDetail;
	}

	public void setPhoneSystemDetail(List<AgentStateDetail> phoneSystemDetail) {
		this.phoneSystemDetail = phoneSystemDetail;
	}

	public boolean isHavePendingReply() {
		havePendingReply = haveQuestion != null && haveQuestion.equals(2);
		return havePendingReply;
	}

	public void setHavePendingReply(boolean havePendingReply) {
		this.havePendingReply = havePendingReply;
	}

	public List<ProblemInvoice> getProblemInvoices() {
		return problemInvoices;
	}

	public void setProblemInvoices(List<ProblemInvoice> problemInvoices) {
		this.problemInvoices = problemInvoices;
	}

	@Override
	public void setCreatedDate(Date date) {
	}

	@Override
	public void setUserCreated(User user) {
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
		if (!(obj instanceof Invoice))
			return false;
		Invoice other = (Invoice) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	@Override
	public String toString() {

		return "Start date: " + getStartDateFormat() + " End date: "
				+ getEndDateFormat() + " Number: " + number + " Import Total: "
				+ importTotal;
	}
}
