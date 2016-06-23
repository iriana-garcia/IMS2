package com.ghw.service.impl;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.controller.ApplicationBean;
import com.ghw.controller.SessionBean;
import com.ghw.dao.IboTemporalDAO;
import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.IboTemporal;
import com.ghw.model.UserUtil;
import com.ghw.service.IboTemporalService;
import com.ghw.util.ActiveDirectory;
import com.ghw.util.SystemException;

@Service("iboTemporalService")
@Transactional(readOnly = false)
public class IboTemporalServiceImpl extends
		ServiceImpl<IboTemporal, String, IboTemporalDAO> implements
		IboTemporalService {
	private IboTemporalDAO dao;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	public void setDao(IboTemporalDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Autowired
	private ApplicationBean applicationBean;

	private ActiveDirectory ad = new ActiveDirectory();

	/**
	 * Before insert in system validate if exists yet in AD in state active
	 * 
	 * @param userUtil
	 * @return
	 * @throws NamingException
	 */
	@Override
	public boolean validateIfExistsAd(UserUtil userUtil) throws Exception {
		boolean exists = true;

		if (!userUtil.isState())
			throw new SystemException("user_inactive");

		ConfigurationGeneral conf = applicationBean.getConfiguration();

		if (conf == null)
			throw new SystemException("ad_configuration_necesary");

		ad.openConnection(conf);

		NamingEnumeration<SearchResult> result = ad.searchAccountName(
				userUtil.getUserName(), conf.getLdapDn());

		ad.closeLdapConnection();

		if (result == null || !result.hasMore())
			throw new SystemException("user_not_exists_ad");

		// validate if is in state active
		SearchResult rs = (SearchResult) result.nextElement();
		Attributes attrs = rs.getAttributes();
		Attribute bitsAttribute = attrs.get("userAccountControl");

		if (bitsAttribute != null) {
			long lng = Long.parseLong(bitsAttribute.get(0).toString());
			long secondBit = lng & 2; // get bit 2
			if (secondBit != 0)
				throw new SystemException("user_inactive_ad");

		}

		return exists;
	}

	/**
	 * Change the state and save the date and the user that make the action
	 */
	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','BOARD_C')")
	public IboTemporal changeState(IboTemporal entity) throws Exception {

		dao.changeState(entity, sessionBean.getUser());

		entity.setState(!entity.isState());
		entity.setFieldAdicional(" Old state: "
				+ (entity.isState() ? "Inactive" : "Active"));

		return entity;
	}
}