package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.constant.Reports;
import com.ghw.dao.EventDAO;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.UpdatedDateDAO;
import com.ghw.model.ClientApplication;
import com.ghw.model.Event;
import com.ghw.model.LogSystem;
import com.ghw.model.UpdatedDate;
import com.ghw.service.EventService;
import com.ghw.util.IpServer;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;

@Service("eventService")
@Transactional(readOnly = false)
public class EventServiceImpl extends ServiceImpl<Event, String, EventDAO>
		implements EventService {
	private EventDAO dao;

	@Autowired
	private LogSystemDAO logSystemDAO;

	@Autowired
	private UpdatedDateDAO updatedDateDAO;

	@Autowired
	public void setDAO(EventDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Override
	public List<Event> getDataWithoutCA() {
		return dao.getDataWithoutCA();
	}

	@Override
	public List<Event> getDataWithoutCA(ClientApplication ca) {
		return dao.getDataWithoutCA(ca);
	}

	@Override
	@Cacheable(cacheName = "eventCache")
	public List<Event> getDataOrder() {
		return super.getDataOrder();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	@TriggersRemove(cacheName = "eventCache", when = When.AFTER_METHOD_INVOCATION, removeAll = true)
	public String saverOrUpdate(List<Event> events) {

		LogSystem log = new LogSystem();
		log.setMethod("saverOrUpdate");
		log.setClassName("Event");

		boolean error = false;
		String detail = "";
		try {

			if (events != null && events.size() > 0) {

				// get all the event to make the comparision
				List<Event> listEventSystem = dao.findAll();

				// inserted if not exists
				// I need to compare with the id in pipkins
				List<Event> newList = new ArrayList<Event>();
				newList.addAll(events);
				newList.removeAll(listEventSystem);

				for (Event event : newList) {
					dao.makePersistent(event);
				}

				int totalUpdated = 0;

				// update the event if exists in system
				for (Event system : listEventSystem) {
					for (Event event : events) {
						if (event.equals(system)) {
							system.setName(event.getName());
							dao.merge(system);
							break;
						}
					}
				}

				detail = "Total inserted: " + newList.size()
						+ " Total updated: " + totalUpdated;

			}

			UpdatedDate updatedDate = updatedDateDAO.getById(Reports.EVENTS);
			updatedDate.setDateUpdated(new Date());
			updatedDateDAO.update(updatedDate);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			error = true;
			detail += e.getMessage();
			throw e;
		} finally {

			log.setError(error);
			log.setDetail(detail);
			log.setIp(IpServer.ipServer());
			logSystemDAO.makePersistent(log);

		}

		return detail;
	}
}