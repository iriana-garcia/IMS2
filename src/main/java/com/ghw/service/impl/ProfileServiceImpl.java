package com.ghw.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.dao.AddressDAO;
import com.ghw.dao.ClientApplicationDAO;
import com.ghw.dao.CorporationDAO;
import com.ghw.dao.ProfileDAO;
import com.ghw.model.ClientApplication;
import com.ghw.model.Groups;
import com.ghw.model.Profile;
import com.ghw.service.EmailService;
import com.ghw.service.LogSystemService;
import com.ghw.service.ProfileService;

@Service("profileService")
@Transactional(readOnly = false)
public class ProfileServiceImpl extends
		ServiceImpl<Profile, String, ProfileDAO> implements ProfileService {
	private ProfileDAO dao;

	@Autowired
	private EmailService emailService;

	@Autowired
	private LogSystemService loggingService;

	@Autowired
	private ClientApplicationDAO clientApplicationDAO;

	@Autowired
	private AddressDAO addressDAO;

	@Autowired
	private CorporationDAO corporationDAO;

	@Autowired
	public void setDAO(ProfileDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','USER_M','USER_A','BOARD_M','BOARD_A')")
	public Profile updateIBOToPrincipal(Profile profile) {

		dao.updateIBOToPrincipal(profile);

		return profile;
	}

	@Override
	public List<Profile> getDataWithoutGroup(Groups group) {
		return dao.getDataWithoutGroup(group);
	}

	@Override
	@PreAuthorize("hasAnyRole('SUPER','BOARD')")
	public boolean sendWelcomeEmail(String idUser) throws Exception {

		Profile profile = dao.getDataBySendEmail(idUser);

		// send welcome email
		emailService.sendWelcomeEmail(profile);

		return true;

	}

	@Override
	public Profile loadAllById(String idUser) throws Exception {
		Profile ibo = dao.loadAllById(idUser);

		if (ibo != null) {
			ibo.setListCa(clientApplicationDAO.getDataByIbo(ibo));
			if (ibo.getCorporation() != null
					&& StringUtils.isNotBlank(ibo.getCorporation().getId())) {
				ibo.getCorporation().setAddress(
						addressDAO.getDataByCorporation(ibo.getCorporation()));
			}
		}
		return ibo;
	}

	@Override
	public Profile loadAllByIdToEdit(String idUser) throws Exception {
		Profile ibo = dao.loadAllByIdToEdit(idUser);

		if (ibo != null && ibo.getCorporation() != null
				&& StringUtils.isNotBlank(ibo.getCorporation().getId())) {
			ibo.getCorporation().setAddress(
					addressDAO.getDataByCorporation(ibo.getCorporation()));
			// get the information from list profile in the corporation
			// ibo.getCorporation().setIdPrincipalOwner(
			// corporationDAO.getIdPrincipalOwnerCorporation(ibo
			// .getCorporation()));
		}
		return ibo;
	}

	@Override
	public List<Profile> getDataActiveIbo() {
		return dao.getDataActiveIbo();
	}

	@Override
	public List<Profile> getDataAllIboByCA(ClientApplication ca) {
		return dao.getDataAllIboByCA(ca);
	}

	@Override
	public String[] getEmailIboAndGroup(String iboId) {
		return dao.getEmailIboAndGroup(iboId);
	}

	@Override
	public void resetTotalSumbit() {
		dao.resetTotalSumbit();

	}

	@Override
	public List<Profile> getDataByCorporationActive(String corporationId) {
		return dao.getDataByCorporationActive(corporationId);
	}

	@Override
	public String getProfileNumberByUser(String userID) {
		return dao.getProfileNumberByUser(userID);
	}

}