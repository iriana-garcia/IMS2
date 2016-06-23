package com.ghw.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.ghw.datamodel.ClientApplicationDataModel;
import com.ghw.model.ClientApplication;
import com.ghw.model.Event;
import com.ghw.model.InvoiceWork;
import com.ghw.model.Profile;
import com.ghw.model.Skill;
import com.ghw.model.SkillPhoneSystem;
import com.ghw.service.ClientApplicationService;
import com.ghw.service.EventService;
import com.ghw.service.InvoiceWorkService;
import com.ghw.service.ProfileService;
import com.ghw.service.SkillService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class ClientApplicationController extends
		Controller<ClientApplication, String, ClientApplicationService> {

	@ManagedProperty(value = "#{clientApplicationDataModel}")
	private ClientApplicationDataModel lazyModel;

	@ManagedProperty(value = "#{clientApplicationService}")
	private ClientApplicationService service;

	@ManagedProperty(value = "#{skillService}")
	private SkillService skillService;

	@ManagedProperty(value = "#{eventService}")
	private EventService eventService;

	@ManagedProperty(value = "#{invoiceWorkService}")
	private InvoiceWorkService invoiceWorkService;

	// fill the skill pickList
	private DualListModel<Skill> skills = new DualListModel<Skill>(
			new ArrayList<Skill>(), new ArrayList<Skill>());

	// fill the event pickList
	private DualListModel<Event> events = new DualListModel<Event>(
			new ArrayList<Event>(), new ArrayList<Event>());

	private Date dateMod;

	// used to view detail
	private List<ClientApplication> listDetail = new ArrayList<ClientApplication>();
	private Integer position = 0;

	private List<InvoiceWork> invoicesAffected = new ArrayList<InvoiceWork>();

	@ManagedProperty(value = "#{profileService}")
	private ProfileService profileService;

	private DualListModel<Profile> ibos = new DualListModel<Profile>(
			new ArrayList<Profile>(), new ArrayList<Profile>());

	// used to action back. Can be to "client_application" or
	// "invoices_problem", It depends on
	// where it was called
	private String actionBack = "client_application";

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

				List<Skill> skillSource = new ArrayList<Skill>();
				List<Skill> skillTarget = new ArrayList<Skill>();

				List<Profile> listSource = new ArrayList<Profile>();
				List<Profile> listTarget = new ArrayList<Profile>();

				List<Event> eventSource = new ArrayList<Event>();
				List<Event> eventTarget = new ArrayList<Event>();

				if (StringUtils.isNotBlank(objectUtil.getAction())) {
					actionBack = objectUtil.getAction();
				}
				String eventIdByDefault = objectUtil.getIdEvent();

				// if editing
				if (objectUtil.getEdit() == 2) {

					setEntity((ClientApplication) objectUtil.getObject());

					// get the skills associate
					List<Skill> list = skillService.getDataWithoutCA(entity);
					for (Skill s : list) {
						if (s.getClientApplication() != null) {
							skillTarget.add(s);
						} else {
							skillSource.add(s);
						}
					}

					// get the event associate
					List<Event> listEvent = eventService
							.getDataWithoutCA(entity);
					for (Event s : listEvent) {
						if (s.getClientApplication() != null
								|| (StringUtils.isNotBlank(eventIdByDefault) && eventIdByDefault
										.equals(s.getId()))) {
							eventTarget.add(s);
						} else {
							eventSource.add(s);
						}
					}

					// get the Profile associate
					List<Profile> listIbo = profileService
							.getDataAllIboByCA(entity);
					for (Profile s : listIbo) {
						// s.setStyle(s.getNameFull().contains("a") ?
						// "textError" : "");
						if (s.isHasCa()) {
							listTarget.add(s);
						} else {
							listSource.add(s);
						}
					}

					// validate if the CA can be deactivate, if not the
					// selectOneRadio is disabled
					setDeactiveState(invoiceWorkService
							.validateAssociateCA(entity));

					if (deactiveState) {
						FacesMessage msg = new FacesMessage(
								Context.getUIMsg("ca_invoice_associate")
										+ " For more information go to View Invoices Affected button");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}

				} else {
					// if adding get all the skill without CA associate
					skillSource = skillService.getDataWithoutCA();

					// if adding get all the event without CA associate
					List<Event> events = eventService.getDataWithoutCA();

					if (StringUtils.isBlank(eventIdByDefault)) {
						eventSource = events;
					} else {
						for (Event event : events) {
							if (eventIdByDefault.equals(event.getId())) {
								eventTarget.add(event);
							} else {
								eventSource.add(event);
							}
						}
					}

					// if objectUtil has event by default added

					// get the IBO list Active
					listSource = profileService.getDataActiveIbo();

				}

				skills = new DualListModel<Skill>(skillSource, skillTarget);

				events = new DualListModel<Event>(eventSource, eventTarget);

				ibos = new DualListModel<Profile>(listSource, listTarget);

			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

		} finally {
			objectUtil.clean();
		}
	}

	/**
	 * To go to program page or invoices problem page, depends how page called
	 * it
	 * 
	 * @return
	 */
	public String back() {
		return actionBack;
	}

	/**
	 * Get the next day to disabled in date modification calendar
	 */
	public Date getMaxDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.DATE, 1);

		return calendar.getTime();
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

	/**
	 * save or update the CA
	 * 
	 * @return
	 */
	public String update() {
		try {

			if (edit) {
				service.update(entity, skills.getTarget(), events.getTarget(),
						ibos.getTarget(), dateMod);
			} else {
				service.makePersistent(entity, skills.getTarget(),
						events.getTarget(), ibos.getTarget());
			}

		} catch (SystemException e) {
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return actionBack;
	}

	public void saveCA() {

		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			// insert the CA
			SkillController sc = (SkillController) Context
					.getBean("skillController");
			sc.save();

			// add the CA to the program
			skills.getTarget().add(sc.getEntity());

			// clean the form
			sc.setEntity(null);
			sc.setSkills(new DualListModel<SkillPhoneSystem>(
					new ArrayList<SkillPhoneSystem>(),
					new ArrayList<SkillPhoneSystem>()));

		} catch (SystemException e) {
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			error = true;
		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}
	}

	/**
	 * search all the invoice affected for the CA modification
	 */
	public void loadInvoicesAffected() {
		try {

			invoicesAffected = invoiceWorkService
					.getDataAffectedByCAModification(entity, dateMod);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	public ClientApplicationDataModel getLazyModel() {
		return lazyModel;
	}

	@Override
	public ClientApplicationService getService() {
		return service;
	}

	public void setService(ClientApplicationService service) {
		this.service = service;
	}

	public void setLazyModel(ClientApplicationDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	@Override
	public ClientApplication getEntity() {
		if (entity == null) {
			entity = new ClientApplication();
		}
		return entity;
	}

	public DualListModel<Skill> getSkills() {
		return skills;
	}

	public void setSkills(DualListModel<Skill> skills) {
		this.skills = skills;
	}

	public SkillService getSkillService() {
		return skillService;
	}

	public void setSkillService(SkillService skillService) {
		this.skillService = skillService;
	}

	public Date getDateMod() {
		return dateMod;
	}

	public void setDateMod(Date dateMod) {
		this.dateMod = dateMod;
	}

	public InvoiceWorkService getInvoiceWorkService() {
		return invoiceWorkService;
	}

	public void setInvoiceWorkService(InvoiceWorkService invoiceWorkService) {
		this.invoiceWorkService = invoiceWorkService;
	}

	public List<ClientApplication> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<ClientApplication> listDetail) {
		this.listDetail = listDetail;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public List<InvoiceWork> getInvoicesAffected() {
		return invoicesAffected;
	}

	public void setInvoicesAffected(List<InvoiceWork> invoicesAffected) {
		this.invoicesAffected = invoicesAffected;
	}

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	public DualListModel<Profile> getIbos() {
		return ibos;
	}

	public void setIbos(DualListModel<Profile> ibos) {
		this.ibos = ibos;
	}

	public DualListModel<Event> getEvents() {
		return events;
	}

	public void setEvents(DualListModel<Event> events) {
		this.events = events;
	}

	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public String getActionBack() {
		return actionBack;
	}

	public void setActionBack(String actionBack) {
		this.actionBack = actionBack;
	}

}
