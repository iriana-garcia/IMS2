package com.ghw.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "thresholds")
public class Thresholds implements IEntityEditable, Serializable {

	@Id
	@Column(name = "thr_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "thr_metric", nullable = false)
	private String metric;

	@Column(name = "thr_description", nullable = false)
	private String description;

	@Column(name = "thr_min", nullable = false)
	private Double min;

	@Column(name = "thr_max", nullable = false)
	private Double max;

	@Column(name = "thr_color")
	private String color;

	@Column(name = "thr_font_color")
	private String fontColor;

	@Column(name = "thr_update_date")
	private Date updateDate;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id_updated", nullable = true)
	private User userUpdated;

	@Transient
	private String fieldAdicional = "";

	@Transient
	private Integer minutesMin = 0, minutesMax = 0;
	@Transient
	private Double hourMin = 0.0, hourMax = 0.0;

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	@Override
	public String getFieldAdicional() {
		return fieldAdicional;
	}

	@Override
	public String toString() {
		return "Metric: " + metric + " Min: " + min + " Max: " + max;
	}

	@Override
	public int compare(Object o2) {
		Thresholds o = (Thresholds) o2;
		fieldAdicional = "";

		if (getMin().doubleValue() != o.getMin().doubleValue())
			fieldAdicional += " Old min: " + o.getMin();

		if (getMax().doubleValue() != o.getMax().doubleValue())
			fieldAdicional += " Old max: " + o.getMax();

		return StringUtils.isNoneBlank(fieldAdicional) ? 1 : 0;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public boolean isHasValues() {

		return !(id.equals("5") || id.equals("6"));
	}

	public String getMinFormat() {

		DecimalFormat myFormatter = new DecimalFormat(
				"##,##,##,##,##,##,##0.00");

		switch (id) {
		case "1":
		case "2":
		case "3":
			return "$" + myFormatter.format(min);
		case "4":// Total Hours
		{

			Double minute = min * 60;

			Double hours = Math.floor(minute / 60);
			Double minutes = minute % 60;

			return hours.longValue() + " hrs " + minutes.longValue() + " min";
		}
		case "5":
		case "6":
			return "";
		case "8":
			return myFormatter.format(min) + "%";
		}
		return min.toString();
	}

	public String getMaxFormat() {

		DecimalFormat myFormatter = new DecimalFormat(
				"##,##,##,##,##,##,##0.00");

		switch (id) {
		case "1":
		case "2":
		case "3":
			return "$" + myFormatter.format(max);
		case "4":// Total Hours
		{

			Double minute = max * 60;

			Double hours = Math.floor(minute / 60);
			Double minutes = minute % 60;

			return hours.longValue() + " hrs " + minutes.longValue() + " min";
		}
		case "5":
		case "6":
			return "";
		case "8":
			return myFormatter.format(max) + "%";
		}
		return min.toString();
	}

	public Integer getMinutesMin() {
		return minutesMin;
	}

	public void setMinutesMin(Integer minutesMin) {
		this.minutesMin = minutesMin;
	}

	public Integer getMinutesMax() {
		return minutesMax;
	}

	public void setMinutesMax(Integer minutesMax) {
		this.minutesMax = minutesMax;
	}

	public Double getHourMin() {
		return hourMin;
	}

	public void setHourMin(Double hourMin) {
		this.hourMin = hourMin;
	}

	public Double getHourMax() {
		return hourMax;
	}

	public void setHourMax(Double hourMax) {
		this.hourMax = hourMax;
	}

	public void calculate() {
		if (min != null && max != null) {
			minutesMin = new Double((min * 60) % 60).intValue();
			hourMin = new Double(Math.floor((min * 60) / 60));

			minutesMax = new Double((max * 60) % 60).intValue();
			hourMax = new Double(Math.floor((max * 60) / 60));
		}

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
