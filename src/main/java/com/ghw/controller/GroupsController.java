package com.ghw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.ghw.datamodel.GroupsDataModel;
import com.ghw.model.Groups;
import com.ghw.model.Profile;
import com.ghw.model.User;
import com.ghw.service.GroupsService;
import com.ghw.service.ProfileService;
import com.ghw.service.UserService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class GroupsController extends Controller<Groups, String, GroupsService> {

	@ManagedProperty(value = "#{groupsDataModel}")
	private GroupsDataModel lazyModel;

	@ManagedProperty(value = "#{groupsService}")
	private GroupsService service;

	@ManagedProperty(value = "#{profileService}")
	private ProfileService profileService;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	private List<Groups> listDetail = new ArrayList<Groups>();
	private Integer position = 0;

	private DualListModel<Profile> ibos = new DualListModel<Profile>(
			new ArrayList<Profile>(), new ArrayList<Profile>());

	private User user;

	private List<User> listUser = new ArrayList<User>();

	@PostConstruct
	public void init() {
		try {

			// if is the list page call the method the create the default filter
			if (objectUtil.getEdit() == null) {
				initFilter();
			}

			// if I'm editing or adding a Groups
			if (objectUtil.getEdit() != null) {
				setEdit(objectUtil.getEdit() == 2);

				List<Profile> listSource = new ArrayList<Profile>();
				List<Profile> listTarget = new ArrayList<Profile>();

				// if editing
				if (objectUtil.getEdit() == 2) {

					setEntity((Groups) objectUtil.getObject());

					// get the PA list without groups associate and the actual
					// user
					// fill selectOneMenu id="cmbUser"
					listUser = userService.getDataWithoutGroup(entity);

					// assigned the actual user
					user = entity.getUser();

					// get the IBO list without groups associate
					List<Profile> list = profileService
							.getDataWithoutGroup(entity);
					for (Profile s : list) {
						if (s.getGroup() != null) {
							listTarget.add(s);
						} else {
							listSource.add(s);
						}
					}

				} else {
					// get the PA list without groups associate
					listSource = profileService.getDataWithoutGroup(null);
					// get the IBO list without groups associate
					listUser = userService.getDataWithoutGroup(null);
				}

				ibos = new DualListModel<Profile>(listSource, listTarget);

			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

		} finally {
			objectUtil.setObject(null);
			objectUtil.setEdit(null);
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
	 * Load all the groups data
	 */
	public void loadData() {
		try {

			// save the groups list, used in loadDataNext and loadDataBefore
			listDetail = lazyModel.getDatasource();

			entity = service.loadAllById(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * search the next group and show the details
	 */
	public void loadDataNext() {
		try {

			// in the groups list get the actual position
			listDetail = lazyModel.getDatasource();
			for (int i = 0; i < listDetail.size(); i++) {
				if (listDetail.get(i).getId().equals(entity.getId())) {
					position = i;
					break;
				}
			}
			// get the next group
			entity = listDetail.get(++position >= listDetail.size() ? 0
					: position);
			// load all the data
			entity = service.loadAllById(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * search the before group and show the details
	 */
	public void loadDataBefore() {
		try {
			// in the groups list get the actual position
			listDetail = lazyModel.getDatasource();
			for (int i = 0; i < listDetail.size(); i++) {
				if (listDetail.get(i).getId().equals(entity.getId())) {
					position = i;
					break;
				}
			}
			// get the before group
			entity = listDetail.get(--position < 0 ? listDetail.size() - 1
					: position);
			// load all the data
			entity = service.loadAllById(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * save or update the group
	 * 
	 * @return
	 */
	public String update() {
		try {

			List<Profile> list = ibos.getTarget();
			if (edit) {
				service.update(entity, list, user);
			} else {
				service.makePersistent(entity, list, user);
			}

		} catch (SystemException e) {
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "groups";
	}

	public GroupsDataModel getLazyModel() {
		return lazyModel;
	}

	@Override
	public GroupsService getService() {
		return service;
	}

	public void setService(GroupsService service) {
		this.service = service;
	}

	public void setLazyModel(GroupsDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	@Override
	public Groups getEntity() {
		if (entity == null) {
			entity = new Groups();
		}
		return entity;
	}

	public List<Groups> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<Groups> listDetail) {
		this.listDetail = listDetail;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public DualListModel<Profile> getIbos() {
		return ibos;
	}

	public void setIbos(DualListModel<Profile> ibos) {
		this.ibos = ibos;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isDeactiveState() {
		return deactiveState;
	}

	public void setDeactiveState(boolean deactiveState) {
		this.deactiveState = deactiveState;
	}

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public List<User> getListUser() {
		return listUser;
	}

	public void setListUser(List<User> listUser) {
		this.listUser = listUser;
	}

}
