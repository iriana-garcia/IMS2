package com.ghw.dao;

import java.util.List;
import java.util.Map;

import com.ghw.model.Groups;
import com.ghw.model.User;
import com.ghw.model.UserUtil;

public interface UserDAO extends GenericDAO<User, String> {

	public User getUser(String name);

	public User updateLastLogin(User user);

	public User getUserByName(String name);

	public int changeState(User user, User userModify);

	public int changeState(String userName);

	public List<String> getDataNameExceptSuper();

	public List<String> getDataName();

	public boolean validateSuperAdmin(User user, String password);

	public List<User> getDataWithoutGroup(Groups group);

	public User loadAllById(User entity);

	public boolean validateIfExistsName(String name, String id);

	public boolean validateIfExistsEmail(String email, String id);

	public Map<String, User> getUserForAd();

	public void updatePassword(User user);

	public List<User> getDataActiveOracle();

	public void updateNeedUpdatedFalse(List<User> users);

	public User getUserLogin(String name);

	public User loadAllByIdToEdit(User entity);

	public User loadAllByIdToEditVersion2(User entity);

	public void updateAD(User user, UserUtil userUtil);

}
