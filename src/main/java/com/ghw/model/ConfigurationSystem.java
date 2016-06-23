package com.ghw.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "configuration_system")
public class ConfigurationSystem implements IEntityEditable, Serializable {

	@Id
	@Column(name = "sys_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "sys_decimal_place")
	public Integer decimal = 2;

	@Column(name = "sys_admin_fee")
	private Double adminFee;

	@Column(name = "sys_buffer")
	private Integer buffer;

	@Column(name = "sys_type_year")
	private String typeYear;

	@Column(name = "sys_begin_month")
	private Integer beginMonth;

	@Column(name = "sys_invoice_frecuency")
	private String invoiceFrecuency;

	@Column(name = "sys_day_pay")
	private String dayPay;

	@Column(name = "sys_color")
	private String color;

	@Column(name = "sys_font_color")
	private String fontColor;

	@Column(name = "sys_date_start", nullable = false)
	private Date start;

	@Column(name = "sys_date_end", nullable = false)
	private Date end;

	@Column(name = "sys_total_submit", nullable = false)
	private Integer totalSubmitInvoice = 2;

	@Column(name = "sys_paypal_file_limit")
	private Double paypalFileLimit;

	@Column(name = "sys_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getAdminFee() {
		return adminFee;
	}

	public void setAdminFee(Double adminFee) {
		this.adminFee = adminFee;
	}

	public String getTypeYear() {
		return typeYear;
	}

	public void setTypeYear(String typeYear) {
		this.typeYear = typeYear;
	}

	public Integer getBeginMonth() {
		return beginMonth;
	}

	public void setBeginMonth(Integer beginMonth) {
		this.beginMonth = beginMonth;
	}

	public String getInvoiceFrecuency() {
		return invoiceFrecuency;
	}

	public void setInvoiceFrecuency(String invoiceFrecuency) {
		this.invoiceFrecuency = invoiceFrecuency;
	}

	public String getDayPay() {
		return dayPay;
	}

	public void setDayPay(String dayPay) {
		this.dayPay = dayPay;
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

	@Override
	public String getIdentity() {
		return id;
	}

	public Integer getBuffer() {
		return buffer;
	}

	public void setBuffer(Integer buffer) {
		this.buffer = buffer;
	}

	@Override
	public String getFieldAdicional() {
		return "";
	}

	@Override
	public String toString() {
		return "";
	}

	public String getTypeYearDescription() {
		return StringUtils.isNotBlank(typeYear) ? TypeYear.valueOf(
				TypeYear.class, typeYear).getValor() : "";
	}

	public String getInvoiceFrecuencyDescription() {
		return StringUtils.isNotBlank(invoiceFrecuency) ? InvoiceFrequency
				.valueOf(InvoiceFrequency.class, invoiceFrecuency).getValor()
				: "";
	}

	public String getDayPayDescription() {
		return StringUtils.isNotBlank(dayPay) ? DayWeek.valueOf(DayWeek.class,
				dayPay).getValor() : "";
	}

	@Override
	public int compare(Object o2) {
		return 0;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public Integer getDecimal() {
		return decimal;
	}

	public void setDecimal(Integer decimal) {
		this.decimal = decimal;
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

	public String getStartDateFormat() {
		return start == null ? "" : (new SimpleDateFormat("EEEE, h:mm a"))
				.format(start);
	}

	public String getEndDateFormat() {
		return end == null ? "" : (new SimpleDateFormat("EEEE, h:mm a"))
				.format(end);
	}

	public Integer getTotalSubmitInvoice() {
		return totalSubmitInvoice;
	}

	public void setTotalSubmitInvoice(Integer totalSubmitInvoice) {
		this.totalSubmitInvoice = totalSubmitInvoice;
	}

	public Double getPaypalFileLimit() {
		return paypalFileLimit;
	}

	public void setPaypalFileLimit(Double paypalFileLimit) {
		this.paypalFileLimit = paypalFileLimit;
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
