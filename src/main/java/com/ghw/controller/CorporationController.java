package com.ghw.controller;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;

import com.ghw.datamodel.CorporationDataModel;
import com.ghw.model.Corporation;
import com.ghw.model.ProblemCorporation;
import com.ghw.model.Profile;
import com.ghw.service.CorporationService;
import com.ghw.service.ProfileService;
import com.ghw.util.Context;

@ManagedBean
@ViewScoped
public class CorporationController extends
		Controller<Corporation, String, CorporationService> {

	@ManagedProperty(value = "#{corporationDataModel}")
	private CorporationDataModel lazyModel;

	@ManagedProperty(value = "#{CorporationService}")
	private CorporationService corporationService;

	private Corporation entity;

	private List<Profile> listIbosCorporation = null;
	// is the new Principal IBO selected from listIbosCorporation
	private Profile newPrincipalIBO;

	@ManagedProperty(value = "#{profileService}")
	private ProfileService profileService;

	public CorporationController() {

	}

	/**
	 * Clear the datatable filter
	 */
	@Override
	public void clearFilter() {

		super.clearFilter();

		SelectOneMenu selectOne = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbProblem");
		if (selectOne != null) {
			selectOne.resetValue();
		}

	}

	public String fixProblem() {
		try {

			if (entity != null) {
				switch (entity.getProblemId()) {
				case "A":
					RequestContext.getCurrentInstance().execute(
							"PF('dlgBankNotFound').show()");
					break;
				case "B":
				case "AB":
					listIbosCorporation = profileService
							.getDataByCorporationActive(entity.getId());

					RequestContext.getCurrentInstance().execute(
							"PF('dlgIboCorporation').show()");
					break;
				}
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

		return null;
	}

	public void saveIboCorporation() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			if (newPrincipalIBO == null) {
				throw new Exception("ibo_required");
			}
			// update the corporation with the new IBO principal to disabled the
			// control "owner type"

			profileService.updateIBOToPrincipal(newPrincipalIBO);

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}
	}

	public void cancelIboCorporation() {
		try {

			newPrincipalIBO = null;

			RequestContext.getCurrentInstance().execute(
					"PF('tblIboCorporation').unselectAllRows()");

		} catch (Exception e) {
			Context.addInfoMessageFromMSG(e.getMessage());
		}
	}

	public SelectItem[] getProblemList() {
		ProblemCorporation[] listProblem = ProblemCorporation.values();
		SelectItem[] problems = new SelectItem[listProblem.length];
		for (int i = 0; i < listProblem.length; i++) {

			SelectItem select = new SelectItem(
					ProblemCorporation.valueOf(listProblem[i].name()),
					ProblemCorporation.valueOf(listProblem[i].name())
							.getValor());
			problems[i] = select;
		}
		return problems;
	}

	public CorporationDataModel getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(CorporationDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public CorporationService getCorporationService() {
		return corporationService;
	}

	public void setCorporationService(CorporationService corporationService) {
		this.corporationService = corporationService;
	}

	public Corporation getEntity() {
		return entity;
	}

	public void setEntity(Corporation entity) {
		this.entity = entity;
	}

	public List<Profile> getListIbosCorporation() {
		return listIbosCorporation;
	}

	public void setListIbosCorporation(List<Profile> listIbosCorporation) {
		this.listIbosCorporation = listIbosCorporation;
	}

	public Profile getNewPrincipalIBO() {
		return newPrincipalIBO;
	}

	public void setNewPrincipalIBO(Profile newPrincipalIBO) {
		this.newPrincipalIBO = newPrincipalIBO;
	}

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

}
