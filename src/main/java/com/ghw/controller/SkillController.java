package com.ghw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.ghw.datamodel.SkillDataModel;
import com.ghw.model.ClientApplication;
import com.ghw.model.Skill;
import com.ghw.model.SkillPhoneSystem;
import com.ghw.service.SkillPhoneSystemService;
import com.ghw.service.SkillService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class SkillController extends Controller<Skill, String, SkillService> {

	@ManagedProperty(value = "#{skillDataModel}")
	private SkillDataModel lazyModel;

	@ManagedProperty(value = "#{skillService}")
	private SkillService service;

	@ManagedProperty(value = "#{skillPhoneSystemService}")
	private SkillPhoneSystemService phoneSystemService;

	private ClientApplication ca;

	// fill the skill pickList
	private DualListModel<SkillPhoneSystem> skills = new DualListModel<SkillPhoneSystem>(
			new ArrayList<SkillPhoneSystem>(),
			new ArrayList<SkillPhoneSystem>());

	// used to view detail
	private List<Skill> listDetail = new ArrayList<Skill>();
	private Integer position = 0;

	@PostConstruct
	public void init() {
		try {

			// if is the list page call the method the create the default filter
			if (objectUtil.getEdit() == null) {
				initFilter();
			}

			// if I'm editing or adding a ClientApplication
			if (objectUtil.getEdit() != null) {
				setEdit(objectUtil.getEdit() == 2);

				List<SkillPhoneSystem> skillSource = new ArrayList<SkillPhoneSystem>();
				List<SkillPhoneSystem> skillTarget = new ArrayList<SkillPhoneSystem>();

				// if editing
				if (objectUtil.getEdit() == 2) {

					setEntity((Skill) objectUtil.getObject());

					ca = entity.getClientApplication();

					// // get the skills associate
					List<SkillPhoneSystem> list = phoneSystemService
							.getDataWithoutSkills(entity);
					for (SkillPhoneSystem s : list) {
						if (s.getSkill() != null
								&& StringUtils.isNotBlank(s.getSkill().getId())) {
							skillTarget.add(s);
						} else {
							skillSource.add(s);
						}
					}

				} else {

					skillSource = phoneSystemService.getDataWithoutSkills();

				}

				skills = new DualListModel<SkillPhoneSystem>(skillSource,
						skillTarget);

			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

		} finally {
			objectUtil.clean();
		}
	}

	/**
	 * Clear the datatable filter
	 */
	@Override
	public void clearFilter() {

		// super.clearFilter();

		SelectOneMenu selectOne = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:uniTable:cmbError");
		if (selectOne != null) {
			selectOne.resetValue();
		}

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').clearFilters()");
	}

	public String update() {
		try {

			if (edit) {
				service.update(entity, skills.getTarget(), ca);
			} else {
				save();
			}
		} catch (SystemException e) {
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}

		return "skills";
	}

	public void save() throws Exception {
		entity = service.makePersistent(entity, skills.getTarget(), ca);
	}

	/**
	 * Change the state and save the date and the user that make the action
	 */
	public void changeState() {
		try {

			service.changeState(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * Load all the clients applications data
	 */
	public void loadData() {
		try {

			// save the CA list, used in loadDataNext and loadDataBefore
			listDetail = lazyModel.getDatasource();

			entity = service.loadAllById(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * search the next CA and show the details
	 */
	public void loadDataNext() {
		try {

			// in the CA list get the actual position
			listDetail = lazyModel.getDatasource();
			for (int i = 0; i < listDetail.size(); i++) {
				if (listDetail.get(i).getId().equals(entity.getId())) {
					position = i;
					break;
				}
			}
			// get the next CA
			entity = listDetail.get(++position >= listDetail.size() ? 0
					: position);
			// load all the data
			entity = service.loadAllById(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * search the before CA and show the details
	 */
	public void loadDataBefore() {
		try {
			// in the CA list get the actual position
			listDetail = lazyModel.getDatasource();
			for (int i = 0; i < listDetail.size(); i++) {
				if (listDetail.get(i).getId().equals(entity.getId())) {
					position = i;
					break;
				}
			}
			// get the before CA
			entity = listDetail.get(--position < 0 ? listDetail.size() - 1
					: position);
			// load all the data
			entity = service.loadAllById(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	@Override
	public SkillService getService() {
		return service;
	}

	public void setService(SkillService service) {
		this.service = service;
	}

	@Override
	public Skill getEntity() {
		if (entity == null) {
			entity = new Skill();
		}
		return entity;
	}

	public SkillPhoneSystemService getPhoneSystemService() {
		return phoneSystemService;
	}

	public void setPhoneSystemService(SkillPhoneSystemService phoneSystemService) {
		this.phoneSystemService = phoneSystemService;
	}

	public ClientApplication getCa() {
		return ca;
	}

	public void setCa(ClientApplication ca) {
		this.ca = ca;
	}

	public DualListModel<SkillPhoneSystem> getSkills() {
		// by default charge like insert, I used this when add CA in program
		if ((skills.getSource() == null || skills.getSource().size() == 0)
				&& (skills.getTarget() == null || skills.getTarget().size() == 0)) {

			skills = new DualListModel<SkillPhoneSystem>(
					phoneSystemService.getDataWithoutSkills(),
					new ArrayList<SkillPhoneSystem>());

		}
		return skills;
	}

	public void setSkills(DualListModel<SkillPhoneSystem> skills) {
		this.skills = skills;
	}

	public SkillDataModel getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(SkillDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public List<Skill> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<Skill> listDetail) {
		this.listDetail = listDetail;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

}
