package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.controller.ActionsCorporation;
import com.ghw.dao.AddressDAO;
import com.ghw.dao.BankInformationDAO;
import com.ghw.dao.ClientApplicationDAO;
import com.ghw.dao.ConfigurationGeneralDAO;
import com.ghw.dao.CorporationDAO;
import com.ghw.dao.GroupsDAO;
import com.ghw.dao.IboTemporalDAO;
import com.ghw.dao.IbosClientApplicationsDAO;
import com.ghw.dao.InvoiceDAO;
import com.ghw.dao.InvoiceWorkDAO;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.ProfileDAO;
import com.ghw.dao.RolDAO;
import com.ghw.dao.RolPermissionDAO;
import com.ghw.dao.UserDAO;
import com.ghw.dao.UserTypeDAO;
import com.ghw.model.Address;
import com.ghw.model.ClientApplication;
import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.Corporation;
import com.ghw.model.CorporationType;
import com.ghw.model.Country;
import com.ghw.model.Groups;
import com.ghw.model.IboState;
import com.ghw.model.IboTemporal;
import com.ghw.model.IboType;
import com.ghw.model.IbosClientApplications;
import com.ghw.model.LogSystem;
import com.ghw.model.OwnerType;
import com.ghw.model.PayMethod;
import com.ghw.model.PayType;
import com.ghw.model.Profile;
import com.ghw.model.Rol;
import com.ghw.model.States;
import com.ghw.model.TypeContract;
import com.ghw.model.User;
import com.ghw.model.UserType;
import com.ghw.model.UserUtil;
import com.ghw.service.EmailService;
import com.ghw.service.InvoiceService;
import com.ghw.service.ProfileService;
import com.ghw.service.UserService;
import com.ghw.util.ActiveDirectory;
import com.ghw.util.IboUtil;
import com.ghw.util.SystemException;

@Service("userService")
@Transactional(readOnly = false)
public class UserServiceImpl extends ServiceImpl<User, String, UserDAO>
		implements UserService {

	private UserDAO dao;

	@Autowired
	private RolPermissionDAO rolPermissionDao;

	@Autowired
	private LogSystemDAO logSystemDao;

	@Autowired
	private IboTemporalDAO iboTemporalDao;

	@Autowired
	private GroupsDAO groupsDAO;

	@Autowired
	private ProfileDAO profileDAO;

	@Autowired
	private CorporationDAO corporationDAO;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private ClientApplicationDAO clientApplicationDAO;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AddressDAO addressDAO;

	@Autowired
	private InvoiceDAO invoiceDAO;

	@Autowired
	private IbosClientApplicationsDAO ibosClientApplicationsDAO;

	// @Autowired remove it because cause error in SyncTask
	// private SessionBean sessionBean;

	@Autowired
	private BankInformationDAO bankInformationDAO;

	@Autowired
	ConfigurationGeneralDAO configurationGeneralDAO;

	@Autowired
	private InvoiceWorkDAO invoiceWorkDAO;

	@Autowired
	private RolDAO rolDAO;
	@Autowired
	private UserTypeDAO userTypeDAO;

	private ActiveDirectory ad = new ActiveDirectory();

	@Autowired
	public void setDao(UserDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	private String getUserFromAD(String name, String password,
			ConfigurationGeneral configuration) throws SystemException,
			NamingException {

		if (configuration == null)
			throw new SystemException("ad_configuration_necesary");

		UserUtil user = null;
		String response = "ok";
		try {

			ad.openConnection(configuration);
			user = ad.searchAccount(name);

			if (user == null || StringUtils.isEmpty(user.getUserName()))
				return "user_not_found";

			ad.openConnectionUser(configuration, name, password);

			ad.closeLdapConnection();

		} catch (NamingException e) {
			// I need to know if is 533 account disabled
			if (e.getMessage().contains("data 533")) {
				// user.setState(false);
				return "user_inactive";
			} else if (e.getMessage().contains("data 775")) {
				// inactive in the system
				dao.changeState(name);
				dao.flush();
				return "user_locked";
				// throw new UsernameNotFoundException("user_locked");
			} else if (e.getMessage().contains("data 525")) {
				return "user_not_found";
				// throw new UsernameNotFoundException("user_not_found");
			} else if (e.getMessage().contains("data 52e")) {
				return "pass_wrong";
				// throw new UsernameNotFoundException("pass_wrong");
			} else if (e.getMessage().contains("data 532")) {
				return "pass_expired";
				// throw new UsernameNotFoundException("pass_expired");
			} else if (e.getMessage().contains("data 773")) {
				return "pass_need_reset";
			}
			// throw new UsernameNotFoundException("pass_need_reset");

			return "error";
		}

		return response;
	}

	private int getErrorCode(final String exceptionMsg) {
		String pattern = "-?\\d+";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(exceptionMsg);
		if (m.find()) {
			return Integer.valueOf(m.group(0));
		}
		return -1;
	}

	/**
	 * Validate if exist user with good credencial in AD
	 * 
	 * @throws NamingException
	 * @throws SystemException
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = UsernameNotFoundException.class)
	public User getUser(String name, String password,
			ConfigurationGeneral configuration) throws SystemException,
			NamingException, UsernameNotFoundException {

		User user = null;

		// First validate if exists in my system
		user = dao.getUserLogin(name);

		if (user == null) {
			throw new UsernameNotFoundException("user_not_found");
		}

		// if is an IBO international can't enter to the system ONLY if
		// iboInternationalLikeDomestic = false
		if (user.isAnIbo()
				&& user.getIbo().getTypeContract()
						.equals(TypeContract.INTERNATIONAL)) {
			throw new UsernameNotFoundException("error_login_permission");
		}

		// if is super admin validate password in my system and not in AD
		if (user.isSuperAdmin()) {

			if (!user.isState()) {
				throw new UsernameNotFoundException("user_inactive");
			}

			if (!dao.validateSuperAdmin(user, password)) {
				throw new UsernameNotFoundException("pass_wrong");
			}
		}

		// UserUtil userUtil = null;
		String responseAD = null;
		// if exist search in AD
		if (!user.isSuperAdmin()) {
			responseAD = getUserFromAD(name, password, configuration);
			if (!responseAD.equals("ok") && !responseAD.equals("user_inactive")) {
				throw new UsernameNotFoundException(responseAD);
			}
			// if the user is inactive in AD and active in IMS when login
			// inactive
			if (responseAD.equals("user_inactive") && user.isState()) {
				changeStateCommom(user, null);
				user.setState(false);
			}
		}

		// if user is not an IBO and is inactive
		if (!(user.isAnIbo() && user.getIbo().getOwnerType()
				.equals(OwnerType.PRINCIPAL))
				&& !user.isState()) {
			throw new UsernameNotFoundException("user_inactive");
		}

		// if is an IBO and is inactive and Principal owner type validate the
		// date deactivation
		// if is <= 60 days only have permission for bank account
		if (user.isAnIbo()
				&& user.getIbo() != null
				&& user.getIbo().getPayMethod()
						.equals(PayMethod.DIRECT_DEPOSIT)
				&& user.getIbo().getOwnerType().equals(OwnerType.PRINCIPAL)
				&& !user.isState()) {
			Date dateInactive = user.getStateDate() == null ? new Date() : user
					.getStateDate();
			Date date = new Date();
			long diff = date.getTime() - dateInactive.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			System.out.print(diffDays + " days, ");

			// if is more than 60 day does not have permission
			if (diffDays > 60) {
				throw new UsernameNotFoundException("user_inactive");
			}

			// if is less than 60 days has permission only for bank account
			// information
			user.getPermissions().add("ROLE_IBO");
			user.setGoToBank(true);

		} else {

			// get all permission
			// if is super admin have access to all
			if (user.isSuperAdmin()) {
				user.getPermissions().add("ROLE_SUPER");
			} else {
				user.setPermissions(rolPermissionDao
						.getPermissionSpringByUser(user.getId().toString()));

				// if IBO add permission IBO
				if (user != null && user.getType() != null
						&& StringUtils.isNotBlank(user.getType().getId())
						&& user.isAnIbo()) {

					user.getPermissions().add("ROLE_IBO");

					// if is an IBO need to get if has bank account information
					// only matter if the IBO is Domestic or if the
					// international behavior is equals to Domestic
					if (user.getIbo() != null
							&& user.getIbo().getPayMethod()
									.equals(PayMethod.DIRECT_DEPOSIT)
							&& user.getIbo().getOwnerType()
									.equals(OwnerType.PRINCIPAL)
							&& (user.getIbo().isDomestic())) {
						user.setGoToBank(profileDAO.validateIfGoToBank(user
								.getId()));
					}

				}
			}
		}

		if (user.isGoToBank()) {
			user.getPermissions().add("ROLE_GO_BANK");
		}

		return user;
	}

	@Override
	public User updateLastLogin(User user) throws Exception {

		dao.updateLastLogin(user);

		LogSystem log = new LogSystem();
		log.setClassName(user.getClass().getSimpleName());
		log.setDetail(user.toString());
		log.setMethod("login");
		log.setClassId(user.getId().toString());
		log.setUser(new User(user.getId()));
		log.setIp(user.getIp());

		logSystemDao.makePersistent(log);

		return user;

	}

	@Override
	public void syncronizedUser() throws Exception {

		ConfigurationGeneral configuration = configurationGeneralDAO.getData();

		ad.openConnection(configuration);

		Integer cantUserInactivo = 0, cantUserActivo = 0, cantIboTempInactivo = 0, cantIboTempElim = 0, cantIboAdd = 0;

		// get all user exists AD
		List<UserUtil> listUser = ad.searchAllUser(configuration.getLdapDn());

		// get list IBO before insert
		List<String> listIBOIMS = iboTemporalDao.getDataName();

		// get list user
		Map<String, User> listUserSystem = dao.getUserForAd();

		// need to compare if exist user in my system and not in AD
		List<String> userADNotExistIMS = new ArrayList<String>();

		// need for search all IMS user don't exist in AD
		List<String> userAD = new ArrayList<String>();

		// need for save all the user IBO
		List<String> listIboIMS = new ArrayList<String>();

		String methodLogg = "synchronizeUserAD";

		int cantProc = 0, cantUpdated = 0;
		for (UserUtil userAd : listUser) {

			cantProc++;

			String nameAD = userAd.getUserName();
			userAD.add(nameAD);

			User user = listUserSystem.get(nameAD);

			if (user == null)
				userADNotExistIMS.add(nameAD);

			String distinName = userAd.getDistinguishedName().toLowerCase();
			// Need to know is an IBO
			// boolean isAnIbo =
			// distinName.contains(configuration.getLdapDnIbo()
			// .toLowerCase())
			// || distinName.contains(configuration.getLdapDnIdc()
			// .toLowerCase());
			boolean isAnIbo = IboUtil.isDNBelongIbo(distinName, configuration);

			IboTemporal iboTemporal = null;
			if (isAnIbo) {
				iboTemporal = iboTemporalDao.getIboByName(nameAD);
				listIboIMS.add(nameAD);
			}

			// analize state in AD
			boolean stateAD = userAd.isState();

			// update data from user
			if (user != null && !user.isSuperAdmin() && !user.compareAD(userAd)) {
				cantUpdated++;
				dao.updateAD(user, userAd);
			}

			// Inactive user if AD = inactive and active=IMS
			// AD = active and deactive=IMS is OK do nothing
			if (user != null && !user.isSuperAdmin()
					&& user.isState() != stateAD) {

				changeStateUser(user, methodLogg, null);

				if (!stateAD) {
					cantUserInactivo++;
				} else {
					cantUserActivo++;
				}

			} else if (iboTemporal != null && iboTemporal.isState() != stateAD
					&& !stateAD) {
				inactiveIbo(iboTemporal, methodLogg);
				cantIboTempInactivo++;
			} else if (user == null && iboTemporal == null && isAnIbo
					&& stateAD) {
				// If not exist in IMS (user, temp) and is IBO
				createIboTemporal(userAd, methodLogg, configuration);
				cantIboAdd++;
			}

		}

		// get all user active exist IMS and not exist AD (list username get
		// from AD)
		List<String> listUserIMS = dao.getDataNameExceptSuper();
		// remove all exist in AD
		listUserIMS.removeAll(userAD);
		// for every user inactive in IMS
		for (String userName : listUserIMS) {
			inactiveUser(userName, methodLogg, null);
			cantUserInactivo++;
		}

		// get all IBO temporal exist IMS and not exist AD (list username get
		// from AD)
		// remove all exist in AD
		listIBOIMS.removeAll(userAD);
		// for every user inactive in IMS
		if (listIBOIMS != null && listIBOIMS.size() > 0) {

			cantIboTempElim = listIBOIMS.size();
			// delete all
			iboTemporalDao.delete(listIBOIMS);

			String detail = "Delete all the IBO in temporal. Cause: Not found in AD. Name list: "
					+ listIBOIMS;
			// insert logg
			logSystemDao.makePersistent(new LogSystem(new IboTemporal()
					.getClass().getSimpleName(), methodLogg, detail, "", false,
					null, null));

		}

		String detail = "AD synchronization done. Total processed: " + cantProc
				+ " Inactive users: " + cantUserInactivo + " Active users: "
				+ cantUserActivo + " Updated users: " + cantUpdated
				+ " Deactive IBO temporal: " + cantIboTempInactivo
				+ " IBO temporal added: " + cantIboAdd
				+ " IBO temporal deleted: " + cantIboTempElim;
		// insert logg
		logSystemDao.makePersistent(new LogSystem("Timer", methodLogg, detail,
				"", false, null, null));

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	private void createIboTemporal(UserUtil userAd, String methodLogg,
			ConfigurationGeneral conf) throws NamingException {
		// Creo el IBO Temporal
		IboTemporal iboTemporal = new IboTemporal();
		iboTemporal.setId(userAd.getUserName());
		iboTemporal.setUserName(userAd.getUserName());
		iboTemporal.setFirstName(userAd.getFirstName() == null ? " " : userAd
				.getFirstName());
		iboTemporal.setMiddleName(userAd.getMiddleName() == null ? " " : userAd
				.getMiddleName());
		iboTemporal.setLastName(userAd.getLastName() == null ? " " : userAd
				.getLastName());
		iboTemporal.setEmail(userAd.getEmail());

		iboTemporal.setTypeContract(IboUtil.getTypeContract(
				userAd.getDistinguishedName(), conf));

		iboTemporalDao.makePersistent(iboTemporal);
		iboTemporalDao.flush();

		// insert log system
		LogSystem log = new LogSystem();
		log.setClassName(iboTemporal.getClass().getSimpleName());
		log.setDetail(iboTemporal.toString());
		log.setMethod(methodLogg);
		log.setUser(null);
		log.setClassId(iboTemporal.getId());

		logSystemDao.makePersistent(log);
		logSystemDao.flush();
	}

	private void inactiveUser(String userName, String methodLogg,
			User userSession) {
		User user = dao.getUserByName(userName);
		changeStateUser(user, methodLogg, userSession);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	private void changeStateUser(User user, String methodLogg, User userSession) {
		// deactive user
		changeStateCommom(user, userSession);

		// insert log system
		LogSystem log = new LogSystem();
		log.setClassName(user.getClass().getSimpleName());
		log.setDetail(user.toString() + user.getFieldAdicional());
		log.setMethod(methodLogg);
		log.setUser(userSession);
		log.setClassId(user.getIdentity());

		logSystemDao.makePersistent(log);
		logSystemDao.flush();

		// user type = IBO submitted invoice state = Pending
		// insert log system
		if (user.getType() != null && user.getType().getId() != null
				&& user.isAnIbo())
			invoiceService.submitOrCancelInvoice(user, userSession);

		// if is an PA and has a group associate remove from group
		// search the old group
		// if (user.getType() != null && user.getType().getId() != null
		// && user.isAnPA()) {
		// Groups oldGroup = groupsDAO.getDataByUser(user);
		// if (oldGroup != null)
		// groupsDAO.updateUser(oldGroup, null);
		// }
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	private void activeUser(User user, String methodLogg, User userSession) {
		// deactive user
		changeStateCommom(user, userSession);

		// insert log system
		LogSystem log = new LogSystem();
		log.setClassName(user.getClass().getSimpleName());
		log.setDetail(user.toString() + user.getFieldAdicional());
		log.setMethod(methodLogg);
		log.setUser(null);
		log.setClassId(user.getIdentity());

		logSystemDao.makePersistent(log);
		logSystemDao.flush();

		// user type = IBO submitted invoice state = Pending
		// insert log system
		if (user.getType() != null && user.getType().getId() != null
				&& user.isAnIbo())
			invoiceService.submitOrCancelInvoice(user, userSession);

		// if is an PA and has a group associate remove from group
		// search the old group
		// if (user.getType() != null && user.getType().getId() != null
		// && user.isAnPA()) {
		// Groups oldGroup = groupsDAO.getDataByUser(user);
		// if (oldGroup != null)
		// groupsDAO.updateUser(oldGroup, null);
		// }
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	private void inactiveIbo(IboTemporal ibo, String methodLogg) {
		// deactive user
		iboTemporalDao.changeState(ibo, null);

		ibo.setState(!ibo.isState());
		// insert log system
		LogSystem log = new LogSystem();
		log.setClassName(ibo.getClass().getSimpleName());
		log.setDetail(ibo.toString() + " Old state: Active");
		log.setMethod(methodLogg);
		log.setUser(null);
		log.setClassId(ibo.getId());

		logSystemDao.makePersistent(log);

		// user type = IBO submitted invoice state = Pending
		// insert log system
	}

	public void setLogSystemDao(LogSystemDAO logSystemDao) {
		this.logSystemDao = logSystemDao;
	}

	@Override
	public List<User> getDataWithoutGroup(Groups group) {
		return dao.getDataWithoutGroup(group);
	}

	/**
	 * Get all the active user from Active Directory
	 */
	@Override
	public List<UserUtil> getDataActiveAd(ConfigurationGeneral configuration)
			throws Exception {

		if (configuration == null)
			throw new SystemException("ad_configuration_necesary");

		// search all user in my system to exclude from AD
		List<String> listUserSystem = dao.getDataName();

		ad.openConnection(configuration);
		List<UserUtil> listUser = ad.searchAccountActiveListExclude(
				listUserSystem, configuration.getLdapDn());

		// ad.openConnection(configuration);
		// NamingEnumeration<SearchResult> listAd = ad
		// .searchAccountActiveListExclude(listUserSystem,
		// configuration.getLdapDn());
		//
		// List<UserUtil> listUser = new ArrayList<UserUtil>();
		//
		// while (listAd.hasMoreElements()) {
		//
		// UserUtil user = new UserUtil();
		//
		// SearchResult rs = (SearchResult) listAd.nextElement();
		// Attributes attrs = rs.getAttributes();
		//
		// NamingEnumeration<String> ids = attrs.getIDs();
		// while (ids.hasMoreElements()) {
		// String s = (String) ids.nextElement();
		// Attribute temp = attrs.get(s);
		// if (temp != null) {
		// if (temp.getID().equals("sAMAccountName"))
		// user.setUserName(temp.get().toString());
		// else if (temp.getID().equals("givenName"))
		// user.setFirstName(temp.get().toString());
		// else if (temp.getID().equals("sn"))
		// user.setLastName(temp.get().toString());
		// else if (temp.getID().equals("mail"))
		// user.setEmail(temp.get().toString());
		// else if (temp.getID().equals("telephoneNumber"))
		// user.setTelephoneNumber(temp.get().toString());
		// else if (temp.getID().equals("distinguishedName"))
		// user.setDistinguishedName(temp.get().toString());
		//
		// }
		// }
		//
		// // if (!listUserSystem.contains(user.getUserName()))
		// listUser.add(user);
		//
		// }
		//
		ad.closeLdapConnection();

		return listUser;
	}

	/**
	 * Validate the the name and email not exists in system before save or
	 * update
	 */
	@Override
	public void isValidate(User entity) throws Exception {
		List<SystemException> exceptions = new ArrayList<SystemException>();

		if (StringUtils.isBlank(entity.getName()))
			exceptions.add(new SystemException("name_empty"));

		if (StringUtils.isBlank(entity.getFirstName()))
			exceptions.add(new SystemException("first_name_empty"));

		if (StringUtils.isBlank(entity.getLastName()))
			exceptions.add(new SystemException("last_name_empty"));

		if (StringUtils.isBlank(entity.getEmail()))
			exceptions.add(new SystemException("email_empty"));

		if (exceptions.size() > 0)
			throw new SystemException(exceptions);

		if (dao.validateIfExistsName(entity.getName(), entity.getId()))
			throw new SystemException("name_exists");

		if (StringUtils.isNotBlank(entity.getEmail())
				&& dao.validateIfExistsEmail(entity.getEmail(), entity.getId()))
			throw new SystemException("email_exists");

	}

	private void validateCorporationData(Corporation corporation,
			Address address, States states) throws SystemException {

		List<SystemException> exceptions = new ArrayList<SystemException>();

		if (StringUtils.isBlank(corporation.getName()))
			exceptions.add(new SystemException("corporation_name_required",
					"form:txtCorp"));

		if (StringUtils.isBlank(corporation.getEin()))
			exceptions.add(new SystemException("ein_required", "form:txtein"));

		if (StringUtils.isBlank(address.getDescription()))
			exceptions.add(new SystemException("address_required",
					"form:txtAddress"));

		if (StringUtils.isBlank(address.getZipCode()))
			exceptions.add(new SystemException("zip_required", "form:txtZip"));

		if (states == null || StringUtils.isBlank(states.getId()))
			exceptions.add(new SystemException("state_required",
					"form:cmdStateUS"));

		if (StringUtils.isBlank(address.getCity()))
			exceptions
					.add(new SystemException("city_required", "form:txtCity"));

		if (exceptions.size() > 0)
			throw new SystemException(exceptions);

		// validate Corporation name not exists
		if (corporationDAO.validateIfExistsName(corporation.getName(),
				corporation.getId()))
			throw new SystemException("corporation_name_exists", "form:txtCorp");

		if (corporationDAO.validateIfExistsEin(corporation.getEin(),
				corporation.getId()))
			throw new SystemException("ein_exists", "form:txtein");

	}

	/**
	 * insert an user
	 */
	@Override
	@Transactional(readOnly = false)
	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','USER_A','BOARD_A')")
	public User makePersistent(User entity, Rol rol, Groups group,
			Profile profile, IboType type, Address address, States state,
			Country country, List<ClientApplication> cas, Groups groupIbo,
			UserType typeUser, Corporation corporation,
			Integer actionsCorporation, Country countryIbo, User userSession)
			throws Exception {

		isValidate(entity);

		// validate corporation data
		if (profile != null && typeUser.isAnIbo()
				&& !actionsCorporation.equals(ActionsCorporation.NONE))
			validateCorporationData(corporation, address, state);

		// must to define null because cause exception UNIQUE if exits more than
		// one with empty email
		entity.setEmail(StringUtils.isBlank(entity.getEmail()) ? null : entity
				.getEmail());

		// entity.setUserCreated(sessionBean.getUser());

		// insert user
		entity.setRol(rol);
		entity.setType(userTypeDAO.loadById(typeUser.getId()));

		dao.makePersistent(entity);
		dao.flush();

		if (profile != null && typeUser.isAnIbo()) {
			// if is an IBO with type PA and has group associate
			// define the group inverse because the group is like PA not like
			// IBO
			// if (profile.isAnPA()) {
			// group = groupIbo;
			// groupIbo = null;
			// }

			if (!actionsCorporation.equals(ActionsCorporation.NONE))
				corporation = insertOrUpdateCorporation(corporation, address,
						state, country, profile.getTypeContract(), userSession);

			insertProfile(entity, rol, profile, type, cas, groupIbo, typeUser,
					actionsCorporation.equals(ActionsCorporation.NONE) ? null
							: corporation, countryIbo, userSession);
		}

		// is sme and have group associate update the user in the group
		// Also if is an IBO with type PA
		if ((typeUser.isAnPA() || (profile != null && profile.isAnPA()))
				&& group != null && StringUtils.isNotBlank(group.getId())) {
			groupsDAO.updateUser(group, entity);
		}

		// delete if exist in IBO temporal
		iboTemporalDao.delete(entity.getName());

		if (corporation != null)
			entity.setFieldAdicional(corporation.getFieldAdicional());

		return entity;
	}

	private void insertProfile(User entity, Rol rol, Profile profile,
			IboType type, List<ClientApplication> cas, Groups groupIbo,
			UserType typeUser, Corporation corporation, Country countryIbo,
			User userSession) {

		// by default the status is Pending
		String status = IboState.PENDING;
		profile.setType(type);
		profile.setUserCreated(userSession);
		profile.setUser(entity);
		profile.setGroup(groupIbo);
		profile.setCountry(countryIbo);
		profile.setCorporation(corporation);

		List<IbosClientApplications> caInser = new ArrayList<IbosClientApplications>();
		for (ClientApplication ca : cas) {
			IbosClientApplications ica = new IbosClientApplications();
			ica.setClientApplication(ca);
			ica.setUser(profile);

			caInser.add(ica);
		}

		profile.setClientApplications(caInser);

		// set the Pay type, Domestic type has Direct_Deposit, the IDC has
		// PayPal and the normal International NONE
		if (profile.getTypeContract().equals(TypeContract.DOMESTIC)) {
			profile.setPayType(PayType.CORPORATION);
			profile.setPayMethod(PayMethod.DIRECT_DEPOSIT);
		} else if (profile.getTypeContract().equals(
				TypeContract.INT_INDEP_CONTRACT)) {
			profile.setPayType(PayType.USER);
			profile.setPayMethod(PayMethod.PAYPAL);
		} else {
			profile.setPayType(PayType.NONE);
			profile.setPayMethod(PayMethod.NONE);
		}

		// if is an IBO international and has CA the status is OK
		// The International IBO dont need Bank Account Information
		if (profile.getClientApplications() != null
				&& profile.getClientApplications().size() > 0) {
			if (!profile.getPayMethod().equals(PayMethod.DIRECT_DEPOSIT))
				status = IboState.OK;
			else {
				// search if the company has the bank information
				// in that case change status to OK
				if (bankInformationDAO.getExistByCorporation(corporation))
					status = IboState.OK;
			}
		}

		profile.setIboState(new IboState(status));

		profileDAO.makePersistent(profile);
		profileDAO.flush();

		profile.setNumber(profileDAO.getProfileNumberByUser(entity.getId()));

		// send welcome email only to the domestic IBO
		if (!profile.getTypeContract().equals(TypeContract.INTERNATIONAL))
			emailService.sendWelcomeEmail(profile);

		// create invoice for this period
		if (status.equals(IboState.OK))
			invoiceService.createInvoiceIbo(profile, userSession);

	}

	@Override
	@Transactional(readOnly = false)
	@Loggable
	@PreAuthorize("hasAnyRole('SUPER','USER_M','BOARD_M')")
	public User update(User entity, Rol rol, Groups group, Profile profile,
			IboType type, Address address, States state, Country country,
			List<ClientApplication> cas, Groups groupIbo, UserType typeUser,
			Corporation corporation, Integer actionsCorporation,
			Country countryIbo, Profile newPrincipalIBO, User userSession)
			throws Exception {

		isValidate(entity);

		if (!entity.isState() && !validateIfDeactivate(entity))
			throw new SystemException("invoice_associate_user");

		// validate corporation data
		if (profile != null && typeUser.isAnIbo()
				&& !actionsCorporation.equals(ActionsCorporation.NONE)) {

			validateCorporationData(corporation, address, state);

			// validate that all the user in the corporation has the same region
			if (profileDAO.hasCorporationIboOtherRegion(corporation, profile))
				throw new SystemException("corporation_different_region");
		}

		entity.setRol(rol);
		entity.setType(typeUser);

		// Get the old user to compare
		User old = dao.getById(entity.getId());

		entity.compare(old);

		// save the field ending
		entity.setNeedUpdate(old.isNeedUpdate());
		entity.setLastLogin(old.getLastLogin());
		entity.setStateDate(old.getStateDate());

		dao.merge(entity);
		dao.flush();

		if (profile != null) {
			if (typeUser.isAnIbo()) {
				// if is an IBO with type PA and has group associate
				// define the group inverse because the group is like PA not
				// like
				// IBO
				// if (profile.isAnPA()) {
				// group = groupIbo;
				// groupIbo = null;
				// }

				corporation = actionsCorporation
						.equals(ActionsCorporation.NONE) ? null : corporation;

				if (corporation != null)
					corporation = insertOrUpdateCorporation(corporation,
							address, state, country, profile.getTypeContract(),
							userSession);

				// if the Owner Type is Secondary and the newPrincipalIBO is not
				// null
				// updated the type to principal
				if (profile.getOwnerType().equals(OwnerType.SECONDARY)
						&& newPrincipalIBO != null
						&& StringUtils.isNotBlank(newPrincipalIBO.getId())) {
					profileDAO.updateIBOToPrincipal(newPrincipalIBO);
				}

				if (StringUtils.isBlank(profile.getId()))
					insertProfile(entity, rol, profile, type, cas, groupIbo,
							typeUser, corporation, countryIbo, userSession);
				else
					updateProfile(entity, rol, profile, type, cas, groupIbo,
							typeUser, corporation, countryIbo, userSession);
			} else {
				profileDAO.clearGroup(profile);
			}
		}

		// search the old group
		Groups oldGroup = groupsDAO.getDataByUser(entity);
		if (oldGroup != null)
			groupsDAO.updateUser(oldGroup, null);

		String fieldAdicional = "";
		if (group != null && oldGroup != null && !group.equals(oldGroup))
			fieldAdicional += " New group: " + group.toString()
					+ " Old group: " + oldGroup.toString();
		else if (group != null && oldGroup == null)
			fieldAdicional += " New group: " + group.toString()
					+ " Old group: Undefined";
		else if (group == null && oldGroup != null)
			fieldAdicional += " New group: Undefined" + " Old group: "
					+ oldGroup.toString();

		// is PA and have group associate update the user in the group

		if ((typeUser.isAnPA() || (profile != null && profile.isAnPA()))
				&& group != null && StringUtils.isNotBlank(group.getId()))
			groupsDAO.updateUser(group, entity);

		entity.setFieldAdicional(entity.getFieldAdicional()
				+ fieldAdicional
				+ (profile != null ? profile.getFieldAdicional() : "")
				+ (corporation != null ? " " + corporation.getFieldAdicional()
						: ""));

		return entity;
	}

	private Corporation insertOrUpdateCorporation(Corporation corporation,
			Address address, States state, Country country,
			TypeContract typeContract, User userSession) {

		corporation
				.setType(typeContract.equals(TypeContract.DOMESTIC) ? CorporationType.DOMESTIC
						: CorporationType.INTERNATIONAL);
		corporation
				.setPayMethod(typeContract.equals(TypeContract.DOMESTIC) ? PayMethod.DIRECT_DEPOSIT
						: PayMethod.NONE);

		// if corporation ID is null inserted
		if (StringUtils.isBlank(corporation.getId())) {
			// for insert
			address.setUserCreated(userSession);
			if (StringUtils.isBlank(address.getState()))
				address.setStates(state);
			address.setCountry(country);
			address.setCorporation(corporation);

			List<Address> addresses = new ArrayList<Address>();
			addresses.add(address);
			corporation.setAddress(addresses);

			corporation.setUserCreated(userSession);
			corporation.setCreatedDate(new Date());

			// corporation.setFieldAdicional(" New Corporation: "+corporation.toString());

			corporation = corporationDAO.makePersistent(corporation);

		} else {
			// if not updated

			corporation.setUserUpdated(userSession);
			corporation.setUpdateDate(new Date());
			// Get the address before
			Address adOld = addressDAO.getById(address.getId());

			if (StringUtils.isBlank(address.getId()))
				address.setUserCreated(userSession);
			else {
				address.setUserUpdated(userSession);
				address.setUpdateDate(new Date());
			}
			if (StringUtils.isBlank(address.getState()))
				address.setStates(state);
			address.setCountry(country);
			address.setCorporation(corporation);

			address.compare(adOld);

			List<Address> addresses = new ArrayList<Address>();
			addresses.add(address);

			corporation.setAddress(addresses);

			// Get all corporation to compare
			Corporation corporationOld = corporationDAO.loadById(corporation
					.getId());

			corporation.compare(corporationOld);

			corporation.setFieldAdicional(corporation.getFieldAdicional() + " "
					+ address.getFieldAdicional());

			corporation = corporationDAO.merge(corporation);

		}

		return corporation;
	}

	private void updateProfile(User entity, Rol rol, Profile profile,
			IboType type, List<ClientApplication> cas, Groups groupIbo,
			UserType typeUser, Corporation corporation, Country countryIbo,
			User userSession) {

		profile.setType(type);
		profile.setUserUpdated(userSession);
		profile.setUpdateDate(new Date());
		profile.setUser(entity);
		profile.setGroup(groupIbo);
		profile.setCorporation(corporation);
		profile.setCountry(countryIbo);

		// if has CA and the status is NOT 4, if is international change to OK
		// if is domestic IBO search if has bank account
		// information
		// in this case change status to OK
		if (cas != null
				&& cas.size() > 0
				&& !profile.getIboState().getId().equals(IboState.OK)
				&& (!profile.isDomestic() || (profile.getPayMethod().equals(
						PayMethod.DIRECT_DEPOSIT) && bankInformationDAO
						.getExistByCorporation(corporation)))) {

			profile.setIboState(new IboState(IboState.OK));

			// create invoice for this week
			invoiceService.createInvoiceIbo(profile, userSession);
		}

		Profile old = profileDAO.getById(profile.getId());
		old.setListCa(clientApplicationDAO.getDataByIbo(profile));
		profile.setListCa(cas);
		profile.compare(old);

		// delete client application actual
		ibosClientApplicationsDAO.deleteByIbo(profile);
		ibosClientApplicationsDAO.flush();

		List<IbosClientApplications> caInser = new ArrayList<IbosClientApplications>();
		for (ClientApplication ca : cas) {
			IbosClientApplications ica = new IbosClientApplications();
			ica.setClientApplication(ca);
			ica.setUser(profile);

			caInser.add(ica);
		}

		profile.setClientApplications(caInser);

		// set the Pay type, Domestic type has Direct_Deposit, the IDC has
		// PayPal and the normal International NONE
		if (profile.getTypeContract().equals(TypeContract.DOMESTIC)) {
			profile.setPayType(PayType.CORPORATION);
			profile.setPayMethod(PayMethod.DIRECT_DEPOSIT);
		} else if (profile.getTypeContract().equals(
				TypeContract.INT_INDEP_CONTRACT)) {
			profile.setPayType(PayType.USER);
			profile.setPayMethod(PayMethod.PAYPAL);
		} else {
			profile.setPayType(PayType.NONE);
			profile.setPayMethod(PayMethod.NONE);
		}

		// if has invoice and modify any field need to update the invoice with
		// the name and amount before
		int cantInvoicesModif = 0;
		if (!profile.getAmount().equals(old.getAmount())
				&& profile.getDateModification() != null
				&& profile.getAmount() > 0)
			cantInvoicesModif = invoiceWorkDAO.updateProfileRate(profile,
					userSession);

		profile.setFieldAdicional(profile.getFieldAdicional()
				+ (cantInvoicesModif > 0 ? " Total invoices modified: "
						+ cantInvoicesModif : ""));

		// save the field ending
		profile.setTotalSubmit(old.getTotalSubmit());
		profile.setNeedUpdate(old.isNeedUpdate());

		profileDAO.merge(profile);
		profileDAO.flush();

	}

	@Override
	@Transactional(readOnly = true)
	public User loadAllById(User entity) throws Exception {
		User cl = dao.loadAllById(entity);

		cl.setIbo(profileService.loadAllById(entity.getId()));

		if (cl.isAnIbo())
			cl.setGoToBank(bankInformationDAO.getExistByCorporation(cl.getIbo()
					.getCorporation()));

		return cl;
	}

	@Override
	@Transactional(readOnly = true)
	public User loadAllByIdToEdit(User entity) throws Exception {

		User cl = dao.loadAllByIdToEditVersion2(entity);

		// cl.setIbo(profileService.loadAllByIdToEdit(entity.getId()));

		return cl;
	}

	// @Override
	// @Transactional(readOnly = true)
	// public User loadAllByIdToEdit(User entity) throws Exception {
	// User cl = dao.loadAllByIdToEdit(entity);
	//
	// cl.setIbo(profileService.loadAllByIdToEdit(entity.getId()));
	//
	// return cl;
	// }

	@Transactional(readOnly = true)
	public User loadAllByIdToEdit2(User entity) throws Exception {
		User cl = dao.loadAllByIdToEditVersion2(entity);

		// cl.setIbo(profileService.loadAllByIdToEdit(entity.getId()));

		return cl;
	}

	/**
	 * Return true if the user can be change to Deactivate
	 */
	@Override
	public boolean validateIfDeactivate(User entity) {

		if (entity.isSuperAdmin())
			return false;

		if (invoiceDAO.validateAssociateIbo(entity.getId()))
			return false;

		return true;

	}

	private User changeStateCommom(User entity, User userSession) {

		dao.changeState(entity, userSession);
		dao.flush();

		// if the user is deactive remove from group both PA and IBO
		if (entity.isState()) {
			profileDAO.clearGroup(entity.getId());
			profileDAO.flush();
			groupsDAO.clearUser(entity);
			groupsDAO.flush();

			// remove program associate
			ibosClientApplicationsDAO.deleteByIbo(entity.getId());
			ibosClientApplicationsDAO.flush();
		}

		if (entity.isAnIbo() && entity.isState()) {
			// if is an IBO, is inactive and exists only another
			// IBO make than one "Principal", if exists more than one IBO show
			// in problem screen.

			Profile p = profileDAO.getDataForInactive(entity.getId());

			if (p.getOwnerType().equals(OwnerType.PRINCIPAL)
					&& p.getTotalIboCorporation().equals(1L)
					&& StringUtils.isNotBlank(p.getIdNewIboPrincipal()))
				profileDAO.updateIBOToPrincipal(new Profile(p
						.getIdNewIboPrincipal()));

			// change the actual user to Owner Type Secondary
			profileDAO.updateOwnerType(entity.getId(), OwnerType.SECONDARY);

		}

		// if the user is an IBO and is active, create invoice
		if (entity.isAnIbo() && !entity.isState()) {
			invoiceService.createInvoiceIbo(
					profileDAO.getDataForUser(entity.getId()), userSession);
		}

		entity.setState(!entity.isState());
		entity.setFieldAdicional(" Old state: "
				+ (entity.isState() ? "Inactive" : "Active"));

		return entity;
	}

	/**
	 * Change the state and save the date and the user that make the action
	 */
	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','USER_C','BOARD_C')")
	public User changeState(User entity, User userSession) throws Exception {

		// if desactivate and have user error message
		if (entity.isState() && !validateIfDeactivate(entity))
			throw new SystemException("invoice_associate_user");

		changeStateCommom(entity, userSession);

		return entity;
	}

	/**
	 * Register in log system bad credentials, after SessionBean.total_try
	 * deactive the user
	 * 
	 * @param userName
	 * @throws Exception
	 */
	@Override
	public void registerLoginError(String userName, String ip, String cName)
			throws Exception {

		// Integer totalTry = SessionBean.getIntentosFallidos(cName);
		//
		// LogSystem log = new LogSystem();
		// log.setClassName("User");
		// log.setDetail("Name: " + userName + ". Total failed login: " +
		// totalTry);
		// log.setMethod("registerLoginError");
		// log.setIp(ip);
		//
		// logSystemDao.makePersistent(log);
		//
		// if (totalTry >= SessionBean.total_try) {
		// int updated = dao.changeState(userName);
		// if (updated > 0) {
		// log = new LogSystem();
		// log.setClassName("User");
		// log.setDetail("Deactive the user " + userName + " after "
		// + totalTry + " failed login.");
		// log.setMethod("inactiveUser");
		// log.setIp(ip);
		//
		// logSystemDao.makePersistent(log);
		// }
		// }

	}

	/**
	 * Update the Super admin password
	 * 
	 * @throws SystemException
	 */
	@Override
	@PreAuthorize("hasAnyRole('SUPER')")
	public void updatePassword(User user, String oldPassword)
			throws SystemException {

		// validate the old password
		if (!dao.validateSuperAdmin(user, oldPassword))
			throw new SystemException("old_password_incorrect");

		dao.updatePassword(user);

		LogSystem log = new LogSystem();
		log.setClassName(user.getClass().getSimpleName());
		log.setDetail(user.toString());
		log.setMethod("updatePassword");
		log.setClassId(user.getId().toString());
		log.setUser(new User(user.getId()));
		log.setIp(user.getIp());

		logSystemDao.makePersistent(log);

	}

}
