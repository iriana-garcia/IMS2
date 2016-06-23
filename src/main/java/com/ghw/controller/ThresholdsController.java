package com.ghw.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

import com.ghw.model.Thresholds;
import com.ghw.service.ThresholdsService;
import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class ThresholdsController implements Serializable {

	private Thresholds entity;

	private List<Thresholds> list;

	@ManagedProperty(value = "#{thresholdsService}")
	private ThresholdsService service;

	@ManagedProperty(value = "#{applicationBean}")
	private ApplicationBean applicationBean;

	public void onRowEdit(RowEditEvent event) {

		try {

			entity = (Thresholds) event.getObject();

			// add the minutes to hours
			if (entity.getId().equals("4")) {
				entity.setMin(entity.getHourMin()
						+ (new Double(entity.getMinutesMin()) / 60));
				entity.setMax(entity.getHourMax()
						+ (new Double(entity.getMinutesMax()) / 60));

				entity.calculate();
			}

			service.merge(entity);

			// updates ApplicationBean
			applicationBean.setThresholds(null);

			FacesMessage msg = new FacesMessage("Threshold Edited");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

			FacesContext.getCurrentInstance().validationFailed();
		}

	}

	public void onRowCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Edit Cancelled");
		FacesContext.getCurrentInstance().addMessage(null, msg);

		list = null;

		RequestContext.getCurrentInstance().execute("refreshTable()");

	}

	public Thresholds getEntity() {
		return entity;
	}

	public void setEntity(Thresholds entity) {
		this.entity = entity;
	}

	public ThresholdsService getService() {
		return service;
	}

	public void setService(ThresholdsService service) {
		this.service = service;
	}

	public List<Thresholds> getList() {
		if (list == null) {
			list = service.getData();
			for (Thresholds t : list) {
				t.calculate();
			}
		}
		return list;
	}

	public void setList(List<Thresholds> list) {
		this.list = list;
	}

	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

}
