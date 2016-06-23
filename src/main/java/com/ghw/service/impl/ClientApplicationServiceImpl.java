package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.aop.Loggable;
import com.ghw.controller.SessionBean;
import com.ghw.dao.ClientApplicationDAO;
import com.ghw.dao.EventDAO;
import com.ghw.dao.IbosClientApplicationsDAO;
import com.ghw.dao.InvoiceWorkDAO;
import com.ghw.dao.ProfileDAO;
import com.ghw.dao.SkillDAO;
import com.ghw.filter.FilterBase;
import com.ghw.model.ClientApplication;
import com.ghw.model.Event;
import com.ghw.model.IbosClientApplications;
import com.ghw.model.Profile;
import com.ghw.model.Skill;
import com.ghw.service.ClientApplicationService;
import com.ghw.util.SystemException;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;

@Service("clientApplicationService")
@Transactional(readOnly = false)
public class ClientApplicationServiceImpl extends
		ServiceImpl<ClientApplication, String, ClientApplicationDAO> implements
		ClientApplicationService {

	private ClientApplicationDAO dao;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	private SkillDAO skillDAO;

	@Autowired
	private EventDAO eventDAO;

	@Autowired
	private InvoiceWorkDAO invoiceWorkDAO;

	@Autowired
	private ProfileDAO profileDAO;

	@Autowired
	private IbosClientApplicationsDAO ibosClientApplicationsDAO;

	@Autowired
	public void setDao(ClientApplicationDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	/**
	 * Validate if exists the CA name in system
	 */
	@Override
	public void isValidate(ClientApplication entity) throws Exception {
		if (dao.validateIfExistsName(entity.getName(), entity.getId())) {
			throw new SystemException("name_exists", "form:txtName");
		}
	}

	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA_M')")
	@TriggersRemove(cacheName = "clientApplicationCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public ClientApplication update(ClientApplication entity,
			List<Skill> skills, List<Event> events, List<Profile> ibos,
			Date modification) throws Exception {
		// validate if exist the name in system
		isValidate(entity);

		boolean hasInvoice = invoiceWorkDAO.validateAssociateCA(entity);

		// if desactivate and have invoice pending or submitted error message
		if (!entity.isState() && hasInvoice) {
			throw new SystemException("ca_invoice_associate");
		}

		entity.setFieldAdicional(" CAs: " + skills.toString() + " Events: "
				+ events.toString());

		// get the old object before update
		ClientApplication old = dao.getById(entity.getId());

		// assigned the lists to compare
		entity.setListSkill(skills);
		entity.setEvents(events);
		entity.setListIbo(ibos);

		List<Skill> listOld = skillDAO.getDataByCA(entity);
		List<Event> eventlistOld = eventDAO.getDataByCA(entity);
		old.setListSkill(listOld);
		old.setEvents(eventlistOld);
		old.setListIbo(profileDAO.getDataByCA(entity));

		// if has invoice and modify any field need to update the invoice with
		// the name and amount before
		int cantInvoicesModif = 0;
		if (hasInvoice
				&& modification != null
				&& (!entity.getName().equals(old.getName()) || !entity
						.getAmount().equals(old.getAmount()))) {
			cantInvoicesModif = invoiceWorkDAO.updateClientApplication(entity,
					modification, !entity.getName().equals(old.getName()),
					sessionBean.getUser());
		}

		int cantModifEvent = 0;
		if (events.size() > 0) {
			cantModifEvent = invoiceWorkDAO.updateEvents(entity, events,
					sessionBean.getUser());
		}

		// compare the data
		entity.compare(old);

		// clear the skills associate to CA
		skillDAO.clearClientApplication(listOld);
		// associate the skills to CA
		skillDAO.update(skills, entity);

		// clear the event associate to CA
		eventDAO.clearClientApplication(eventlistOld);
		// associate the event to CA
		eventDAO.update(events, entity);

		// delete all ibo associate
		ibosClientApplicationsDAO.deleteByCA(entity.getId());
		ibosClientApplicationsDAO.flush();

		List<IbosClientApplications> caInser = new ArrayList<IbosClientApplications>();
		for (Profile profile : ibos) {
			IbosClientApplications ica = new IbosClientApplications();
			ica.setClientApplication(entity);
			ica.setUser(profileDAO.loadById(profile.getId()));

			caInser.add(ica);
		}

		entity.setIbos(caInser);

		entity.setFieldAdicional(entity.getFieldAdicional()
				+ (cantInvoicesModif > 0 ? " Total invoices modified: "
						+ cantInvoicesModif : "")
				+ (cantModifEvent > 0 ? " Total invoices modified for event: "
						+ cantModifEvent : ""));

		dao.merge(entity);
		dao.flush();

		return entity;
	}

	/**
	 * Insert a client application with it skills
	 */
	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA_A')")
	@TriggersRemove(cacheName = "clientApplicationCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public ClientApplication makePersistent(ClientApplication entity,
			List<Skill> skills, List<Event> events, List<Profile> ibos)
			throws Exception {
		// validate if exist the name in system
		isValidate(entity);

		entity.setUserCreated(sessionBean.getUser());

		List<IbosClientApplications> caInser = new ArrayList<IbosClientApplications>();
		for (Profile profile : ibos) {
			IbosClientApplications ica = new IbosClientApplications();
			ica.setClientApplication(entity);
			ica.setUser(profileDAO.loadById(profile.getId()));

			caInser.add(ica);
		}

		entity.setIbos(caInser);

		dao.makePersistent(entity);
		dao.flush();

		// associate the skills to CA
		skillDAO.update(skills, entity);

		// associate the event to CA
		eventDAO.update(events, entity);

		int cantModifEvent = 0;
		if (events.size() > 0) {
			cantModifEvent = invoiceWorkDAO.updateEvents(entity, events,
					sessionBean.getUser());
		}

		// insert in field adicional to insert in logg system
		entity.setFieldAdicional(" CAs: "
				+ skills.toString()
				+ " Events: "
				+ events.toString()
				+ (cantModifEvent > 0 ? " Total invoices modified for event: "
						+ cantModifEvent : ""));

		return entity;
	}

	/**
	 * Change the CA state
	 */
	@Loggable
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA_C')")
	@TriggersRemove(cacheName = "clientApplicationCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public ClientApplication changeState(ClientApplication entity)
			throws Exception {

		// if desactivate and have invoice submitted or pending error message
		if (entity.isState() && invoiceWorkDAO.validateAssociateCA(entity)) {
			throw new SystemException("ca_invoice_associate");
		}

		// if the CA is going to deactivate celan the skill
		if (entity.isState()) {
			skillDAO.clearClientApplication(entity);
			eventDAO.clearClientApplication(entity);
			ibosClientApplicationsDAO.deleteByCA(entity.getId());
		}

		dao.changeState(entity, sessionBean.getUser());

		entity.setState(!entity.isState());
		entity.setFieldAdicional(" Old state: "
				+ (entity.isState() ? "Inactive" : "Active"));

		return entity;
	}

	/**
	 * Validate if the client application can be deactivated. If has invoice in
	 * state pending or submitted can't be deactivate
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean validateIfDeactivate(ClientApplication entity) {
		return invoiceWorkDAO.validateAssociateCA(entity);
	}

	/**
	 * Load all the clients applications data
	 */
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA')")
	@Transactional(readOnly = true)
	public List<ClientApplication> loadAllById(FilterBase filter)
			throws Exception {
		List<ClientApplication> list = dao.loadAllById(filter);
		for (ClientApplication clientApplication : list) {
			clientApplication.setListSkill(skillDAO
					.getDataByCA(clientApplication));
			clientApplication.setListIbo(profileDAO
					.getDataByCA(clientApplication));
			clientApplication
					.setEvents(eventDAO.getDataByCA(clientApplication));
		}

		return list;
	}

	/**
	 * Load all the clients applications data
	 */
	@Override
	@PreAuthorize("hasAnyRole('SUPER','CA')")
	@Transactional(readOnly = true)
	public ClientApplication loadAllById(ClientApplication ca) throws Exception {
		ClientApplication cl = dao.loadAllById(ca);
		cl.setListSkill(skillDAO.getDataByCA(ca));
		cl.setListIbo(profileDAO.getDataByCA(ca));
		cl.setEvents(eventDAO.getDataByCA(ca));

		return cl;
	}

	/**
	 * Get all the client application NOT associate to an IBO
	 */
	@Override
	public List<ClientApplication> getDataByNotIbo(Profile profile) {
		return dao.getDataByNotIbo(profile);
	}

	/**
	 * Get all the client application associate to an IBO
	 */
	@Override
	public List<ClientApplication> getDataByIbo(Profile profile) {
		return dao.getDataByIbo(profile);
	}

	@Override
	public List<ClientApplication> getDataByEditIbo(Profile profile) {
		return dao.getDataByEditIbo(profile);
	}

	@Override
	@Cacheable(cacheName = "clientApplicationCache")
	public List<ClientApplication> getDataActive() {
		return super.getDataActive();
	}
}