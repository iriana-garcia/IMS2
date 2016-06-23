package com.ghw.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.hibernate.annotations.Type;

import com.ghw.util.DateUtil;

@Entity
@Table(name = "agent_state_detail")
public class AgentStateDetail implements IEntity {

	@Id
	@Column(name = "asd_id", length = 36, columnDefinition = "CHAR(36)")
	private String id;

	@Column(name = "asd_date_start", nullable = false, updatable = false)
	private Date startDate;

	@Column(name = "asd_date_end", nullable = true, updatable = false)
	private Date endDate;

	@Column(name = "asd_place", updatable = false)
	private Short place = 1;

	@Column(name = "asd_event_type", updatable = false)
	private Integer eventType;

	@Column(name = "asd_reason_code", updatable = false)
	private Integer reasonCode;

	@Column(name = "asd_reason_code_description", updatable = false)
	private String reasonCodeDescription;

	// the time is in miliseconds
	@Column(name = "asd_duration", updatable = false)
	private Integer duration;

	// the time is in miliseconds that the schedule don't take
	@Column(name = "asd_duration_pending")
	private Integer durationPending;

	// save if was updated in the compare can be for new register or for update
	@Column(name = "asd_need_updated", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean needUpdated = false;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id", updatable = false)
	private User user;

	@ManyToOne(targetEntity = InvoiceWork.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "iwo_id")
	private InvoiceWork invoiceWork;

	/**
	 * save the LAST invoice work that take the duration pending
	 */
	@ManyToOne(targetEntity = InvoiceWork.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "iwo_id_pending")
	private InvoiceWork invoiceWorkPending;

	@Transient
	private boolean associateInvoice = false;

	public String getEventTypeName() {
		String event = "";
		switch (eventType) {
		case 1: {
			event = "Log in";
			break;
		}
		case 2: {
			event = "Not ready";
			break;
		}
		case 3: {
			event = "Ready";
			break;
		}
		case 4: {
			event = "Reserved";
			break;
		}
		case 5: {
			event = "Talking";
			break;
		}
		case 6: {
			event = "Work";
			break;
		}
		case 7: {
			event = "Log Out";
			break;
		}
		}
		return event;
	}

	public String getDurationHours() {

		if (eventType != 1 && eventType != 7) {

			int seconds = (int) (duration / 1000) % 60;
			int minutes = (int) ((duration / (1000 * 60)) % 60);
			int hours = (int) ((duration / (1000 * 60 * 60)) % 24);

			return hours + " hrs " + minutes + " min " + seconds + " sec";

		} else
			return "";
	}

	public String getStateDescription() {
		return StringUtils.isNotBlank(reasonCodeDescription) ? reasonCodeDescription
				: (reasonCode == null || eventType == 1 || eventType == 7 ? ""
						: reasonCode.toString());
	}

	public Double getDurationOnlyHours() {

		return duration == null || duration == 0 ? 0.0 : MathUtils.round(
				duration / new Double(60 * 60 * 1000), 2);
	}

	public String getPlaceName() {
		switch (place) {
		case 1:
			return "inContact";
		case 2:
			return "Cisco";
		}
		return "";
	}

	public String getStartDateFormat() {
		return DateUtil.dateFormatSeconds(startDate);
	}

	public String getEndDateFormat() {
		return DateUtil.dateFormatSeconds(endDate);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Short getPlace() {
		return place;
	}

	public void setPlace(Short place) {
		this.place = place;
	}

	public Integer getEventType() {
		return eventType;
	}

	public void setEventType(Integer eventType) {
		this.eventType = eventType;
	}

	public Integer getReasonCode() {
		return reasonCode;
	}

	public boolean isReasonCodeWorking(List<ReasonCodePay> reasonCodePays) {
		if (reasonCode != null)
			for (ReasonCodePay reasonCodePay : reasonCodePays) {
				if (place.equals(reasonCodePay.getPlace())
						&& reasonCode.equals(reasonCodePay.getCode()))
					return true;
			}

		return false;
		// 32756, 32758, 32761, 32762
		// return place.equals(Place.cisco)
		// && (reasonCode.equals(32756) || reasonCode.equals(32758)
		// || reasonCode.equals(32761) || reasonCode.equals(32762));
	}

	public void setReasonCode(Integer reasonCode) {
		this.reasonCode = reasonCode;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public boolean isNeedUpdated() {
		return needUpdated;
	}

	public void setNeedUpdated(boolean needUpdated) {
		this.needUpdated = needUpdated;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public InvoiceWork getInvoiceWork() {
		return invoiceWork;
	}

	public void setInvoiceWork(InvoiceWork invoiceWork) {
		this.invoiceWork = invoiceWork;
	}

	public boolean isAssociateInvoice() {
		return associateInvoice;
	}

	public void setAssociateInvoice(boolean associateInvoice) {
		this.associateInvoice = associateInvoice;
	}

	public Integer getDurationPending() {
		return durationPending;
	}

	public void setDurationPending(Integer durationPending) {
		this.durationPending = durationPending;
	}

	public InvoiceWork getInvoiceWorkPending() {
		return invoiceWorkPending;
	}

	public void setInvoiceWorkPending(InvoiceWork invoiceWorkPending) {
		this.invoiceWorkPending = invoiceWorkPending;
	}

	public String getReasonCodeDescription() {
		return reasonCodeDescription;
	}

	public void setReasonCodeDescription(String reasonCodeDescription) {
		this.reasonCodeDescription = reasonCodeDescription;
	}

	public AgentStateDetail() {
		super();
	}

	public AgentStateDetail(String id, Date startDate, Date endDate,
			Short place, Integer eventType, Integer reasonCode,
			Integer duration, boolean needUpdated, String invoiceWorkId,
			String reasonCodeDescription) {
		super();
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.place = place;
		this.eventType = eventType;
		this.reasonCode = reasonCode;
		this.duration = duration;
		this.needUpdated = needUpdated;
		this.associateInvoice = StringUtils.isNotBlank(invoiceWorkId);
		this.reasonCodeDescription = reasonCodeDescription;
	}

	@Override
	public String getIdentity() {
		return id;
	}
}