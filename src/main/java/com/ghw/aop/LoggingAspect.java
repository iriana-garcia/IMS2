package com.ghw.aop;

import java.io.Serializable;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ghw.controller.SessionBean;
import com.ghw.model.IEntity;
import com.ghw.model.IEntityEditable;
import com.ghw.model.LogSystem;
import com.ghw.model.User;
import com.ghw.service.LogSystemService;
import com.ghw.util.SystemException;

/**
 * Insert in DB the user actions and the system exception
 * 
 * @author ifernandez
 * 
 */
@Aspect
public class LoggingAspect implements Serializable {

	@Autowired
	private LogSystemService loggingService;

	@Autowired
	private SessionBean sessionBean;

	/**
	 * Insert in log system the method with annotation @Loggeable
	 * 
	 * @param joinpoint
	 * @param object
	 */
	@AfterReturning(pointcut = "@annotation(com.ghw.aop.Loggable) && !allMethodLogging()", returning = "object")
	public void logging(JoinPoint joinPoint, Object object) {

		try {

			LogSystem log = new LogSystem();
			log.setClassName(object.getClass().getSimpleName());
			log.setDetail(object.toString()
					+ (object instanceof IEntityEditable ? ((IEntityEditable) object)
							.getFieldAdicional() : ""));
			log.setMethod(joinPoint.getSignature().getName());
			User user = sessionBean.getUser();
			log.setUser(user);
			log.setIp(user != null ? user.getIp() : null);

			if (object instanceof IEntity) {
				log.setClassId(((IEntity) object).getIdentity());
			}

			loggingService.makePersistent(log);

		} catch (Exception e) {
			System.out.println("Exception in logging: " + e.getMessage());
		}

	}

	/**
	 * Insert in log system all the exception BUT the SystemException type
	 * 
	 * @param joinPoint
	 * @param ex
	 */
	@AfterThrowing(pointcut = "allPublicMethodService()", throwing = "ex")
	public void loggingException(JoinPoint joinPoint, Exception ex) {

		try {

			if (!(ex instanceof SystemException)
					&& !(ex instanceof UsernameNotFoundException)) {
				LogSystem log = new LogSystem();
				log.setClassName(joinPoint.getTarget().getClass()
						.getSimpleName());
				String messa = ex.getClass().getSimpleName() + " Message: "
						+ ex.getMessage();
				log.setDetail(messa.length() > 1000 ? messa.substring(0, 999)
						: messa);
				log.setMethod(joinPoint.getSignature().getName());
				User user = sessionBean.getUser();
				log.setUser(user);
				log.setIp(user != null ? user.getIp() : null);
				log.setError(true);

				loggingService.makePersistent(log);
			}

		} catch (Exception e) {
			System.out.println("Exception in log: " + e.getMessage());
		}

	}

	/**
	 * Before updating an entity assigns the user session and the actual date
	 * 
	 * @param joinpoint
	 */
	@Before("@annotation(com.ghw.aop.Loggable) && (allPublicMethodUpdateService() || allPublicMethodMergeService())")
	public void beforeUpdate(JoinPoint joinPoint) {

		try {

			Object[] args = joinPoint.getArgs();

			if (args != null && args[0] != null && args[0] instanceof IEntity) {
				IEntityEditable object = (IEntityEditable) args[0];
				object.setUpdateDate(new Date());
				object.setUserUpdated(sessionBean.getUser());
			}

		} catch (Exception e) {
			System.out.println("Exception in log: " + e.getMessage());
		}

	}

	/**
	 * Before insert an entity clean the space from name
	 * 
	 * @param joinpoint
	 */
	@Before("@annotation(com.ghw.aop.Loggable) && allPublicMethodAddService()")
	public void beforeInsert(JoinPoint joinPoint) {

		try {
			Object[] args = joinPoint.getArgs();
			if (args != null && args[0] != null && args[0] instanceof IEntity) {
				IEntityEditable object = (IEntityEditable) args[0];
				object.setCreatedDate(new Date());
				object.setUserCreated(sessionBean.getUser());

			}

		} catch (Exception e) {
			System.out.println("Exception in log: " + e.getMessage());
		}

	}

	/**
	 * Insert in log system the method with annotation @Loggeable in
	 * InvoiceHoursAddedService
	 * 
	 * @param joinpoint
	 * @param object
	 */
	@AfterReturning(pointcut = "execution(public * com.ghw.service.impl.InvoiceHoursAddedServiceImpl.saverOrUpdate(..))", returning = "object")
	public void loggingAddHoursInvoice(JoinPoint joinPoint, Object object) {

		try {

			LogSystem log = new LogSystem();
			log.setClassName("InvoiceHoursAdded");
			log.setDetail((String) object);
			log.setMethod(joinPoint.getSignature().getName());
			User user = sessionBean.getUser();
			log.setUser(user);
			log.setIp(user != null ? user.getIp() : null);

			loggingService.makePersistent(log);

		} catch (Exception e) {
			System.out.println("Exception in logging: " + e.getMessage());
		}

	}

	/**
	 * Insert in log system the method with annotation @Loggeable in
	 * InvoiceHoursAddedService
	 * 
	 * @param joinpoint
	 * @param object
	 */
	// @AfterReturning(pointcut =
	// "execution(public * com.ghw.service.impl.OracleServiceImpl.create*(..))",
	// returning = "object")
	// public void loggingOracle(JoinPoint joinPoint, Object object) {
	//
	// try {
	//
	// LogSystem log = new LogSystem();
	// log.setClassName("Finance");
	// log.setDetail((String) object);
	// log.setMethod(joinPoint.getSignature().getName());
	// User user = sessionBean.getUser();
	// log.setUser(user);
	// log.setIp(user != null ? user.getIp() : null);
	//
	// loggingService.makePersistent(log);
	//
	// } catch (Exception e) {
	// System.out.println("Exception in logging: " + e.getMessage());
	// }
	//
	// }

	@Pointcut("execution(public * com.ghw.service.impl.*.*(..))")
	private void allPublicMethodService() {
	}

	@Pointcut("execution(public * com.ghw.service.impl.*.get*(..))")
	private void allPublicMethodGetService() {
	}

	@Pointcut("within(com.ghw.service.impl.LogSystemServiceImpl)")
	private void allMethodLogging() {
	}

	@Pointcut("execution(public * com.ghw.service.impl.*.update*(..))")
	private void allPublicMethodUpdateService() {
	}

	@Pointcut("execution(public * com.ghw.service.impl.*.merge*(..))")
	private void allPublicMethodMergeService() {
	}

	@Pointcut("execution(public * com.ghw.service.impl.*.makePersistent*(..))")
	private void allPublicMethodAddService() {
	}

}
