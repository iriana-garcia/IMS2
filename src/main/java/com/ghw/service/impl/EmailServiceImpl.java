package com.ghw.service.impl;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.controller.ApplicationBean;
import com.ghw.controller.SessionBean;
import com.ghw.dao.BankInformationDAO;
import com.ghw.dao.ClientApplicationDAO;
import com.ghw.dao.ConfigurationEmailDAO;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.ProfileDAO;
import com.ghw.model.ConfigurationEmail;
import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.IboState;
import com.ghw.model.Invoice;
import com.ghw.model.LogSystem;
import com.ghw.model.Profile;
import com.ghw.model.TypeContract;
import com.ghw.model.User;
import com.ghw.service.EmailService;
import com.ghw.service.InvoiceService;
import com.ghw.util.Context;

/**
 * Util class to send email
 * 
 * @author ifernandez
 * 
 */
@Service("emailService")
@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
public class EmailServiceImpl implements EmailService, Serializable {

	@Autowired
	private ApplicationBean applicationBean;

	@Autowired
	private ConfigurationEmailDAO configurationEmailDAO;

	@Autowired
	private BankInformationDAO bankInformationDAO;

	@Autowired
	private ClientApplicationDAO clientApplicationDAO;

	@Autowired
	private LogSystemDAO logSystemDAO;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	private MailSender mailSender;

	@Autowired
	private ProfileDAO profileDAO;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private TaskExecutor taskExecutor;

	public EmailServiceImpl() {

	}

	public ConfigurationGeneral getConf() {
		return applicationBean.getConfiguration();
	}

	private void createEmail(String from, String[] to, String[] bcc,
			String subject, String body, boolean html, List<Path> path,
			boolean quickly) throws Exception {

		// validate the null values in the configuration
		JavaMailSenderImpl a = getProperties((JavaMailSenderImpl) mailSender,
				quickly);

		MimeMessage mimeMessage = a.createMimeMessage();

		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);

		// add the predefined text to the email, specifying if is or not
		// HTML
		message.setText(body, html);
		if (bcc != null) {
			message.setBcc(bcc);
		}

		// process all the files to adjunt
		if (path != null && path.size() > 0) {

			for (Path p : path) {
				FileSystemResource file = new FileSystemResource(p.toFile());
				message.addAttachment(file.getFilename(), file);
			}

		}

		a.send(mimeMessage);

	}

	/**
	 * Create and send an email
	 * 
	 * @param conEmail
	 *            Configuration Email data
	 * @param profile
	 *            IBO involved, can be null
	 * @param invoice
	 *            Invoice involved, can be null
	 * @throws Exception
	 */
	private void createEmail(ConfigurationEmail conEmail, Profile profile,
			Invoice invoice, List<Path> path, boolean quickly) throws Exception {

		// sustitute all parameter in the email content
		String msgBody = StringUtils.isNotBlank(conEmail.getContent()) ? processContent(
				conEmail.getContent(), profile, invoice) : "";

		// String msgBody = "";

		// add Bcc if has predefined BCC email
		String[] bcc = null;
		if (StringUtils.isNotBlank(conEmail.getBcc())) {
			List<String> bccs = new ArrayList<String>();
			String[] listto = conEmail.getBcc().split(";");
			for (String s : listto) {
				if (StringUtils.isBlank(s))
					bccs.add(s);
			}

			String[] stockArr = new String[bccs.size()];
			stockArr = bccs.toArray(stockArr);
			bcc = (String[]) bccs.toArray();
		}

		createEmail(getConf().getEmailFrom(),
				processTo(profile, conEmail.getTo()), bcc,
				conEmail.getSubject(), msgBody, true, path, quickly);

		// createEmail(getConf().getEmailFrom(),new
		// String[]{"ifernandez@greathealthworks.com"}, bcc,
		// conEmail.getSubject(), msgBody, true, path, quickly);

	}

	private String[] processTo(Profile profile, String to) {
		List<String> tos = new ArrayList<String>();

		if (profile != null) {
			if (StringUtils.isNotBlank(profile.getUser().getEmail()))
				tos.add(profile.getUser().getEmail());

			// if (StringUtils.isNotBlank(profile.getPrincEmail()))
			// tos.add(profile.getPrincEmail());
		}

		String[] listto = to.split(";");
		for (String s : listto) {
			if (StringUtils.isNotBlank(s))
				tos.add(s);
		}

		String[] stockArr = new String[tos.size()];
		stockArr = tos.toArray(stockArr);

		return stockArr;
	}

	/**
	 * create Email Properties, validate the email data
	 * 
	 * @return
	 * @throws Exception
	 */
	private JavaMailSenderImpl getProperties(JavaMailSenderImpl a,
			boolean quickly) throws Exception {

		// validate null values
		if (getConf() == null)
			throw new Exception("email_configuration_necesary");

		if (StringUtils.isBlank(getConf().getEmailFrom()))
			throw new Exception("email_from_configuration_necesary");

		if (StringUtils.isBlank(getConf().getEmailHost()))
			throw new Exception("email_host_configuration_necesary");

		if (StringUtils.isBlank(getConf().getEmailProtocol()))
			throw new Exception("email_protocol_configuration_necesary");

		if (getConf().getEmailPort() == null)
			throw new Exception("email_port_configuration_necesary");

		a.setHost(getConf().getEmailHost());
		a.setPort(getConf().getEmailPort());
		a.setProtocol(getConf().getEmailProtocol());

		if (StringUtils.isNotBlank(getConf().getEmailUser())) {
			a.setUsername(getConf().getEmailUser());
		}

		if (StringUtils.isNotBlank(getConf().getEmailPass())) {
			a.setPassword(getConf().getEmailPass());
		}

		Properties properties = new Properties();
		properties.put("mail." + getConf().getEmailProtocol() + ".auth",
				getConf().isEmailAuth());
		// If set to true, and a message has some valid and some invalid
		// addresses, send the message anyway, reporting the partial failure
		// with a SendFailedException. If set to false (the default), the
		// message is not sent to any of the recipients if there is an invalid
		// recipient address.
		properties.put("mail." + getConf().getEmailProtocol() + ".sendpartial",
				true);

		// If set to false, the QUIT command is sent and the connection is
		// immediately closed. If set to true (the default), causes the
		// transport to wait for the response to the QUIT command.
		properties.put("mail.smtp.quitwait", false);

		a.setJavaMailProperties(properties);

		return a;
	}

	/**
	 * Necesary for sustitute all parameter in the email content
	 * 
	 * @param content
	 * @return
	 */
	private String processContent(String content, Profile profile,
			Invoice invoice) {

		content = content.replaceAll("@@userid",
				profile != null ? profile.getNumber() : "");

		content = content.replaceAll("@@username",
				profile != null && profile.getUser() != null
						&& profile.getUser().getName() != null ? profile
						.getUser().getName() : "");
		content = content.replaceAll("@@firstname",
				profile != null && profile.getUser() != null
						&& profile.getUser().getFirstName() != null ? profile
						.getUser().getFirstName() : "");
		content = content.replaceAll("@@middlename",
				profile != null && profile.getUser() != null
						&& profile.getUser().getMiddleName() != null ? profile
						.getUser().getMiddleName() : "");
		content = content.replaceAll("@@lastname",
				profile != null && profile.getUser() != null
						&& profile.getUser().getLastName() != null ? profile
						.getUser().getLastName() : "");

		content = content
				.replaceAll(
						"@@companyname",
						profile != null && profile.getCorporationName() != null ? profile
								.getCorporationName() : "");

		content = content
				.replaceAll("@@linksystem", getConf().getEmailSystem());

		content = content.replaceAll("@@paydateinvoice", invoice != null
				&& invoice.getPayDate() != null ? (new SimpleDateFormat(
				"E, M-d-yy")).format(invoice.getPayDate()) : "");

		content = content
				.replaceAll(
						"@@invoiceImporttotal",
						invoice != null && invoice.getImportTotal() != null ? new Double(
								MathUtils.round(invoice.getImportTotal(), 2))
								.toString() : "");

		content = content.replaceAll("@@invoicenumber", invoice != null
				&& invoice.getNumber() != null ? invoice.getNumber() : "");

		return content;
	}

	/**
	 * send a welcome email to the IBO, return true if not occurs any exception
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	public void sendWelcomeEmail(final Profile profile) {

		taskExecutor.execute(new Runnable() {
			public void run() {

				String state = IboState.EMAIL_SENT;
				String errorMessage = null;

				try {
					// search the predefined data from Welcome email
					ConfigurationEmail configurationEmail = configurationEmailDAO
							.getDataByType("W");
					// send the email to the IBO
					createEmail(configurationEmail, profile, null, null, true);
				} catch (Exception e) {

					state = IboState.EMAIL_ERROR;
					// if an exception occurs insert into log system

					errorMessage = Context.getUIMsg(e.getMessage());
					errorMessage = StringUtils.isBlank(errorMessage) ? e
							.getMessage() : errorMessage;

				} finally {
					// update the IBO state
					// si the ibo has state OK dont update the IBO
					// if the IBO has state Email Sent neither
					if (!profile.getIboState().getId()
							.equals(IboState.EMAIL_SENT)
							&& !profile.getIboState().getId()
									.equals(IboState.OK)) {

						// if the IBO has bank account information and CA the
						// status is OK
						// if has CA and the status is NOT 4, search y has bank
						// account
						// information
						// in this case change status to OK
						// List<ClientApplication> listCA = clientApplicationDAO
						// .getDataByIbo(profile);
						// if (listCA != null && listCA.size() > 0
						// && bankInformationDAO.getExistByIbo(profile)) {
						// state = IboState.OK;
						// // create invoice for this week
						// invoiceService.createInvoiceIbo(profile);
						// }

						profileDAO.update(profile, state);
					}

					// insert the email send
					LogSystem log = new LogSystem();
					log.setClassName("Email");

					if (state.equals(IboState.EMAIL_ERROR)
							|| StringUtils.isNotBlank(errorMessage)) {
						log.setDetail(profile.toString()
								+ " Error send Welcome email: " + errorMessage);
					} else {
						log.setDetail(profile.toString());
					}
					log.setMethod("sendWelcomeEmail");
					log.setUser(null);
					log.setClassId(profile.getId());

					logSystemDAO.makePersistent(log);

				}
			}
		});
	}

	/**
	 * send a welcome email to the IBO, return true if not occurs any exception
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	public void sendFinancialEmail(final List<Path> files) {

		taskExecutor.execute(new Runnable() {
			public void run() {
				try {

					// search the predefined data from Welcome email
					ConfigurationEmail configurationEmail = configurationEmailDAO
							.getDataByType("F");

					Integer maxFile = applicationBean.getConfiguration()
							.getEmailMaxSize();
					if (maxFile != null && maxFile > 0) {
						// convert max file MB to bytes
						maxFile = maxFile * 1000000;
						// analizy file's size
						long size = 0;
						List<Path> processPath = new ArrayList<Path>();
						for (Path p : files) {

							long sizeFile = Files.size(p);

							if ((size + sizeFile) >= maxFile) {
								// send the email to the IBO
								createEmail(configurationEmail, null, null,
										processPath, true);
								size = sizeFile;
								processPath = new ArrayList<Path>();
							} else {
								size += sizeFile;
							}
							processPath.add(p);

						}
						// send the email to the IBO
						createEmail(configurationEmail, null, null,
								processPath, true);
					} else
						createEmail(configurationEmail, null, null, files, true);

				} catch (Exception e) {
					// if an exception occurs insert into log system
					try {
						String errorMessage = Context.getUIMsg(e.getMessage());
						errorMessage = StringUtils.isBlank(errorMessage) ? e
								.getMessage() : errorMessage;

						// insert logg system like an error
						LogSystem log = new LogSystem();
						log.setClassName("Email");
						log.setDetail("Error send Financial email: "
								+ errorMessage);
						log.setMethod("sendFinancialEmail");
						User user = sessionBean.getUser();
						log.setUser(user);
						log.setError(true);
						log.setIp(user.getIp());

						logSystemDAO.makePersistent(log);
					} catch (Exception e2) {
						System.out.println(e2.getMessage());
					}

				}
			}
		});
	}

	/**
	 * send a welcome email to the IBO, return true if not occurs any exception
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	@Override
	public void sendInvoiceEmail(final Invoice invoice) {

		taskExecutor.execute(new Runnable() {
			public void run() {
				try {

					// search the predefined data from Welcome email
					ConfigurationEmail configurationEmail = configurationEmailDAO
							.getDataByType("I");

					// Search profile email to send an email
					Profile profile = profileDAO.getEmailIbo(invoice.getUser()
							.getId());

					// Profile profile = invoice.getUser();
					// profile.setUser(new User());
					// profile.getUser().setEmail(email);

					if (!profile.getTypeContract().equals(
							TypeContract.INTERNATIONAL)) {
						// send the email to the IBO
						createEmail(configurationEmail, profile, invoice, null,
								false);
					}

				} catch (Exception e) {
					// if an exception occurs insert into log system
					try {
						String errorMessage = Context.getUIMsg(e.getMessage());
						errorMessage = StringUtils.isBlank(errorMessage) ? e
								.getMessage() : errorMessage;
						// insert logg system like an error
						LogSystem log = new LogSystem();
						log.setClassName("Email");
						log.setDetail("Error send Invoice email: "
								+ errorMessage);
						log.setMethod("sendFinancialEmail");
						log.setUser(null);
						log.setError(true);

						logSystemDAO.makePersistent(log);
					} catch (Exception e2) {
						System.out.println(e2.getMessage());
					}

				}
			}
		});
	}

	/**
	 * Send a question to IBOC from IBO
	 * 
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public void sendQuestion(String from, String to, String bcc,
			String subject, String body) throws Exception {

		createEmail(from, to.split(";"),
				StringUtils.isNotBlank(bcc) ? bcc.split(";") : null, subject,
				body, true, null, false);

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
	@Override
	public void sendInvoiceEmail(final List<Invoice> listInvoices) {

		taskExecutor.execute(new Runnable() {
			public void run() {
				try {
					// search the predefined data from Welcome email
					ConfigurationEmail configurationEmail = configurationEmailDAO
							.getDataByType("I");

					System.out.println("Total Invoices: " + listInvoices.size());

					for (Invoice invoice : listInvoices) {

						System.out.println("Envia email: " + invoice.toString());
						try {

							// Search profile email to send an email
							Profile profile = profileDAO.getEmailIbo(invoice
									.getUser().getId());

							// Profile profile = invoice.getUser();
							// profile.setUser(new User());
							// profile.getUser().setEmail(email);

							if (!profile.getTypeContract().equals(
									TypeContract.INTERNATIONAL)) {
								// send the email to the IBO
								createEmail(configurationEmail, profile,
										invoice, null, false);
							}
							
						} catch (Exception e) {
							System.out.println("Entro al primer error");
							String errorMessage = Context.getUIMsg(e
									.getMessage());
							errorMessage = StringUtils.isBlank(errorMessage) ? e
									.getMessage() : errorMessage;
							// insert logg system like an error
							LogSystem log = new LogSystem();
							log.setClassName("Email");
							log.setDetail("Error send Invoice email: "
									+ errorMessage);
							log.setMethod("sendFinancialEmail");
							log.setUser(null);
							log.setError(true);

							logSystemDAO.makePersistent(log);
						}
					}

				} catch (Exception e) {
					// if an exception occurs insert into log system
					try {
						System.out.println("Entro al segundo error");
						String errorMessage = Context.getUIMsg(e.getMessage());
						errorMessage = StringUtils.isBlank(errorMessage) ? e
								.getMessage() : errorMessage;
						// insert logg system like an error
						LogSystem log = new LogSystem();
						log.setClassName("Email");
						log.setDetail("Error send Invoice email: "
								+ errorMessage);
						log.setMethod("sendFinancialEmail");
						log.setUser(null);
						log.setError(true);

						logSystemDAO.makePersistent(log);
					} catch (Exception e2) {
						System.out.println(e2.getMessage());
					}

				}
			}
		});

	}

	public boolean confirmSMTP(ConfigurationGeneral general) throws Exception {
		boolean result = false;
		try {

			JavaMailSenderImpl a = (JavaMailSenderImpl) mailSender;

			a.setHost(general.getEmailHost());
			a.setPort(general.getEmailPort());
			a.setProtocol(general.getEmailProtocol());

			// if (StringUtils.isNotBlank(general.getEmailUser()))
			a.setUsername(general.getEmailUser());

			// if (StringUtils.isNotBlank(general.getEmailPass()))
			a.setPassword(general.getEmailPass());

			Properties properties = new Properties();
			properties.put("mail." + general.getEmailProtocol() + ".auth",
					general.isEmailAuth());
			properties.put("mail." + general.getEmailProtocol()
					+ ".sendpartial", true);

			a.setJavaMailProperties(properties);

			a.testConnection();

			result = true;

		} catch (AuthenticationFailedException e) {
			throw e;

		} catch (MessagingException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return result;
	}
}