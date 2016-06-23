package com.ghw.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

//@Component
public class StartupListener implements
		ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private SyncTask syncTask;

	@Autowired
	private TaskExecutor taskExecutor;

	public void onApplicationEvent(final ContextRefreshedEvent event) {

//		taskExecutor.execute(new Runnable() {
//			public void run() {
//
//				syncTask.executeAllJobs();
//			}
//		});
	}

}
