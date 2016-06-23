package com.ghw.service;

import java.util.List;

import com.ghw.model.Schedule;

public interface ScheduleService extends Service<Schedule, String> {

	public void makePersistent(List<Schedule> schedules);
}