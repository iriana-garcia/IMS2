package com.ghw.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.WeakHashMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.inputmask.InputMask;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.ghw.constant.Constant;
import com.ghw.datamodel.UserDataModel;
import com.ghw.filter.UserFilter;
import com.ghw.model.Address;
import com.ghw.model.ClientApplication;
import com.ghw.model.Corporation;
import com.ghw.model.Country;
import com.ghw.model.Groups;
import com.ghw.model.IboType;
import com.ghw.model.OwnerType;
import com.ghw.model.PayMethod;
import com.ghw.model.Profile;
import com.ghw.model.Region;
import com.ghw.model.Rol;
import com.ghw.model.States;
import com.ghw.model.TypeContract;
import com.ghw.model.User;
import com.ghw.model.UserType;
import com.ghw.model.UserUtil;
import com.ghw.report.service.OracleService;
import com.ghw.service.AddressService;
import com.ghw.service.ClientApplicationService;
import com.ghw.service.CorporationService;
import com.ghw.service.CountryService;
import com.ghw.service.GroupsService;
import com.ghw.service.InvoiceService;
import com.ghw.service.ProfileService;
import com.ghw.service.RolService;
import com.ghw.service.StatesService;
import com.ghw.service.UserService;
import com.ghw.util.CallUpsUtil;
import com.ghw.util.Context;
import com.ghw.util.EntityConverter;
import com.ghw.util.IboUtil;
import com.ghw.util.SystemException;

@ManagedBean
@ViewScoped
public class UserController extends Controller<User, String, UserService>
		implements Serializable {

	@ManagedProperty(value = "#{userDataModel}")
	private UserDataModel lazyModel;

	@ManagedProperty(value = "#{userService}")
	private UserService service;

	@ManagedProperty(value = "#{countryService}")
	private CountryService countryService;

	@ManagedProperty(value = "#{oracleService}")
	private OracleService oracleService;

	@ManagedProperty(value = "#{userFilter}")
	private UserFilter userFilter;

	@ManagedProperty(value = "#{applicationBean}")
	private ApplicationBean applicationBean;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	@ManagedProperty(value = "#{invoiceService}")
	private InvoiceService invoiceService;

	private List<User> listDetail = new ArrayList<User>();
	private Integer position = 0;
	// is the list user from Active Directory and do not exist in my system
	// is used to fill cmbUser in "Add User"
	private List<UserUtil> listUserAd = new ArrayList<UserUtil>();
	// user selected from listUserAd
	private UserUtil userAd;

	private Rol rol;
	@ManagedProperty(value = "#{rolService}")
	private RolService rolService;

	// all the active group that do not have PA associate, if the user is edit
	// show too
	// that user, control = cmbGroup
	private List<Groups> listGroup = new ArrayList<Groups>();
	// All the active group, control = cmbGroupIbo
	// private List<Groups> listGroupIbo;
	// PA's group, is the group that PA is boss
	private Groups group;
	// IBO's group, the group that owns the IBO
	private Groups groupIbo;
	@ManagedProperty(value = "#{groupsService}")
	private GroupsService groupsService;

	// fill the p:pickList the Client Applications control = txtCA
	private DualListModel<ClientApplication> clientApplications = new DualListModel<ClientApplication>(
			new ArrayList<ClientApplication>(),
			new ArrayList<ClientApplication>());

	@ManagedProperty(value = "#{clientApplicationService}")
	private ClientApplicationService clientApplicationService;

	@ManagedProperty(value = "#{profileService}")
	private ProfileService profileService;

	private String messageSupplier;

	private IboType iboType;
	private UserType type;
	private Profile profile;
	private Address address;

	// is the country from corporation address
	private Country country = new Country(Constant.usCountryId);
	// is the IBO's country
	private Country countryIbo = new Country(Constant.usCountryId);
	// is true when the IBO's region is INTERNATIONAL
	private boolean isInternational = false;
	// the corporation address's state
	private States states;
	// fill the selectOneMenu "cmdState" with all the states from US
	private List<States> listStates = new ArrayList<States>();
	@ManagedProperty(value = "#{statesService}")
	private StatesService statesService;
	// save if the country from corporation address have states (NOW is not
	// used)
	private boolean haveStates = false;

	// used to action back. Can be to "user" or "onboarding", It depends on
	// where it was called
	private String actionBack = "user";
	// used to see if is an temporal IBO or is add user normal, if is true don't
	// show cmbUser (have all AD user)
	private boolean addTemporal = false;

	// @ManagedProperty(value = "#{zipCodeService}")
	// private ZipCodeService zipCodeService;
	//
	// private List<ZipCode> zipCodes;

	// Corporation information
	// save if the corporation is required or not, is NOT required only when is
	// IDC
	// save the IBO's corporation
	private Corporation corporation;
	// The action can be ADD a new, ASSOCIATE to another corporation existing or
	// MODIFY the corporation associate to the IBO (only in User Edit)
	private Integer action = ActionsCorporation.ADD_NEW;
	// Have all the corporation existing fill the selectOneMenu "cmbCorporation"
	// from dialog_corporation
	private List<Corporation> listCorporation;
	@ManagedProperty(value = "#{corporationService}")
	private CorporationService corporationService;
	@ManagedProperty(value = "#{addressService}")
	private AddressService addressService;
	// save the corporation existing, when the action selected is
	// "Associate Existing" and select corporation
	private Corporation corporationExisting;
	// corporation original is when the user is edit to save the original
	private Corporation corporationOriginal;
	// disabled or not the radioButton Owner type
	// is disabled when the action selected is "Associate Existing" and the
	// corporation has principal owner
	private boolean disabledOwnerType = false;
	// all the ibo that belong to the user edited corporation
	// this is necessary when you edit an IBO owner_type = PRINCIPAL to type
	// SECONDARY
	// is used in dialog_list_ibo_corporation to select the new principal IBO
	private List<Profile> listIbosCorporation = null;
	// is the new Principal IBO selected from listIbosCorporation
	private Profile newPrincipalIBO;

	// Utilitary class to connect to UPS API an get the zipcode information
	private CallUpsUtil callUpsUtil = new CallUpsUtil();

	private List<ClientApplication> listSource = new ArrayList<ClientApplication>();
	private List<ClientApplication> listTarget = new ArrayList<ClientApplication>();

	@PostConstruct
	public void init() {
		try {

			// if is the list page call the method the create the default filter
			if (objectUtil.getEdit() == null) {
				initFilter();
			}

			// by default the user type is 1 = "User"
			type = new UserType(UserType.USER);
			// if I'm editing or adding a User
			if (objectUtil.getEdit() != null) {
				setEdit(objectUtil.getEdit() == 2);

				// if editing
				if (objectUtil.getEdit() == 2) {

					setEntity(service.loadAllByIdToEdit((User) objectUtil
							.getObject()));

					deactiveState = !service.validateIfDeactivate(entity);

					// load all user data
					rol = entity.getRol();
					group = entity.getGroup();
					type = entity.getType();

					if (entity.isAnIbo() && entity.getIbo() != null) {
						chargeProfile(entity.getIbo());
					}
					if (entity.isAnPA() || entity.isAnIbo()) {
						group = entity.getGroup();
						listGroup = groupsService.getDataWithUser(entity);
					}

					if (StringUtils.isNotBlank(objectUtil.getAction()))
						actionBack = objectUtil.getAction();

				} // if adding from onboarding
				else if (objectUtil.getEdit() == 4) {
					UserUtil userUtil = (UserUtil) objectUtil.getObject();

					entity = new User();

					entity.setName(userUtil.getUserName());
					entity.setFirstName(userUtil.getFirstName());
					entity.setMiddleName(userUtil.getMiddleName());
					entity.setLastName(userUtil.getLastName());
					entity.setEmail(userUtil.getEmail());
					setType(new UserType(UserType.IBO));

					listSource = clientApplicationService.getDataActive();
					listGroup = groupsService.getDataWithUser(null);

					clientApplications = new DualListModel<ClientApplication>(
							listSource, listTarget);

					addTemporal = true;

					getProfile().setTypeContract(userUtil.getTypeContract());
					getProfile().setRegion(
							profile.getTypeContract().equals(
									TypeContract.DOMESTIC) ? Region.D.name()
									: Region.I.name());
					action = profile.getTypeContract().equals(
							TypeContract.INT_INDEP_CONTRACT) ? ActionsCorporation.NONE
							: ActionsCorporation.ADD_NEW;

					// set the country for IBO
					countryIbo = getProfile().getTypeContract().equals(
							TypeContract.DOMESTIC) ? new Country(
							Constant.usCountryId) : null;

					actionBack = "onboarding";

				} else {// if adding

					// search all active user in AD and do not exist in my
					// system
					listUserAd = service.getDataActiveAd(applicationBean
							.getConfiguration());

					listSource = clientApplicationService.getDataActive();
					listGroup = groupsService.getDataWithUser(null);

					clientApplications = new DualListModel<ClientApplication>(
							listSource, listTarget);

				}

			}
		} catch (NamingException e) {
			Context.addErrorMessageFromMSG("error_data_connection_ad");

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());

		} finally {
			objectUtil.clean();
		}
	}

	/**
	 * Change the region
	 */
	public void changeRegion() {
		try {

			if (profile != null && profile.isDomestic()) {
				countryIbo = new Country(Constant.usCountryId);
				getProfile().setTypeContract(TypeContract.DOMESTIC);
			} else {
				countryIbo = null;
			}
			listCorporation = null;

			// if change the region and have corporation and has another IBO
			// need to select the action
			// from now only add a warning message
			if (corporation != null && getListIbosCorporation() != null
					&& getListIbosCorporation().size() > 0) {
				Context.addErrorMessageFromMSG("corporation_different_region",
						null, FacesMessage.SEVERITY_WARN);
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Change the owner type
	 */
	public void changeOwnerType() {
		try {

			// newPrincipalIBO = null;
			//
			// RequestContext.getCurrentInstance().execute(
			// "PF('tblIboCorporation').unselectAllRows()");

			// if change the owner type to Secondary and the corporation has
			// others IBO show the list in p:dialog to select the new Principal
			// Owner
			if (profile.getOwnerType().equals(OwnerType.SECONDARY)
					&& getListCorporation() != null
					&& getListCorporation().size() > 0) {

				RequestContext.getCurrentInstance().execute(
						"PF('dlgIboCorporation').show()");
			} else {
				newPrincipalIBO = null;
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	public void clearDateModification() {
		profile.setDateModification(null);
	}

	/**
	 * Change the actions in the corporation section
	 */
	public void chargeCorporation() {
		try {

			if (action.equals(ActionsCorporation.ADD_NEW)
					|| action.equals(ActionsCorporation.NONE)) {
				corporation = new Corporation();
				corporationExisting = null;
				address = new Address();
				states = null;
				disabledOwnerType = false;
			} else if (action.equals(ActionsCorporation.EDIT)) {
				// if edit show the original corporation
				corporationExisting = null;
				corporation = corporationOriginal;
				fieldCorporation(corporation);

			} else {

				if (getListCorporation() == null
						|| getListCorporation().size() == 0) {

					action = ActionsCorporation.ADD_NEW;

					Context.addInfoMessageFromMSG("corporation_not_exist");

					RequestContext.getCurrentInstance().execute(
							"refreshCorporation()");

				} else
					RequestContext.getCurrentInstance().execute(
							"PF('dlgCorporation').show()");

			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	public void saveCorporation() {
		try {

			corporation = corporationExisting;

			// search corporation address
			corporation.setAddressCorporation(addressService
					.getDataByCorporation(corporation));

			fieldCorporation(corporation);

		} catch (Exception e) {
			Context.addInfoMessageFromMSG(e.getMessage());
		}
	}

	public void saveIboCorporation() {
		try {

			// update the corporation with the new IBO principal to disabled the
			// control "owner type"

		} catch (Exception e) {
			Context.addInfoMessageFromMSG(e.getMessage());
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

	public void cancelCorporation() {
		try {
			// if cancel define Add new or Modify if the action is Edit
			if (edit) {
				// if the corportation is not null
				action = ActionsCorporation.EDIT;
				if (corporationOriginal != null) {
					corporation = corporationOriginal;
					fieldCorporation(corporation);
				} else {
					action = getProfile().getTypeContract().equals(
							TypeContract.INT_INDEP_CONTRACT) ? ActionsCorporation.NONE
							: ActionsCorporation.ADD_NEW;
				}

			} else if (StringUtils.isBlank(corporation.getId())) {
				action = ActionsCorporation.ADD_NEW;
				corporationExisting = null;
				disabledOwnerType = false;
			}

		} catch (Exception e) {
			Context.addInfoMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Fill selectOneRadio id="rdCorporation" with the option to do in the
	 * corporation
	 * 
	 * @return
	 */
	public List<Object[]> getOption() {

		List<Object[]> listOption = new ArrayList<Object[]>();
		if (edit && profile != null && profile.getCorporation() != null) {
			listOption.add(new Object[] { "Modify", 3 });
		}

		listOption.add(new Object[] { "Add New", 1 });

		List<Corporation> list = getListCorporation();
		// if editing i need to exclude my corporation if exists only one
		// corporation
		if (list != null
				&& list.size() > 0
				&& !(edit && list.size() == 1 && profile != null
						&& profile.getCorporation() != null && list.get(0)
						.equals(profile.getCorporation()))) {
			listOption.add(new Object[] { "Associate Existing", 2 });
		}

		// if is an IDC add NONE
		if (getProfile() != null
				&& getProfile().getTypeContract().equals(
						TypeContract.INT_INDEP_CONTRACT)) {
			listOption.add(new Object[] { "None", 4 });
		}

		boolean found = false;
		// if the action is not found in the list of option define default
		for (Object[] o : listOption) {
			if (o[1].equals(action)) {
				found = true;
				break;
			}
		}
		// set default action
		if (!found) {
			if (edit) {
				action = getProfile().getTypeContract().equals(
						TypeContract.INT_INDEP_CONTRACT) ? ActionsCorporation.NONE
						: corporationOriginal == null ? ActionsCorporation.ADD_NEW
								: ActionsCorporation.EDIT;
			} else
				action = getProfile().getTypeContract().equals(
						TypeContract.INT_INDEP_CONTRACT) ? ActionsCorporation.NONE
						: ActionsCorporation.ADD_NEW;
		}
		return listOption;
	}

	/**
	 * Fill selectOneRadio id="rdOwnerType"
	 * 
	 * @return
	 */
	public List<Object[]> getOwnerTypes() {

		List<Object[]> listOption = new ArrayList<Object[]>();
		listOption.add(new Object[] { "Principal", OwnerType.PRINCIPAL });
		listOption.add(new Object[] { "Secondary", OwnerType.SECONDARY });

		return listOption;
	}

	/**
	 * Fill selectOneMenu id="cmbContractType"
	 * 
	 * @return
	 */
	public List<Object[]> getContractTypes() {

		List<Object[]> listOption = new ArrayList<Object[]>();
		// listOption.add(new Object[] { TypeContract.DOMESTIC, "Domestic" });
		listOption.add(new Object[] { TypeContract.INTERNATIONAL,
				"International" });
		listOption.add(new Object[] { TypeContract.INT_INDEP_CONTRACT,
				"International independent contract" });

		return listOption;
	}

	/**
	 * Process the user data to send to Finance (ALL except invoices and ACH
	 * file)
	 */
	public void sentFinance() {

		try {

			messageSupplier = oracleService.createSupplierFiles(sessionBean
					.getUser());

			RequestContext.getCurrentInstance().execute(
					"PF('dlgInvoicesProcessed').show()");

			// FacesMessage msg = new FacesMessage("Information processed", "");
			// FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Search the ZIP code in UPS API
	 */
	public void searchStateByZipCode() {
		try {

			String zipCode = address.getZipCode();

			if (StringUtils.isNotBlank(zipCode)) {

				// reset the component when error server side do not show the
				// new value
				SelectOneMenu selectOne = (SelectOneMenu) FacesContext
						.getCurrentInstance().getViewRoot()
						.findComponent("form:cmdStateUS");
				if (selectOne != null) {
					selectOne.resetValue();
				}

				InputText inputText = (InputText) FacesContext
						.getCurrentInstance().getViewRoot()
						.findComponent("form:txtCity");
				if (inputText != null) {
					inputText.resetValue();
				}

				// if has the "-" only get the first part
				String zipCodeFinal = zipCode.contains("-") ? zipCode
						.split("-")[0] : zipCode;
				if (zipCodeFinal.length() == 5) {
					String[] search = callUpsUtil
							.searchStateCityByZip(zipCodeFinal);
					if (search != null && search[0] != null) {
						String city = search[0];
						// proper
						city = StringUtils.lowerCase(city);
						String finalCity = "";
						String[] separate = city.split(" ");
						for (String a : separate) {
							finalCity += StringUtils.capitalize(a) + " ";
						}
						address.setCity(finalCity);
					}
					if (search != null && search[1] != null) {
						String statesAbre = search[1];

						for (States s : listStates) {
							if (s.getAbbreviation().equals(statesAbre)) {
								states = s;
								break;
							}
						}
					} else {
						FacesMessage msg = new FacesMessage(
								"Zip code not found");
						FacesContext.getCurrentInstance().addMessage("txtZip",
								msg);
					}
				} else {
					FacesMessage msg = new FacesMessage("Zip code wrong format");
					FacesContext.getCurrentInstance().addMessage("txtZip", msg);
				}
				// for (ZipCode zip : getZipCodes()) {
				// if (zip.getNumber().equals(zipCodeFinal)) {
				//
				// address.setCity(zip.getCity());
				// states = zip.getStates();
				//
				// break;
				// }
				// }
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	/**
	 * Is used to fill the corporation data in the attributes
	 */
	private void fieldCorporation(Corporation corporation) {

		if (corporation != null) {

			address = corporation.getAddressCorporation();
			// setAddress(corporation.getAddress().get(0));

			country = address.getCountry();
			onCountryChange();
			states = address.getStates();

			// If the corporation has a principal and the principal is NOT the
			// actual user change the type to secondary
			// and disabled the field
			disabledOwnerType = false;
			if (corporation.isHasPrincipalOwner()
					&& !corporation.getIdPrincipalOwner().equals(
							profile.getId())) {
				disabledOwnerType = true;
				profile.setOwnerType(OwnerType.SECONDARY);
			} else
				profile.setOwnerType(OwnerType.PRINCIPAL);

		}
	}

	/**
	 * Fill all the field like IBO when update
	 * 
	 * @param entityProfile
	 */
	private void chargeProfile(Profile entityProfile) {

		List<ClientApplication> listSource = new ArrayList<ClientApplication>();
		List<ClientApplication> listTarget = new ArrayList<ClientApplication>();

		if (entityProfile != null) {
			profile = entityProfile;
			groupIbo = profile.getGroup();
			iboType = profile.getType();
			corporation = profile.getCorporation();
			action = corporation != null ? ActionsCorporation.EDIT
					: ActionsCorporation.NONE;
			corporationOriginal = profile.getCorporation();
			countryIbo = profile.getCountry();

			fieldCorporation(corporation);

			List<ClientApplication> list = clientApplicationService
					.getDataByEditIbo(profile);
			for (ClientApplication ca : list) {
				if (ca.isIboAssociate()) {
					listTarget.add(ca);
				} else {
					listSource.add(ca);
				}
			}

		} else {
			listSource = clientApplicationService.getDataActive();

		}

		clientApplications = new DualListModel<ClientApplication>(listSource,
				listTarget);
	}

	/**
	 * clear datatable filter
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

		cleanDetailFilter();

		RequestContext.getCurrentInstance().execute(
				"PF('uniTableFil').clearFilters()");
	}

	/**
	 * The corporation is required for all the IBO but the International
	 * Independent Contract (IDC)
	 * 
	 * @return
	 */
	public boolean isRequiredCorporation() {
		boolean requiredCorporation = true;
		if (isIbo()
				&& getProfile() != null
				&& getProfile().getTypeContract().equals(
						TypeContract.INT_INDEP_CONTRACT)) {
			requiredCorporation = false;
		}
		return requiredCorporation;
	}

	/**
	 * Is for show the Contract Type when is editing and is international
	 * 
	 * @return
	 */
	public boolean isShowContractType() {
		return edit && isIbo() && profile != null
				&& profile.getRegion().equals(Region.I.name());
	}

	/**
	 * Is for show or not cmbUser, only show it if the action is ADD and is not
	 * a temporal IBO
	 * 
	 * @return
	 */
	public boolean isShowUser() {
		return !edit && !addTemporal;
	}

	/**
	 * To go to user page or onboarding page, depends how page called it
	 * 
	 * @return
	 */
	public String back() {
		return actionBack;
	}

	/**
	 * Used to show or not the cmb State, only show is the user is an IBO and
	 * the country have states (US or Canada)
	 * 
	 * @return
	 */
	public boolean isShowState() {
		return haveStates && isIbo();
	}

	/**
	 * Used to show or not the State text, only show is the user is an IBO and
	 * the country DON'T have states (US or Canada)
	 * 
	 * @return
	 */
	public boolean isShowStateTxt() {
		return !haveStates && isIbo();
	}

	/**
	 * Return if the user is an IBO, is an IBO when user type is 2
	 * 
	 * @return
	 */
	public boolean isIbo() {
		return (type != null && StringUtils.isNotBlank(type.getId()) ? type
				.getId().equals("2") : false);
	}

	/**
	 * Return if the user is an PA, is an IBO when user type is 3
	 * 
	 * @return
	 */
	public boolean isPa() {

		return (type != null && StringUtils.isNotBlank(type.getId()) ? type
				.getId().equals("3")
				|| (type.getId().equals("2") && iboType != null && iboType
						.getId().equals(IboType.PA)) : false);
	}

	/**
	 * Method calles by cmbCountry when the user change it, charge the states
	 * list
	 */
	public void onCountryChange() {
		try {

			// if is USA or Canada charge their state
			// if not only show a inputText
			// if (country != null)
			// listStates = statesService.getDataByCountry(country);

			if (country != null) {
				listStates = statesService.getDataByUS();
			}

			// haveStates = listStates.size() > 0;
			//
			// if (haveStates)
			// getAddress().setState(null);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * Method called by cmbUserType when the user change it, charge the groups
	 * list
	 */
	public void onUserTypeChange() {
		try {

			if (type.isAnPA()) {
				listGroup = groupsService.getDataWithUser(entity);
			}

			// if (type.isAnIbo())
			// listGroupIbo = groupsService.getDataWithoutUser(entity);

			// all search if in any time was an IBO, that means has a register
			// in profile
			if (type.isAnIbo()
					&& (profile == null || StringUtils.isBlank(profile.getId()))) {
				profile = profileService.loadAllByIdToEdit(entity.getId());
				chargeProfile(profile);
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * Clear form when change user
	 */
	private void clearForm() {
		entity = new User();
		rol = null;
		// listGroup = new ArrayList<Groups>();
		// listGroupIbo = null;
		group = null;
		groupIbo = null;
		clientApplications = new DualListModel<ClientApplication>(listSource,
				listTarget);
		iboType = null;
		type = null;
		profile = null;
		address = null;
		country = new Country(Constant.usCountryId);
		countryIbo = new Country(Constant.usCountryId);
		isInternational = false;
		states = null;
		listStates = new ArrayList<States>();
		haveStates = false;
		corporation = null;
		action = ActionsCorporation.ADD_NEW;
		listCorporation = null;
		corporationExisting = null;
		corporationOriginal = null;
		disabledOwnerType = false;
		listIbosCorporation = null;
		newPrincipalIBO = null;

		// the follows controls keep the error class when i try to save it, do
		// error and then change the user. I need the reset the control
		SelectOneMenu selectOneRol = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:cmbRol");
		selectOneRol.resetValue();

		SelectOneMenu selectOneCountry = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:cmbCountryIbo");
		selectOneCountry.resetValue();

		SelectOneRadio selectOneRadioActions = (SelectOneRadio) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:rdCorporation");
		selectOneRadioActions.resetValue();

		InputText inputTextCountry = (InputText) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:txtCorp");
		inputTextCountry.resetValue();

		InputMask inputTextEin = (InputMask) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent("form:txtein");
		inputTextEin.resetValue();

		InputText inputTextAddress = (InputText) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:txtAddress");
		inputTextAddress.resetValue();

		InputText inputTextZip = (InputText) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent("form:txtZip");
		inputTextZip.resetValue();

		SelectOneMenu selectOneState = (SelectOneMenu) FacesContext
				.getCurrentInstance().getViewRoot()
				.findComponent("form:cmdStateUS");
		selectOneState.resetValue();

		InputText inputTextCity = (InputText) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent("form:txtCity");
		inputTextCity.resetValue();

	}

	/**
	 * Method called when select a user, filled the user object with the Active
	 * Directory data
	 * 
	 * @param event
	 */

	public void changeUser(ValueChangeEvent event) {
		try {

			// clean the form
			clearForm();

			userAd = (UserUtil) event.getNewValue();

			if (userAd != null) {
				entity.setName(userAd.getUserName());
				entity.setFirstName(userAd.getFirstName());
				entity.setMiddleName(userAd.getMiddleName());
				entity.setLastName(userAd.getLastName());
				entity.setEmail(userAd.getEmail());
				entity.setPhone(userAd.getTelephoneNumber());
				entity.setDistinguishedName(userAd.getDistinguishedName());

				if (StringUtils.isBlank(userAd.getFirstName())) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"First Name: Value is required. Please contact HelpDesk.",
											""));
				}
				if (StringUtils.isBlank(userAd.getLastName())) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Last Name: Value is required. Please contact HelpDesk.",
											""));
				}
				if (StringUtils.isBlank(userAd.getEmail())) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Email: Value is required. Please contact HelpDesk.",
											""));
				}

				String distName = userAd.getDistinguishedName().toLowerCase();
				// define the user type
				if (IboUtil.isDNBelongIbo(distName,
						applicationBean.getConfiguration())) {

					setType(new UserType(UserType.IBO));

					getProfile().setTypeContract(
							IboUtil.getTypeContract(distName,
									applicationBean.getConfiguration()));

					// if is an international contract IBO
					getProfile().setRegion(
							getProfile().getTypeContract().equals(
									TypeContract.DOMESTIC) ? "D" : "I");

					action = getProfile().getTypeContract().equals(
							TypeContract.INT_INDEP_CONTRACT) ? ActionsCorporation.NONE
							: ActionsCorporation.ADD_NEW;

					// set the country for IBO
					countryIbo = getProfile().getTypeContract().equals(
							TypeContract.DOMESTIC) ? new Country(
							Constant.usCountryId) : null;

				} else if (IboUtil.isDNBelongPa(distName,
						applicationBean.getConfiguration())) {
					setType(new UserType(UserType.PA));
				} else {
					setType(new UserType(UserType.USER));
				}

			} else {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info",
								"Not found in system"));

			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
	}

	@Override
	public String add() {
		try {

			if (applicationBean.getConfiguration() == null) {
				throw new SystemException("ad_configuration_necesary");
			}

			// clean the entities
			EntityConverter.entities = new WeakHashMap<Object, String>();

			objectUtil.setEdit(1);

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

			service.changeState(entity, sessionBean.getUser());

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * Load all the user data
	 */
	public void loadData() {
		try {
			// save the users list, used in loadDataNext and loadDataBefore
			listDetail = lazyModel.getDatasource();

			entity = service.loadAllById(entity);

			type = entity.getType();

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * search the next user and show the details
	 */
	public void loadDataNext() {
		try {
			// in the user list get the actual position
			listDetail = lazyModel.getDatasource();
			for (int i = 0; i < listDetail.size(); i++) {
				if (listDetail.get(i).getId().equals(entity.getId())) {
					position = i;
					break;
				}
			}
			// get the next user
			entity = listDetail.get(++position >= listDetail.size() ? 0
					: position);
			// load all the data
			entity = service.loadAllById(entity);

			type = entity.getType();

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	/**
	 * search the before user and show the details
	 */
	public void loadDataBefore() {
		try {
			// in the user list get the actual position
			listDetail = lazyModel.getDatasource();
			for (int i = 0; i < listDetail.size(); i++) {
				if (listDetail.get(i).getId().equals(entity.getId())) {
					position = i;
					break;
				}
			}
			// get the before user
			entity = listDetail.get(--position < 0 ? listDetail.size() - 1
					: position);
			// load all the data
			entity = service.loadAllById(entity);

			type = entity.getType();

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	private String number = null;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * save or update the user
	 * 
	 * @return
	 */
	public String update() {
		try {

			if (country == null) {
				country = countryService.getDataOrder().get(0);
			}

			List<ClientApplication> list = clientApplications.getTarget();
			// if is an IBO remove "-" from EIN number
			if (type.isAnIbo() && corporation != null
					&& StringUtils.isNotBlank(corporation.getEin())) {
				corporation.setEin(corporation.getEin().replaceAll("-", ""));
			}

			if (edit) {
				service.update(entity, rol, group, profile, iboType, address,
						states, country, list, groupIbo, type, corporation,
						action, countryIbo, newPrincipalIBO,
						sessionBean.getUser());
			} else {
				service.makePersistent(entity, rol, group, profile, iboType,
						address, states, country, list, groupIbo, type,
						corporation, action, countryIbo, sessionBean.getUser());

				if (isIbo()) {
					// search user ID to show to the user
					// number = profileService.getProfileNumberByUser(entity
					// .getId());
					// profile.setNumber(number);
					if (StringUtils.isNotBlank(profile.getNumber())) {
						RequestContext.getCurrentInstance().execute(
								"PF('dlgConfirmationIdUser').show()");
						return null;
					}
				}
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

	@Override
	public UserService getService() {
		return service;
	}

	@Override
	public User getEntity() {
		if (entity == null) {
			entity = new User();
		}
		return entity;
	}

	public Profile getProfile() {
		if (profile == null) {
			profile = new Profile();
			profile.setOwnerType(OwnerType.PRINCIPAL);
			profile.setTypeContract(TypeContract.DOMESTIC);
			profile.setPayMethod(PayMethod.DIRECT_DEPOSIT);
		}
		return profile;
	}

	public Address getAddress() {
		if (address == null) {
			address = new Address();
		}
		return address;
	}

	public UserDataModel getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(UserDataModel lazyModel) {
		this.lazyModel = lazyModel;
	}

	public UserFilter getUserFilter() {
		return userFilter;
	}

	public void setUserFilter(UserFilter userFilter) {
		this.userFilter = userFilter;
	}

	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public List<User> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<User> listDetail) {
		this.listDetail = listDetail;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public List<UserUtil> getListUserAd() {
		return listUserAd;
	}

	public void setListUserAd(List<UserUtil> listUserAd) {
		this.listUserAd = listUserAd;
	}

	public UserUtil getUserAd() {
		return userAd;
	}

	public void setUserAd(UserUtil userAd) {
		this.userAd = userAd;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public RolService getRolService() {
		return rolService;
	}

	public void setRolService(RolService rolService) {
		this.rolService = rolService;
	}

	public List<Groups> getListGroup() {
		return listGroup;
	}

	public void setListGroup(List<Groups> listGroup) {
		this.listGroup = listGroup;
	}

	public Groups getGroup() {
		return group;
	}

	public void setGroup(Groups group) {
		this.group = group;
	}

	public Groups getGroupIbo() {
		return groupIbo;
	}

	public void setGroupIbo(Groups groupIbo) {
		this.groupIbo = groupIbo;
	}

	public GroupsService getGroupsService() {
		return groupsService;
	}

	public void setGroupsService(GroupsService groupsService) {
		this.groupsService = groupsService;
	}

	public IboType getIboType() {
		return iboType;
	}

	public void setIboType(IboType iboType) {
		this.iboType = iboType;
	}

	public DualListModel<ClientApplication> getClientApplications() {
		return clientApplications;
	}

	public void setClientApplications(
			DualListModel<ClientApplication> clientApplications) {
		this.clientApplications = clientApplications;
	}

	public ClientApplicationService getClientApplicationService() {
		return clientApplicationService;
	}

	public void setClientApplicationService(
			ClientApplicationService clientApplicationService) {
		this.clientApplicationService = clientApplicationService;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public States getStates() {
		return states;
	}

	public void setStates(States states) {
		this.states = states;
	}

	public List<States> getListStates() {
		if (listStates == null || listStates.size() == 0) {
			listStates = statesService.getDataByUS();
		}

		return listStates;
	}

	public void setListStates(List<States> listStates) {
		this.listStates = listStates;
	}

	public StatesService getStatesService() {
		return statesService;
	}

	public void setStatesService(StatesService statesService) {
		this.statesService = statesService;
	}

	public boolean isHaveStates() {
		return haveStates;
	}

	public void setHaveStates(boolean haveStates) {
		this.haveStates = haveStates;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public void setService(UserService service) {
		this.service = service;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getActionBack() {
		return actionBack;
	}

	public void setActionBack(String actionBack) {
		this.actionBack = actionBack;
	}

	public boolean isAddTemporal() {
		return addTemporal;
	}

	public void setAddTemporal(boolean addTemporal) {
		this.addTemporal = addTemporal;
	}

	// /**
	// * Fill the groups selectOneMenu
	// *
	// * @return
	// */
	// public List<Groups> getListGroupIbo() {
	// if (listGroupIbo == null) {
	// listGroupIbo = groupsService.getDataActive();
	// }
	// return listGroupIbo;
	// }
	//
	// public void setListGroupIbo(List<Groups> listGroupIbo) {
	// this.listGroupIbo = listGroupIbo;
	// }

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	public CountryService getCountryService() {
		return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public OracleService getOracleService() {
		return oracleService;
	}

	public void setOracleService(OracleService oracleService) {
		this.oracleService = oracleService;
	}

	public void validateFilter() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean error = false;
		try {

			if (userFilter.getEndDate() != null
					&& userFilter.getStartDate() != null
					&& userFilter.getEndDate()
							.before(userFilter.getStartDate())) {
				throw new Exception("start_end_date_incorrect");
			}

			if (userFilter.getEndDate() != null
					&& userFilter.getStartDate() == null) {
				throw new SystemException("date_end_without_start");
			}

		} catch (Exception e) {
			error = true;
			Context.addErrorMessageFromMSG(e.getMessage());
		} finally {
			context.addCallbackParam("error", error);
		}
	}

	public void cleanDetailFilter() {

		userFilter.setEndDate(null);
		userFilter.setStartDate(null);
	}

	// **************** ELIMINAR **********************/
	public void syncAD() {

		try {

			service.syncronizedUser();

			// service.makePersistent(dateIni, dateFin);

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	public InvoiceService getInvoiceService() {
		return invoiceService;
	}

	public void setInvoiceService(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	public void canceled() {

		try {

			invoiceService.cancelInvoice(new Date());

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}

	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	// public ZipCodeService getZipCodeService() {
	// return zipCodeService;
	// }
	//
	// public void setZipCodeService(ZipCodeService zipCodeService) {
	// this.zipCodeService = zipCodeService;
	// }
	//
	// public List<ZipCode> getZipCodes() {
	//
	// if (zipCodes == null) {
	// zipCodes = zipCodeService.getDataOrder();
	// }
	// return zipCodes;
	// }
	//
	// public void setZipCodes(List<ZipCode> zipCodes) {
	// this.zipCodes = zipCodes;
	// }

	public Corporation getCorporation() {
		if (corporation == null) {
			corporation = new Corporation();
		}
		return corporation;
	}

	public void setCorporation(Corporation corporation) {
		this.corporation = corporation;
	}

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public List<Corporation> getListCorporation() {
		if (listCorporation == null && profile != null
				&& StringUtils.isNotBlank(profile.getRegion())) {
			listCorporation = corporationService.getDataWithAddress(profile
					.getRegion());
		}
		return listCorporation;
	}

	public void setListCorporation(List<Corporation> listCorporation) {
		this.listCorporation = listCorporation;
	}

	public CallUpsUtil getCallUpsUtil() {
		return callUpsUtil;
	}

	public void setCallUpsUtil(CallUpsUtil callUpsUtil) {
		this.callUpsUtil = callUpsUtil;
	}

	public CorporationService getCorporationService() {
		return corporationService;
	}

	public void setCorporationService(CorporationService corporationService) {
		this.corporationService = corporationService;
	}

	public Corporation getCorporationExisting() {
		return corporationExisting;
	}

	public void setCorporationExisting(Corporation corporationExisting) {
		this.corporationExisting = corporationExisting;
	}

	public boolean isDisabledOwnerType() {
		return disabledOwnerType;
	}

	public void setDisabledOwnerType(boolean disabledOwnerType) {
		this.disabledOwnerType = disabledOwnerType;
	}

	public Country getCountryIbo() {
		return countryIbo;
	}

	public void setCountryIbo(Country countryIbo) {
		this.countryIbo = countryIbo;
	}

	public boolean isInternational() {
		isInternational = isIbo() && profile != null && !profile.isDomestic();
		return isInternational;
	}

	public void setInternational(boolean isInternational) {
		this.isInternational = isInternational;
	}

	public Corporation getCorporationOriginal() {
		return corporationOriginal;
	}

	public void setCorporationOriginal(Corporation corporationOriginal) {
		this.corporationOriginal = corporationOriginal;
	}

	public List<Profile> getListIbosCorporation() {
		// search all the ibo from the corporation
		// and define the ibo principal
		if (corporation != null && listIbosCorporation == null) {
			listIbosCorporation = new ArrayList<Profile>();
			List<Profile> listIbos = profileService
					.getDataByCorporationActive(corporation.getId());
			// search by the principal
			for (Profile ibo : listIbos) {
				if (ibo.getOwnerType().equals(OwnerType.PRINCIPAL)) {
				}
				corporation.setIdPrincipalOwner(ibo.getId());
				// add all the IBO but the edited IBO
				if (!ibo.getId().equals(profile.getId())) {
					listIbosCorporation.add(ibo);
				}
			}
		}
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

	public AddressService getAddressService() {
		return addressService;
	}

	public void setAddressService(AddressService addressService) {
		this.addressService = addressService;
	}

	public String getMessageSupplier() {
		return messageSupplier;
	}

	public void setMessageSupplier(String messageSupplier) {
		this.messageSupplier = messageSupplier;
	}

}
