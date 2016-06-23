package com.ghw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;

import com.ghw.datamodel.RolDataModel;
import com.ghw.model.Permission;
import com.ghw.model.Rol;
import com.ghw.model.RolPermission;
import com.ghw.service.PermissionService;
import com.ghw.service.RolPermissionService;
import com.ghw.service.RolService;
import com.ghw.util.Context;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class RolController extends Controller<Rol, String, RolService> {

	@ManagedProperty(value = "#{rolDataModel}")
	private RolDataModel lazyModel;

	private List<RolPermission> selectedPerm = new ArrayList<RolPermission>();

	@ManagedProperty(value = "#{rolService}")
	private RolService service;

	@ManagedProperty(value = "#{rolPermissionService}")
	private RolPermissionService rolPermissionService;

	@ManagedProperty(value = "#{permissionService}")
	private PermissionService permissionService;

	@PostConstruct
	public void init() {
		try {

			// if is the list page call the method the create the default filter
			if (objectUtil.getEdit() == null) {
				initFilter();
			}

			// if I'm editing or adding a roll
			if (objectUtil != null && objectUtil.getEdit() != null) {
				setEdit(objectUtil.getEdit() == 2);
				// add rol pull the permission and validate If has user
				// associate
				// if has user associate field state is disabled
				if (objectUtil.getEdit().intValue() == 2) {

					setEntity((Rol) objectUtil.getObject());

					List<RolPermission> rp = rolPermissionService
							.getListByRol(entity.getId());
					for (RolPermission rolPermission : rp) {
						if (StringUtils.isNotBlank(rolPermission.getAccess())) {
							rolPermission.analizeAccess();
							selectedPerm.add(rolPermission);
						}
					}

					getEntity().setPermissions(rp);

					entity.setUserAssociate(service
							.validateUserAssociate(entity));

				} else if (objectUtil.getEdit().intValue() == 3) {
					// if the action is clone
					setEdit(false);

					Rol clone = (Rol) objectUtil.getObject();
					getEntity().setName(clone.getName() + " Copy");

					List<RolPermission> rp = rolPermissionService
							.getListByRol(clone.getId());
					for (RolPermission rolPermission : rp) {
						if (StringUtils.isNotBlank(rolPermission.getAccess())) {
							rolPermission.analizeAccess();
							selectedPerm.add(rolPermission);
						}
					}

					getEntity().setPermissions(rp);

				} else {
					List<Permission> list = permissionService.getData();
					for (Permission p : list) {
						getEntity().getPermissions().add(new RolPermission(p));
					}
				}

				objectUtil.setObject(null);
				objectUtil.setEdit(null);

			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
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

	public String clone() {
		try {

			objectUtil.setEdit(3);
			objectUtil.setObject(entity);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}
		return "add";
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
	 * save or update the role
	 * 
	 * @return
	 */
	public transient DataTable dataTablePerm;

	public DataTable getDataTablePerm() {
		return dataTablePerm;
	}

	public void setDataTablePerm(DataTable dataTablePerm) {
		this.dataTablePerm = dataTablePerm;
	}

	public String update() {
		try {

			List<RolPermission> list = (List<RolPermission>) dataTablePerm
					.getValue();
			selectedPerm = new ArrayList<RolPermission>();

			for (RolPermission r : list) {
				if (r.isAccessAdd()) {
					selectedPerm.add(r);
				}
			}

			if (edit) {
				service.update(entity, selectedPerm);
			} else {
				service.makePersistent(entity, selectedPerm);
			}

		} catch (SystemException e) {
			Context.addErrorMessageFromMSG(e);
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
			return null;
		}

		return "rol";
	}

	public RolDataModel getLazyModel() {
		return lazyModel;
	}

	@Override
	public RolService getService() {
		return service;
	}

	public void setService(RolService service) {
		this.service = service;
	}

	public void setLazyModel(RolDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	@Override
	public Rol getEntity() {
		if (entity == null) {
			entity = new Rol();
		}
		return entity;
	}

	public List<RolPermission> getSelectedPerm() {
		return selectedPerm;
	}

	public void setSelectedPerm(List<RolPermission> selectedPerm) {
		this.selectedPerm = selectedPerm;
	}

	public RolPermissionService getRolPermissionService() {
		return rolPermissionService;
	}

	public void setRolPermissionService(
			RolPermissionService rolPermissionService) {
		this.rolPermissionService = rolPermissionService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
}
