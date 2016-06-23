package com.ghw.model;

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
import org.apache.commons.math.util.MathUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.ghw.util.DateUtil;

@Entity
@Table(name = "invoice_work")
public class InvoiceWork implements IEntityEditable {

	@Id
	@Column(name = "iwo_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "cli_id")
	private String cliId;

	@Column(name = "cli_name")
	private String cliName;

	@Column(name = "iwo_amount")
	private Double amount;

	@Column(name = "skill_id")
	private String skillId;

	@Column(name = "skill_name")
	private String skillName;

	@Column(name = "iwo_sch_start_time")
	private Date schStartTime;

	// used only for query
	@Column(name = "iwo_sch_start_time", updatable = false, insertable = false)
	@Type(type = "date")
	private Date schStartDate;

	@Column(name = "iwo_sch_end_time")
	private Date schEndTime;

	// total in seconds. state: Ready, Work, Talking, Reserved, Not ready in
	// some reason (Cisco: 32756, 32758, 32761, 32762) include buffer hours
	// @Column(name = "iwo_total_ready_time")
	// private Integer totalReadyTime;

	// total not ready time in hour
	@Column(name = "iwo_total_not_ready_time")
	private Double totalNotReadyTime;

	// total not ready time in miliseconds I save it only for see the difference
	// of convert the miliseconds to hours
	@Column(name = "iwo_total_not_ready_time_mili")
	private Integer totalNotReadyTimeMili;

	// hour add by buffer concept
	// @Column(name = "iwo_hours_buffer")
	// private Double hoursBuffer;

	// @Column(name = "iwo_hours_add")
	// private Double hoursAdded;

	// save total hours worked by IBO totalReadyTime
	@Column(name = "iwo_actual_service")
	private Double actualService;

	@Column(name = "iwo_actual_service_mili")
	private Integer actualServiceMili;

	@Transient
	private Double importTotal;

	// save actualService * rate
	@Column(name = "iwo_service_revenue")
	private Double serviceRevenue;

	@Column(name = "iwo_update_date")
	private Date updateDate;

	// buffer used to the calculation
	@Column(name = "iwo_buffer")
	private Integer buffer;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id")
	private User user;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@ManyToOne(targetEntity = Invoice.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "inv_id", nullable = true)
	private Invoice invoice;

	@ManyToOne(targetEntity = Skill.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "skill_id", nullable = true, insertable = false, updatable = false)
	private Skill skill;

	@OneToMany(mappedBy = "invoiceWork", fetch = FetchType.LAZY)
	private List<InvoiceHoursAdded> hoursAddeds = new ArrayList<InvoiceHoursAdded>();

	@Transient
	private String fieldAdicional = "";

	/**
	 * Field used in Finance export
	 * 
	 * @return
	 */
	@Transient
	private Double hoursExtra;

	public String getSchStartTimeFormat() {
		return DateUtil.dateFormatMinutes(schStartTime);
	}

	public String getSchEndTimeFormat() {
		return DateUtil.dateFormatMinutes(schEndTime);
	}

	public Double getTotalReadyHours() {

		// return MathUtils.round(new Double(actualService) / (60 * 60), 2);

		return MathUtils.round(actualService, 2);
	}

	public Double getTotalHours() {

		return MathUtils.round(getTotalReadyHours() + getHoursAdded(), 2);
	}

	public Double getTotalHoursFinance() {

		return MathUtils.round(getTotalReadyHours() + getHoursExtra(), 2);
	}

	public Double getTotalNotReadyHours() {

		// return MathUtils.round(new Double(totalNotReadyTime) / (60 * 60), 2);
		return MathUtils.round(new Double(totalNotReadyTime), 2);
	}

	public Double getLoggedTime() {

		return MathUtils.round(getTotalReadyHours() + getTotalNotReadyHours(),
				2);
	}

	public String getDescriptionTask() {
		return StringUtils.isNotBlank(cliName) ? cliName : skillName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCliId() {
		return cliId;
	}

	public void setCliId(String cliId) {
		this.cliId = cliId;
	}

	public String getCliName() {
		return cliName;
	}

	public void setCliName(String cliName) {
		this.cliName = cliName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public Date getSchStartTime() {
		return schStartTime;
	}

	public void setSchStartTime(Date schStartTime) {
		this.schStartTime = schStartTime;
	}

	public Date getSchEndTime() {
		return schEndTime;
	}

	public void setSchEndTime(Date schEndTime) {
		this.schEndTime = schEndTime;
	}

	public Double getTotalNotReadyTime() {
		return totalNotReadyTime;
	}

	public void setTotalNotReadyTime(Double totalNotReadyTime) {
		this.totalNotReadyTime = totalNotReadyTime;
	}

	public Double getHoursAdded() {
		Double hours = 0.0;
		if (hoursAddeds != null) {
			for (InvoiceHoursAdded added : hoursAddeds) {
				hours += added.getHours() == null ? 0.0 : added.getHours();
			}
		}

		return hours;
	}

	public Double getActualService() {
		return actualService;
	}

	public void setActualService(Double actualService) {
		this.actualService = actualService;
	}

	public Double getImportTotal() {
		importTotal = (getTotalReadyHours() + getHoursAdded()) * amount;
		return importTotal;
	}

	public Double getImportTotalFinance() {
		importTotal = (getTotalReadyHours() + getHoursExtra()) * amount;
		return importTotal;
	}

	public void setImportTotal(Double importTotal) {
		this.importTotal = importTotal;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getBuffer() {
		return buffer;
	}

	public void setBuffer(Integer buffer) {
		this.buffer = buffer;
	}

	public User getUserUpdated() {
		return userUpdated;
	}

	public void setUserUpdated(User userUpdated) {
		this.userUpdated = userUpdated;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
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
		if (!(obj instanceof InvoiceWork))
			return false;
		InvoiceWork other = (InvoiceWork) obj;
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

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
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

	public List<InvoiceHoursAdded> getHoursAddeds() {
		return hoursAddeds;
	}

	public void setHoursAddeds(List<InvoiceHoursAdded> hoursAddeds) {
		this.hoursAddeds = hoursAddeds;
	}

	public Double getServiceRevenue() {
		return serviceRevenue;
	}

	public void setServiceRevenue(Double serviceRevenue) {
		this.serviceRevenue = serviceRevenue;
	}

	public Date getSchStartDate() {
		return schStartDate;
	}

	public void setSchStartDate(Date schStartDate) {
		this.schStartDate = schStartDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getTotalNotReadyTimeMili() {
		return totalNotReadyTimeMili;
	}

	public void setTotalNotReadyTimeMili(Integer totalNotReadyTimeMili) {
		this.totalNotReadyTimeMili = totalNotReadyTimeMili;
	}

	public Integer getActualServiceMili() {
		return actualServiceMili;
	}

	public void setActualServiceMili(Integer actualServiceMili) {
		this.actualServiceMili = actualServiceMili;
	}

	public void copy(InvoiceWork invoiceWork) {
		setBuffer(invoiceWork.getBuffer());
		setAmount(invoiceWork.getAmount());
		setTotalNotReadyTime(0.0);

		setSkillId(invoiceWork.getSkillId());
		setSkillName(invoiceWork.getSkillName());
		setCliId(invoiceWork.getCliId());
		setCliName(invoiceWork.getCliName());

		setSchStartTime(invoiceWork.getSchStartTime());
		setSchEndTime(invoiceWork.getSchEndTime());

		user = invoiceWork.getUser();

	}

	@Override
	public void setCreatedDate(Date date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUserCreated(User user) {
		// TODO Auto-generated method stub

	}

	public Double getHoursExtra() {
		hoursExtra = hoursExtra == null ? 0 : hoursExtra;
		return hoursExtra;
	}

	public void setHoursExtra(Double hoursExtra) {
		this.hoursExtra = hoursExtra;
	}

}
