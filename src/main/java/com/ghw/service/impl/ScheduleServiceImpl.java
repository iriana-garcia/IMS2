package com.ghw.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.constant.Reports;
import com.ghw.dao.EventDAO;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.ScheduleDAO;
import com.ghw.dao.UpdatedDateDAO;
import com.ghw.model.ClientApplication;
import com.ghw.model.Event;
import com.ghw.model.LogSystem;
import com.ghw.model.Schedule;
import com.ghw.model.UpdatedDate;
import com.ghw.service.ScheduleService;
import com.ghw.util.IpServer;

@Service("scheduleService")
@Transactional(readOnly = false)
public class ScheduleServiceImpl extends
		ServiceImpl<Schedule, String, ScheduleDAO> implements ScheduleService {

	private ScheduleDAO dao;

	@Autowired
	private LogSystemDAO logSystemDAO;

	@Autowired
	private EventDAO eventDAO;

	@Autowired
	private UpdatedDateDAO updatedDateDAO;

	@Autowired
	public void setDAO(ScheduleDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public void makePersistent(List<Schedule> schedules) {

		LogSystem log = new LogSystem();
		log.setMethod("saverOrUpdate");
		log.setClassName("Schedule");

		boolean error = false;
		String detail = "";
		try {

			if (schedules != null && schedules.size() > 0) {

				// search all the schedule between date
				List<Date> listDate = new ArrayList<Date>();
				for (Schedule s : schedules) {
					listDate.add(s.getDateStart());
				}
				// Sorts the specified list into ascending order
				Collections.sort(listDate);
				// get the first and the last entries
				Date dateStart = listDate.get(0);
				Date dateEnd = listDate.get(listDate.size() - 1);

				// if exists schedule for that range date skip
				Long count = dao.getCountByDate(dateStart, dateEnd);

				if (count == null || count.equals(0L)) {

					// get all the event with the program associate
					List<Event> events = eventDAO.findAll();
					Map<String, ClientApplication> cMap = new HashMap<String, ClientApplication>();
					for (Event event : events) {
						cMap.put(event.getId(), event.getClientApplication());
					}

					Integer total = 0;
					for (Schedule s : schedules) {

						//System.out.println(s.getUser().getId());
						// System.out.println(s.getId());
						s.setEvent(eventDAO.loadById(s.getEvent().getId()));

						// get the programm associate to the event
						s.setClientApplication(cMap.get(s.getEvent().getId()));

						dao.makePersistent(s);

						if (++total % 50 == 0) { // 500, same as the JDBC batch
													// size
							// flush a batch of inserts and release memory:
							dao.flush();
							dao.clear();
						}
					}

					dao.flush();

					detail = "Total inserted: " + schedules.size();

				}
			}

			UpdatedDate updatedDate = updatedDateDAO.getById(Reports.SCHEDULE);
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

	}

}