package com.ghw.service;

import java.util.List;

import javax.naming.NamingException;

import com.ghw.model.Address;
import com.ghw.model.ClientApplication;
import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.Corporation;
import com.ghw.model.Country;
import com.ghw.model.Groups;
import com.ghw.model.IboType;
import com.ghw.model.Profile;
import com.ghw.model.Rol;
import com.ghw.model.States;
import com.ghw.model.User;
import com.ghw.model.UserType;
import com.ghw.model.UserUtil;
import com.ghw.util.SystemException;

public interface UserService extends Service<User, String> {

	public User getUser(String name, String password,
			ConfigurationGeneral configuration) throws SystemException,
			NamingException;

	public User updateLastLogin(User user) throws Exception;

	public void syncronizedUser() throws Exception;

	public List<User> getDataWithoutGroup(Groups group);

	public List<UserUtil> getDataActiveAd(ConfigurationGeneral configuration)
			throws Exception;

	public User makePersistent(User entity, Rol rol, Groups group,
			Profile profile, IboType type, Address address, States state,
			Country country, List<ClientApplication> cas, Groups groupIbo,
			UserType typeUser, Corporation corporation,
			Integer actionsCorporation, Country countryIbo, User userSession)
			throws Exception;

	public User update(User entity, Rol rol, Groups group, Profile profile,
			IboType type, Address address, States state, Country country,
			List<ClientApplication> cas, Groups groupIbo, UserType typeUser,
			Corporation corporation, Integer actionsCorporation,
			Country countryIbo, Profile newPrincipalIBO, User userSession)
			throws Exception;

	public User loadAllById(User entity) throws Exception;

	public User changeState(User entity, User userSession) throws Exception;

	public boolean validateIfDeactivate(User entity);

	public void registerLoginError(String userName, String ip, String cName)
			throws Exception;

	public void updatePassword(User user, String oldPassword)
			throws SystemException;

	public User loadAllByIdToEdit(User entity) throws Exception;
	
	public User loadAllByIdToEdit2(User entity) throws Exception;
}