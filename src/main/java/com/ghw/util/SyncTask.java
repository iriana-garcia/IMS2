package com.ghw.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ghw.model.LogSystem;
import com.ghw.report.service.OracleService;
import com.ghw.service.GDWConnectService;
import com.ghw.service.InvoiceService;
import com.ghw.service.InvoiceWorkService;
import com.ghw.service.LogSystemService;
import com.ghw.service.ProfileService;
import com.ghw.service.ServerExistingService;
import com.ghw.service.UserService;

@Component
public class SyncTask {

	@Autowired
	private UserService userService;

	@Autowired
	private LogSystemService loggingService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private OracleService oracleService;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ServerExistingService serverExistingService;

	private Calendar actual = new GregorianCalendar();

	@Autowired
	private GDWConnectService gdwConnectService;

	@Autowired
	private InvoiceWorkService invoiceWorkService;

	@PostConstruct
	public void onStartup() {

		// invoiceService.makePersistent();

		// cancel all the invoices
		// invoiceService.cancelInvoice(new Date());

		// gdwConnectService.processSkillsFromGDW();
//		Calendar calendar = new GregorianCalendar();
//		calendar.set(Calendar.DAY_OF_MONTH, 3);
//		calendar.set(Calendar.MONTH, 3);
//		calendar.set(Calendar.YEAR, 2016);
//
//		// process the invoice work
//		invoiceWorkService.createInvoiceWork(calendar.getTime());

		// invoiceService.makePersistent();

		//

		// gdwConnectService.processDataFromGDW();
		// gdwConnectService.processScheduleOnlyTest();

//		try {
			// userService.syncronizedUser();
			//
			// invoiceService.makePersistent();

			// profileService.resetTotalSumbit();
			//
			// // submit all the internation invoices
			// invoiceService.submitInternationalInvoices();

		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public void executeAllJobs() {

		syncAd();
		syncCreateInvoices();
		syncResetTotal();
		syncSentInvoices();
		syncSentSupplier();
		syncGHW();

	}

	@Scheduled(cron = "0 1 0 * * *")
	public void syncAd() {

		try {

			if (serverExistingService.validateIfExecute())
				userService.syncronizedUser();

		} catch (Exception e) {
			try {

				String errorMessage = Context.getUIMsg(e.getMessage());
				// insert logg system like an error
				LogSystem log = new LogSystem();
				log.setClassName("SyncTask");
				log.setDetail("AD sync. Error: "
						+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
								: errorMessage));
				log.setMethod("syncAd");
				log.setUser(null);
				log.setError(true);

				loggingService.makePersistent(log);
			} catch (Exception e2) {

			}
		}

	}

	@Scheduled(cron = "0 0 22 * * SAT")
	public void syncCreateInvoices() {
		try {

			// only execute on saturday
			if (serverExistingService.validateIfExecute()
					&& actual.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				invoiceService.makePersistent();

		} catch (Exception e) {
			try {

				String errorMessage = Context.getUIMsg(e.getMessage());
				// insert logg system like an error
				LogSystem log = new LogSystem();
				log.setClassName("SyncTask");
				log.setDetail("Create Invoices task. Error: "
						+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
								: errorMessage));
				log.setMethod("syncCreateInvoices");
				log.setUser(null);
				log.setError(true);

				loggingService.makePersistent(log);
			} catch (Exception e2) {

			}
		}

	}

	@Scheduled(cron = "0 0 6 * * MON")
	public void syncSentSupplier() {
		try {
			// only execute on saturday
			if (serverExistingService.validateIfExecute()
					&& actual.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
				oracleService.createSupplierFiles(null);

		} catch (Exception e) {
			try {

				String errorMessage = Context.getUIMsg(e.getMessage());
				// insert logg system like an error
				LogSystem log = new LogSystem();
				log.setClassName("SyncTask");
				log.setDetail("Sent Supplier to Finance task. Error: "
						+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
								: errorMessage));
				log.setMethod("syncSentSupplier");
				log.setUser(null);
				log.setError(true);

				loggingService.makePersistent(log);
			} catch (Exception e2) {

			}
		}

	}

	@Scheduled(cron = "0 0 17 * * TUE")
	public void syncSentInvoices() {
		try {
			// send Invoice and ACH to Finance Tuesday 5:00 pm
			// only execute on saturday
			if (serverExistingService.validateIfExecute()
					&& actual.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
				oracleService.createPayables(null);

		} catch (Exception e) {
			try {

				String errorMessage = Context.getUIMsg(e.getMessage());
				// insert logg system like an error
				LogSystem log = new LogSystem();
				log.setClassName("SyncTask");
				log.setDetail("Sent Invoices to Finance task. Error: "
						+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
								: errorMessage));
				log.setMethod("syncSentInvoices");
				log.setUser(null);
				log.setError(true);

				loggingService.makePersistent(log);
			} catch (Exception e2) {

			}
		}

	}

	@Scheduled(cron = "0 1 0 * * SUN")
	public void syncResetTotal() {

		try {

			// only execute on saturday
			if (serverExistingService.validateIfExecute()
					&& actual.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				profileService.resetTotalSumbit();

		} catch (Exception e) {
			try {

				String errorMessage = Context.getUIMsg(e.getMessage());
				// insert logg system like an error
				LogSystem log = new LogSystem();
				log.setClassName("SyncTask");
				log.setDetail("Reset Total Submit. Error: "
						+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
								: errorMessage));
				log.setMethod("syncResetTotal");
				log.setUser(null);
				log.setError(true);

				loggingService.makePersistent(log);
			} catch (Exception e2) {

			}
		}

	}

	@Scheduled(cron = "0 0 0/1 * * ?")
	public void syncEveryHourProblem() {
		try {

			// Every hours analize if exists any problem with the sync and
			// execute the process
			if (serverExistingService.validateIfExecute())
				gdwConnectService.processDataFromGDW();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			try {

				LogSystem log = new LogSystem();
				log.setClassName("SyncTask");
				log.setDetail("Error: " + e.getMessage());
				log.setMethod("syncEveryHourProblem");
				log.setError(true);

				loggingService.makePersistent(log);
			} catch (Exception e2) {

			}
		}
	}

	@Scheduled(cron = "0 0 4 * * *")
	public void syncGHW() {

		try {

			if (serverExistingService.validateIfExecute()) {

				gdwConnectService.processDataFromGDW();

				// if is sunday
				if (new GregorianCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					// cancel all the invoices
					invoiceService.cancelInvoice(new Date());
					// submit all the internation invoices
					invoiceService.submitInternationalInvoices();
				}
			}

		} catch (Exception e) {
			try {

				String errorMessage = Context.getUIMsg(e.getMessage());
				// insert logg system like an error
				LogSystem log = new LogSystem();
				log.setClassName("SyncTask");
				log.setDetail("Error: "
						+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
								: errorMessage));
				log.setMethod("syncGHW");
				log.setUser(null);
				log.setError(true);
				log.setIp(IpServer.ipServer());

				loggingService.makePersistent(log);
			} catch (Exception e2) {

			}
		}

	}

	@Scheduled(cron = "0 0 0/2 * * ?")
	public void syncGHWSkill() {

		try {

			if (serverExistingService.validateIfExecute())
				gdwConnectService.processSkillsFromGDW();

		} catch (Exception e) {
			try {

				String errorMessage = Context.getUIMsg(e.getMessage());
				// insert logg system like an error
				LogSystem log = new LogSystem();
				log.setClassName("SyncTask");
				log.setDetail("Error: "
						+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
								: errorMessage));
				log.setMethod("syncGHWSkill");
				log.setUser(null);
				log.setError(true);
				log.setIp(IpServer.ipServer());

				loggingService.makePersistent(log);
			} catch (Exception e2) {

			}
		}

	}
}
