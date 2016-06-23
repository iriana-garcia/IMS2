package com.ghw.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghw.controller.ApplicationBean;
import com.ghw.dao.AddressDAO;
import com.ghw.dao.BankInformationDAO;
import com.ghw.dao.CorporationDAO;
import com.ghw.dao.InvoiceDAO;
import com.ghw.dao.LogSystemDAO;
import com.ghw.dao.ProfileDAO;
import com.ghw.dao.RoutingNumbersDAO;
import com.ghw.dao.UserDAO;
import com.ghw.model.Address;
import com.ghw.model.BankInformation;
import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.ConfigurationSystem;
import com.ghw.model.Corporation;
import com.ghw.model.Incentive;
import com.ghw.model.Invoice;
import com.ghw.model.InvoiceWork;
import com.ghw.model.LogSystem;
import com.ghw.model.RoutingNumbers;
import com.ghw.model.User;
import com.ghw.report.service.OracleService;
import com.ghw.service.EmailService;
import com.ghw.service.InvoiceService;
import com.ghw.util.FTPUtil;
import com.ghw.util.IpServer;

@Service("oracleService")
@Transactional(readOnly = false)
public class OracleServiceImpl implements OracleService {

	@Autowired
	private ProfileDAO profileDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private AddressDAO addressDAO;

	@Autowired
	private LogSystemDAO logSystemDAO;

	@Autowired
	private BankInformationDAO bankInformationDAO;

	@Autowired
	private ApplicationBean applicationBean;

	@Autowired
	private EmailService emailService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceDAO invoiceDAO;

	@Autowired
	private CorporationDAO corporationDAO;

	@Autowired
	private RoutingNumbersDAO routingNumbersDAO;

	private FTPUtil ftpUtil = new FTPUtil();

	// action to create for oracle
	private final static String CREATE = "CREATE";

	// action to update for oracle
	private final static String UPDATE = "UPDATE";

	private static final String namePayPal = "paypal";

	private static final String extPayPal = ".csv";

	// mandatory name for supplier import in oracle
	private static final String nameSupplier = "PozSuppliersInt.csv";

	// mandatory name for supplier site import in oracle
	private static final String nameSupplierSite = "PozSupplierSitesInt.csv";

	// mandatory name for supplier import in oracle
	private static final String nameSupplierSiteAssignments = "PozSiteAssignmentsInt.csv";

	// mandatory name for supplier contact import in oracle
	private static final String nameSupplierContact1 = "PozSupContactsInt.csv";

	// mandatory name for supplier contact import in oracle
	private static final String nameSupplierContact2 = "PozSupContactAddressesInt.csv";

	// mandatory name for supplier address import in oracle
	private static final String nameSupplierAddress = "PozSupplierAddressesInt.csv";

	// mandatory name for supplier bankf import in oracle
	private static final String nameSupplierBank1 = "IbyTempExtPayees.csv";

	// mandatory name for supplier bankf import in oracle
	private static final String nameSupplierBank2 = "IbyTempExtBankAccts.csv";

	// mandatory name for supplier bankf import in oracle
	private static final String nameSupplierBank3 = "IbyTempPmtInstrUses.csv";

	// mandatory name for supplier import in oracle
	private static final String nameInvoice = "ApInvoicesInterface.csv";

	// mandatory name for supplier contact import in oracle
	private static final String nameInvoiceLine = "ApInvoiceLinesInterface.csv";

	private static final String nameBank = "CE_RAPID_IMPLEMENTATION.xml";

	private static final String nameACH = "ACH";

	private static final String site = "MAIN-PURCH";

	private static final String procurement = "GHWBusinessUnit";

	DecimalFormat myFormatter = new DecimalFormat(".##");

	// private static String getDateFiles();

	private Charset charset = StandardCharsets.ISO_8859_1;

	// private String getPathDirectory();

	// // path where the document are save in the local
	// private String getPath() {
	// return Context.getRealPath() + "/documents";
	//
	// }

	private String directoryDate, pathDirectory, detail;

	// path where the document are save in the local
	private String getPathDirectory() {
		//
		// pathDirectory = Context.getRealPath() + File.separator + "documents"
		// + File.separator + getDateFile();

		pathDirectory = File.separator + "ims" + File.separator + "documents"
				+ File.separator + getDateFile();

		return pathDirectory;

	}

	@Autowired
	private ServletContext servletContext;

	public OracleServiceImpl() {

	}

	public String getDateFile() {

		directoryDate = (new SimpleDateFormat("MMddyyyyHHmmss"))
				.format(new Date());

		return directoryDate;
	}

	@Override
	public String createPayables(User user) {

		String result = "";
		List<Path> files = new ArrayList<Path>();

		try {

			/**
			 * Process PayPal, all the invoices IBO has PayType = User and
			 * PayMethod = PAYPAL, process = false
			 */
			List<Invoice> invoicesPayPal = invoiceDAO.getDataActivePayPal();

			/**
			 * Process Direct Deposit, are processed all the IBO with supplier
			 * number (means that exist in Finance Oracle), the invoice must to
			 * have state Approval, no processed (false) and the PayMethod =
			 * Direct Deposit in the corporation
			 */
			List<Invoice> invoices = invoiceService.getDataActiveOracle();

			// if at least one has data create the directory
			// the directory structure is folder name = actual date, inside
			// folder PayPal and DirectDeposit
			if ((invoicesPayPal != null && invoicesPayPal.size() > 0)
					|| (invoices != null && invoices.size() > 0)) {
				// create the directory to save all the document, the folder's
				// name
				// is the actual date
				Path path = Paths.get(getPathDirectory());
				path = Files.createDirectories(path.toAbsolutePath());
			}

			if (invoices != null && invoices.size() > 0) {
				// result += " Total invoices processed like Direct Deposit: "
				// + invoices.size();

				String folderName = "DirectDeposit";
				// create the directory name DirectDeposit
				Path path = Paths.get(pathDirectory + File.separator
						+ folderName);
				path = Files.createDirectories(path.toAbsolutePath());

				// call the method to create supplier's file
				Path payableInvoice = createInvoice(invoices, path, user,
						directoryDate + File.separator + folderName);
				if (payableInvoice != null) {
					files.add(payableInvoice);
					result += detail;
				}

				// call the method to create supplier contact's file
				Path achFile = createACHFile(invoices, path, user,
						directoryDate + File.separator + folderName);
				if (achFile != null) {
					files.add(achFile);
					result += "</br>" + detail;
				}

				if (files.size() > 0) {
					// save all the directory into FTP folder
					copyDirectory(directoryDate + File.separator + folderName,
							path);

				}
			}

			if (invoicesPayPal != null && invoicesPayPal.size() > 0) {

				String folderName = "PayPal";
				// create the directory name DirectDeposit
				Path path = Paths.get(pathDirectory + File.separator
						+ folderName);
				path = Files.createDirectories(path.toAbsolutePath());

				// process the invoices
				result += "</br>"
						+ createPayablesPayPal(invoicesPayPal, path, user,
								directoryDate + File.separator + folderName);

				// save all the directory into FTP folder
				copyDirectory(directoryDate + File.separator + folderName, path);

			}

			if ((invoicesPayPal != null && invoicesPayPal.size() > 0)
					|| (invoices != null && invoices.size() > 0)) {

				// send by email all the files created to Finance
				// validate the files size to send by email
				emailService.sendFinancialEmail(files);
			}

		} catch (Exception e) {
			logSystemDAO.insertError(user, "createPayables",
					"Error: " + e.getMessage(), "OracleServiceImpl");
		}

		return result;
	}

	@Override
	public String createSupplierFiles(User user) {

		String result = "";
		try {
			// getPathDirectory() = getPath() + File.separator + getDateFiles();

			// List<Profile> profiles = profileDAO.getDataActiveOracle();
			List<Corporation> corporation = corporationDAO
					.getDataActiveOracle();
			List<User> users = userDAO.getDataActiveOracle();
			List<Address> addresses = addressDAO.getDataActiveOracle();
			List<BankInformation> banks = bankInformationDAO
					.getDataActiveOracle();
			List<RoutingNumbers> routingNumbers = routingNumbersDAO
					.getDataActiveOracle();

			// create the directory to save all the document, the folder's
			// name
			// is the actual date
			Path path = Paths.get(getPathDirectory());
			path = Files.createDirectories(path.toAbsolutePath());

			List<Path> files = new ArrayList<Path>();

			// call the method to create supplier's file
			Path supplier = createSupplier(corporation);
			if (supplier != null) {
				files.add(supplier);
			}

			Path supplierSite = createSupplierSite(corporation);
			if (supplierSite != null) {
				files.add(supplierSite);
			}

			Path supplierSiteAssig = createSupplierSiteAssignments(corporation);
			if (supplierSiteAssig != null) {
				files.add(supplierSiteAssig);
			}

			// call the method to create supplier contact's file
			Path supplierContact = createSupplierContact(users);
			if (supplierContact != null) {
				files.add(supplierContact);
			}

			// call the method to create supplier Address's file
			Path supplierAddress = createSupplierAddress(addresses);
			if (supplierAddress != null) {
				files.add(supplierAddress);
			}

			// call the method to create bank and branches
			Path supplierBankBranches = createBankAndBranches(routingNumbers);
			if (supplierBankBranches != null) {
				files.add(supplierBankBranches);
			}

			// call the method to create bank account information
			Path supplierBank = createSupplierBank(banks);
			if (supplierBank != null) {
				files.add(supplierBank);
			}

			result = "Supplier processed. Total register: "
					+ (users.size() + corporation.size() + addresses.size());
			result += " Total supplier: " + corporation.size();
			result += " Total bank account information: " + banks.size();
			result += " Folder's name: " + directoryDate;

			if (files.size() > 0) {
				// save all the directory into FTP folder
				copyDirectory(directoryDate, path);

				// send by email all the files created to Finance
				// validate the files size to send by email
				emailService.sendFinancialEmail(files);
			}

		} catch (Exception e) {
			logSystemDAO.insertError(user, "createSupplier",
					"Error: " + e.getMessage(), "OracleServiceImpl");
		}
		logSystemDAO.makePersistent(new LogSystem("OracleServiceImpl",
				"createSupplier", result, user != null ? user.getIp() : null,
				false, null, user, true));

		return result;
	}

	private void copyDirectory(String directoryName, Path source) {

		try {

			ConfigurationGeneral cg = applicationBean.getConfiguration();

			if (cg == null || StringUtils.isBlank(cg.getPathOracle())) {
				throw new Exception(
						"Folder Path Oracle configuration is required.");
			}

			// Path dest = Paths.get(cg.getPathOracle() + File.separator
			// + directoryDate);

			ftpUtil.uploadFTPFile(directoryName, source, cg);

			// DirUtils.copy(source, dest);

		} catch (Exception e) {
			logSystemDAO.insertError(null, "createSupplier",
					"Error copy files to FTP: " + e.getMessage(),
					"OracleServiceImpl");
		}

	}

	/**
	 * Create the supplier's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createSupplier(List<Corporation> corporations)
			throws Exception {
		Path supplierZip = null;
		if (corporations != null && corporations.size() > 0) {

			// create the file for supplier
			Path supplier = Paths.get(pathDirectory + File.separator
					+ nameSupplier);
			supplier = Files.createFile(supplier);

			// create BufferedWriter to supplier
			try (BufferedWriter supplierFile = Files.newBufferedWriter(
					supplier, charset)) {

				for (Corporation corporation : corporations) {

					/**
					 * process the file supplier, only is processed when the
					 * supplier number is null or was updated
					 */
					if (StringUtils.isBlank(corporation.getSupplierNumber())
							|| corporation.isNeedUpdate()) {

						boolean inserted = StringUtils.isBlank(corporation
								.getSupplierNumber());
						String line = "";
						// Import Action *
						// if the user does not have supplier number need to
						// be
						// inserted in Oracle
						line += inserted ? CREATE : UPDATE;
						line += ",";

						// Supplier Name* 360 CHAR
						line += corporation.getName();
						line += ",";

						if (inserted) {
							// Supplier Name New
							line += ",";

							// Supplier Number
							line += corporationDAO
									.updateSupplierNumber(corporation);
							line += ",";

						} else {
							// Supplier Name New
							line += corporation.getName();
							line += ",";

							// Supplier Number
							line += corporation.getSupplierNumber();
							line += ",";
						}
						// Tax Organization Type
						line += ",Corporation";
						// Supplier Type
						line += ",IBO";
						// Inactive Date is blank
						line += ",";
						// Business Relationship*
						line += inserted ? ",SPEND_AUTHORIZED" : ",";
						// Parent Supplier, Alias,D-U-N-S Number are blank
						line += ",,,";
						// One-time supplier
						line += ",N";
						// Customer Number,SIC,National Insurance
						// Number,Corporate Web Site,Chief Executive Title,Chief
						// Executive Name,Business Classifications Not
						// Applicable are blank
						line += ",,,,,,,";
						// Taxpayer Country
						line += ",US";
						// Taxpayer ID
						line += "," + corporation.getEin();
						// Federal reportable
						line += ",Y";
						// Federal Income Tax Type
						line += ",MISC7";

						// all the other field are blank
						line += ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,END";

						supplierFile.write(line, 0, line.length());
						supplierFile.newLine();
					}

				}

			} catch (Exception e) {
				throw e;
			}

			supplierZip = Paths.get(pathDirectory + "/SupplierImport"
					+ directoryDate + ".zip");
			// the zip file name that we will create
			File zipFileName = supplierZip.toFile();
			// create a zip to import into Oracle
			try (ZipOutputStream zipStream = new ZipOutputStream(
					new FileOutputStream(zipFileName))) {

				addToZipFile(supplier, zipStream);

			} catch (Exception e) {
				throw e;
			}

			// update the need update to false
			corporationDAO.updateNeedUpdatedFalse(corporations);

		}

		return supplierZip;
	}

	/**
	 * Create the file to import in Oracle the banks and branches
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createBankAndBranches(List<RoutingNumbers> routingNumbers)
			throws Exception {
		Path supplier = null;
		if (routingNumbers != null && routingNumbers.size() > 0) {

			// create the file for supplier
			supplier = Paths.get(pathDirectory + File.separator + nameBank);
			supplier = Files.createFile(supplier);

			// create BufferedWriter to supplier
			try (BufferedWriter supplierFile = Files.newBufferedWriter(
					supplier, charset)) {

				String line = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RapidImplementation>";
				supplierFile.write(line, 0, line.length());
				supplierFile.newLine();

				for (RoutingNumbers rn : routingNumbers) {

					line = " <RapidImplementationRow>";
					line += "<HomeCountry>United States</HomeCountry>";
					line += " <BankName>" + rn.getBank().getName()
							+ "</BankName>";
					line += " <BankNumber /><BankNameAlt />";
					line += " <BankBranchName>" + rn.getNumber()
							+ "</BankBranchName>";
					line += " <BranchNumber>" + rn.getNumber()
							+ "</BranchNumber>";
					line += "<EftSwiftCode /><BranchNameAlt /> <BankBranchType /> <BankAccountName /> <BankAccountNum /> <Currency /><LegalEntityName /> <AccountType /> <IBAN /> <AccountNameAlt /> <ApUseAllowed>Yes</ApUseAllowed><ArUseAllowed>Yes</ArUseAllowed><AssetSegments /> <CashClearingSegments />";
					line += " </RapidImplementationRow>";

					supplierFile.write(line, 0, line.length());
					supplierFile.newLine();

				}

				line = "</RapidImplementation>";
				supplierFile.write(line, 0, line.length());
				supplierFile.newLine();

			} catch (Exception e) {
				throw e;
			}

			// update the need update to false
			routingNumbersDAO.updateNeedUpdatedFalse(routingNumbers);

		}

		return supplier;
	}

	/**
	 * Create the supplier's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createSupplierSiteNewVersion(List<Corporation> corporations)
			throws Exception {
		Path supplierZip = null;
		if (corporations != null && corporations.size() > 0) {

			// create the file for supplier
			Path supplier = Paths.get(pathDirectory + File.separator
					+ nameSupplierSite);
			supplier = Files.createFile(supplier);

			// create BufferedWriter to supplier
			try (BufferedWriter supplierFile = Files.newBufferedWriter(
					supplier, charset)) {

				for (Corporation corporation : corporations) {

					/**
					 * process the file supplier, only is processed when the
					 * supplier number is null (means is new)
					 */
					if (StringUtils.isBlank(corporation.getSupplierNumber())) {

						String line = "CREATE,";

						// Supplier Name* 360 CHAR
						line += corporation.getName();
						line += ",";
						// Procurement BU*
						line += procurement + ",";
						// Address Name *
						line += corporation.getName().length() > 15 ? corporation
								.getName().substring(0, 14) : corporation
								.getName();
						// Supplier Site* always is MAIN-PURCH
						line += site + ",";

						// Supplier Site New, Inactive Date
						line += ",,";

						// Sourcing only,Purchasing,Procurement card,Pay,Primary
						// Pay
						line += "N,N,N,Y,Y,";

						// all the other field are blank
						line += ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,END";

						supplierFile.write(line, 0, line.length());
						supplierFile.newLine();
					}

				}

			} catch (Exception e) {
				throw e;
			}

			supplierZip = Paths.get(pathDirectory + "/SupplierSiteImport"
					+ directoryDate + ".zip");
			// the zip file name that we will create
			File zipFileName = supplierZip.toFile();
			// create a zip to import into Oracle
			try (ZipOutputStream zipStream = new ZipOutputStream(
					new FileOutputStream(zipFileName))) {

				addToZipFile(supplier, zipStream);

			} catch (Exception e) {
				throw e;
			}

		}

		return supplierZip;
	}

	/**
	 * Create the supplier's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createSupplierSiteAssignmentsNew(List<Corporation> corporations)
			throws Exception {
		Path supplierZip = null;
		if (corporations != null && corporations.size() > 0) {

			// create the file for supplier
			Path supplier = Paths.get(pathDirectory + File.separator
					+ nameSupplierSiteAssignments);
			supplier = Files.createFile(supplier);

			// create BufferedWriter to supplier
			try (BufferedWriter supplierFile = Files.newBufferedWriter(
					supplier, charset)) {

				for (Corporation corporation : corporations) {

					/**
					 * process the file supplier, only is processed when the
					 * supplier number is null (means is new)
					 */
					if (StringUtils.isBlank(corporation.getSupplierNumber())) {

						String line = "CREATE,";

						// Supplier Name* 360 CHAR
						line += corporation.getName();
						line += ",";
						// Supplier Site* always is MAIN-PURCH
						line += site + ",";
						// Procurement BU*
						line += procurement + ",";
						// Client BU*
						line += procurement + ",";
						// Bill-to BU*
						line += procurement + ",";

						// all the other field are blank
						line += ",,,N,,,,,,,END";

						supplierFile.write(line, 0, line.length());
						supplierFile.newLine();
					}

				}

			} catch (Exception e) {
				throw e;
			}

			supplierZip = Paths
					.get(pathDirectory + "/SupplierSiteAssignmentsImport"
							+ directoryDate + ".zip");
			// the zip file name that we will create
			File zipFileName = supplierZip.toFile();
			// create a zip to import into Oracle
			try (ZipOutputStream zipStream = new ZipOutputStream(
					new FileOutputStream(zipFileName))) {

				addToZipFile(supplier, zipStream);

			} catch (Exception e) {
				throw e;
			}

		}

		return supplierZip;
	}

	/**
	 * Create the supplier's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createSupplierSite(List<Corporation> corporations)
			throws Exception {
		Path supplierZip = null;
		if (corporations != null && corporations.size() > 0) {

			// create the file for supplier
			Path supplier = Paths.get(pathDirectory + File.separator
					+ nameSupplierSite);
			supplier = Files.createFile(supplier);

			// create BufferedWriter to supplier
			try (BufferedWriter supplierFile = Files.newBufferedWriter(
					supplier, charset)) {

				for (Corporation corporation : corporations) {

					/**
					 * process the file supplier, only is processed when the
					 * supplier number is null (means is new)
					 */
					if (StringUtils.isBlank(corporation.getSupplierNumber())) {

						String line = "";

						// Supplier Name* 360 CHAR
						line += corporation.getName();
						line += ",";
						// Supplier Site* always is MAIN-PURCH
						line += site + ",";
						// Procurement BU*
						line += procurement + ",";
						// Alternate Site Name
						line += ",";
						// Address Name *
						line += corporation.getName().length() > 15 ? corporation
								.getName().substring(0, 14) : corporation
								.getName();
						// Sourcing only,Purchasing,Procurement card,Pay,Primary
						// Pay
						line += "N,N,N,Y,Y,";

						// all the other field are blank
						line += ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,END";

						supplierFile.write(line, 0, line.length());
						supplierFile.newLine();
					}

				}

			} catch (Exception e) {
				throw e;
			}

			supplierZip = Paths.get(pathDirectory + "/SupplierSiteImport"
					+ directoryDate + ".zip");
			// the zip file name that we will create
			File zipFileName = supplierZip.toFile();
			// create a zip to import into Oracle
			try (ZipOutputStream zipStream = new ZipOutputStream(
					new FileOutputStream(zipFileName))) {

				addToZipFile(supplier, zipStream);

			} catch (Exception e) {
				throw e;
			}

		}

		return supplierZip;
	}

	/**
	 * Create the supplier's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createSupplierSiteAssignments(List<Corporation> corporations)
			throws Exception {
		Path supplierZip = null;
		if (corporations != null && corporations.size() > 0) {

			// create the file for supplier
			Path supplier = Paths.get(pathDirectory + File.separator
					+ nameSupplierSiteAssignments);
			supplier = Files.createFile(supplier);

			// create BufferedWriter to supplier
			try (BufferedWriter supplierFile = Files.newBufferedWriter(
					supplier, charset)) {

				for (Corporation corporation : corporations) {

					/**
					 * process the file supplier, only is processed when the
					 * supplier number is null (means is new)
					 */
					if (StringUtils.isBlank(corporation.getSupplierNumber())) {

						String line = "";

						// Supplier Name* 360 CHAR
						line += corporation.getName();
						line += ",";
						// Supplier Site* always is MAIN-PURCH
						line += site + ",";
						// Procurement BU*
						line += procurement + ",";
						// Client BU*
						line += procurement + ",";
						// Bill-to BU*
						line += procurement + ",";

						// all the other field are blank
						line += ",,,N,,,,,,,END";

						supplierFile.write(line, 0, line.length());
						supplierFile.newLine();
					}

				}

			} catch (Exception e) {
				throw e;
			}

			supplierZip = Paths
					.get(pathDirectory + "/SupplierSiteAssignmentsImport"
							+ directoryDate + ".zip");
			// the zip file name that we will create
			File zipFileName = supplierZip.toFile();
			// create a zip to import into Oracle
			try (ZipOutputStream zipStream = new ZipOutputStream(
					new FileOutputStream(zipFileName))) {

				addToZipFile(supplier, zipStream);

			} catch (Exception e) {
				throw e;
			}

		}

		return supplierZip;
	}

	/**
	 * Adds an extra file to the zip archive, copying in the created date and a
	 * comment.
	 * 
	 * @param file
	 *            file to be archived
	 * @param zipStream
	 *            archive to contain the file.
	 */
	private void addToZipFile(Path file, ZipOutputStream zipStream)
			throws IOException {
		String inputFileName = file.toFile().getPath();
		try (FileInputStream inputStream = new FileInputStream(inputFileName)) {

			// create a new ZipEntry, which is basically another file
			// within the archive. We omit the path from the filename
			ZipEntry entry = new ZipEntry(file.toFile().getName());
			zipStream.putNextEntry(entry);

			// Now we copy the existing file into the zip archive. To do
			// this we write into the zip stream, the call to putNextEntry
			// above prepared the stream, we now write the bytes for this
			// entry. For another source such as an in memory array, you'd
			// just change where you read the information from.
			byte[] readBuffer = new byte[2048];
			int amountRead;

			while ((amountRead = inputStream.read(readBuffer)) > 0) {
				zipStream.write(readBuffer, 0, amountRead);
			}

		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Create the supplier contact's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createSupplierContact(List<User> users) throws Exception {
		Path supplierZip = null;
		if (users != null && users.size() > 0) {

			// create the file for supplier
			Path supplier = Paths.get(pathDirectory + File.separator
					+ nameSupplierContact1);
			supplier = Files.createFile(supplier);

			// create the file for supplier
			Path supplier2 = Paths.get(pathDirectory + File.separator
					+ nameSupplierContact2);
			supplier2 = Files.createFile(supplier2);

			// create BufferedWriter to supplier
			try (BufferedWriter supplierFile = Files.newBufferedWriter(
					supplier, charset)) {

				for (User user : users) {

					/**
					 * process the file supplier, only is processed when the
					 * supplier number is null or was updated
					 */
					if (StringUtils.isNotBlank(user.getCorporationName())) {

						boolean inserted = !user.isNeedUpdate();
						String line = "";
						// Import Action *
						// if the user does not have supplier number need to
						// be
						// inserted in Oracle
						line += inserted ? CREATE : UPDATE;
						line += ",";

						// Supplier Name* 360 CHAR
						line += user.getCorporationName();
						line += ",";

						// Prefix
						line += ",";

						// First Name
						line += user.getFirstName();
						line += ",";

						// First Name New
						if (!inserted) {
							line += user.getFirstName();
						}
						line += ",";

						// Middle Name
						line += ",";

						// Last Name
						line += user.getLastName();
						line += ",";

						// Last Name New
						if (!inserted) {
							line += user.getLastName();
						}
						line += ",";

						// Job Title, Administrative Contact
						line += ",,";

						// E-Mail
						line += StringUtils.isNotBlank(user.getEmail()) ? user
								.getEmail() : "";
						line += ",";

						// E-Mail New
						if (!inserted) {
							line += StringUtils.isNotBlank(user.getEmail()) ? user
									.getEmail() : "";
						}
						line += ",";

						// Phone Country Code,Phone Area Code
						line += ",,";

						// Phone
						line += StringUtils.isNotBlank(user.getPhone()) ? user
								.getPhone() : "";
						line += ",";

						// all the other field are blank
						line += ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,END";

						supplierFile.write(line, 0, line.length());
						supplierFile.newLine();
					}

				}

			} catch (Exception e) {
				throw e;
			}

			supplierZip = Paths.get(pathDirectory + "/SupplierContactImport"
					+ directoryDate + ".zip");

			// the zip file name that we will create
			File zipFileName = supplierZip.toFile();
			// create a zip to import into Oracle
			try (ZipOutputStream zipStream = new ZipOutputStream(
					new FileOutputStream(zipFileName))) {

				addToZipFile(supplier, zipStream);
				addToZipFile(supplier2, zipStream);

			} catch (Exception e) {
				throw e;
			}

			// update the need update to false
			userDAO.updateNeedUpdatedFalse(users);

		}

		return supplierZip;
	}

	/**
	 * Create the supplier address's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createSupplierAddress(List<Address> addresses)
			throws Exception {
		Path supplierZip = null;
		if (addresses != null && addresses.size() > 0) {

			// create the file for supplier
			Path supplier = Paths.get(pathDirectory + File.separator
					+ nameSupplierAddress);
			supplier = Files.createFile(supplier);

			// create BufferedWriter to supplier
			try (BufferedWriter supplierFile = Files.newBufferedWriter(
					supplier, charset)) {

				for (Address address : addresses) {

					/**
					 * process the file supplier, only is processed when the
					 * supplier number is null or was updated
					 */
					if (StringUtils.isNotBlank(address.getCorporationName())) {

						boolean inserted = !address.isNeedUpdate();
						String line = "";
						// Import Action *
						// if the user does not have supplier number need to
						// be
						// inserted in Oracle
						line += inserted ? CREATE : UPDATE;
						line += ",";

						// Supplier Name* 360 CHAR
						line += address.getCorporationName();
						line += ",";

						// Address Name *
						line += address.getCorporationName().length() > 15 ? address
								.getCorporationName().substring(0, 14)
								: address.getCorporationName();
						line += ",";

						// Address Name New
						line += ",";

						// Country
						line += "US,";

						// Address Line 1
						line += address.getDescription();
						line += ",";

						// Address Line 2,3,4,Phonetic Address Line,Address
						// Element Attribute 1,Address Element Attribute
						// 2,Address Element Attribute 3,Address Element
						// Attribute 4,Address Element
						// Attribute 5,Building,Floor Number
						line += ",,,,,,,,,,,";

						// City 60
						line += address.getCity();
						line += ",";

						// State 60
						line += address.getState();
						line += ",";

						// Province,County
						line += ",,";

						// Postal code
						line += address.getZipCode();
						line += ",";

						// all the other field are blank
						line += ",,,,,,,,,,,,N,N,Y,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,END";

						supplierFile.write(line, 0, line.length());
						supplierFile.newLine();
					}

				}

			} catch (Exception e) {
				throw e;
			}

			supplierZip = Paths.get(pathDirectory + "/SupplierAddressImport"
					+ directoryDate + ".zip");
			// the zip file name that we will create
			File zipFileName = supplierZip.toFile();
			// create a zip to import into Oracle
			try (ZipOutputStream zipStream = new ZipOutputStream(
					new FileOutputStream(zipFileName))) {

				addToZipFile(supplier, zipStream);

			} catch (Exception e) {
				throw e;
			}

			// update the need update to false
			addressDAO.updateNeedUpdatedFalse(addresses);

		}

		return supplierZip;
	}

	/**
	 * Create the supplier bank account information file to import in Oracle
	 * 
	 * @param banks
	 * @throws Exception
	 */
	private Path createSupplierBank(List<BankInformation> banks)
			throws Exception {
		Path supplierZip = null;
		if (banks != null && banks.size() > 0) {

			// create the file for Supplier Payees
			Path supplier = Paths.get(pathDirectory + File.separator
					+ nameSupplierBank1);
			supplier = Files.createFile(supplier);

			// create the file for Supplier Bank Accounts
			Path supplier2 = Paths.get(pathDirectory + File.separator
					+ nameSupplierBank2);
			supplier2 = Files.createFile(supplier2);

			// create the file for Supplier Bank Account Assignments
			Path supplier3 = Paths.get(pathDirectory + File.separator
					+ nameSupplierBank3);
			supplier3 = Files.createFile(supplier3);

			BufferedWriter supplierFilePayees = null;
			BufferedWriter supplierFileBank = null;
			BufferedWriter supplierFileAssignments = null;

			try {
				// create BufferedWriter to supplier
				supplierFilePayees = Files.newBufferedWriter(supplier, charset);

				supplierFileBank = Files.newBufferedWriter(supplier2, charset);

				supplierFileAssignments = Files.newBufferedWriter(supplier3,
						charset);

				// get the Batch Identifier
				Long batch = bankInformationDAO
						.getNextValue("sq_supplier_bank");
				int i = 1;

				for (BankInformation bank : banks) {

					/**
					 * process the file supplier, only is processed when the
					 * supplier number is null or was updated
					 */
					if (StringUtils.isNotBlank(bank.getSupplierNumber())) {

						// *Import Batch Identifier
						String line1 = batch + ",";
						// *Payee Identifier
						line1 += i + ",";
						// Business Unit Name
						line1 += procurement + ",";
						// *Supplier Number
						line1 += bank.getSupplierNumber() + ",";
						// Supplier Site
						line1 += site + ",";
						// *Pay Each Document Alone
						line1 += "N,";
						// Payment Method Code
						line1 += "EFT,";
						// Delivery Channel Code,Settlement Priority
						line1 += ",,";
						// Remit Delivery Method
						line1 += "EMAIL,";
						// Remit Advice Email
						line1 += ",";
						// Remit Advice Fax
						line1 += ",,,,,,,,,";

						// *Import Batch Identifier
						String line2 = batch + ",";
						// *Payee Identifier
						line2 += i + ",";
						// *Payee Bank Account Identifier
						line2 += i + ",";
						// **Bank Name
						line2 += "J.P. Morgan Chase,";
						// **Branch Name
						line2 += "Las Olas Branch,";
						// *Account Country Code
						line2 += "US,";
						// Account Name
						line2 += ",";
						// *Account Number
						line2 += bank.getNumber() + ",";
						// Account Currency Code
						line2 += "USD,";
						// Allow International Payments
						line2 += "N,";
						// Account Start Date
						line2 += ",,,,,,,,,,,,,,,,,,,,,,,,,,,,";

						// *Import Batch Identifier
						String line3 = batch + ",";
						// *Payee Identifier
						line3 += i + ",";
						// *Payee Bank Account Identifier
						line3 += i + ",";
						// *Payee Bank Account Assignment Identifier
						line3 += i + ",";
						// *Primary Flag
						line3 += "Y,";
						// Account Assignment Start Date,Account Assignment End
						// Date
						line3 += ",,";

						supplierFilePayees.write(line1, 0, line1.length());
						supplierFilePayees.newLine();

						supplierFileBank.write(line2, 0, line2.length());
						supplierFileBank.newLine();

						supplierFileAssignments.write(line3, 0, line3.length());
						supplierFileAssignments.newLine();

						i++;
					}

				}

			} catch (Exception e) {
				throw e;
			} finally {

				if (supplierFilePayees != null)
					supplierFilePayees.close();
				if (supplierFileBank != null)
					supplierFileBank.close();
				if (supplierFileAssignments != null)
					supplierFileAssignments.close();
			}

			supplierZip = Paths.get(pathDirectory + "/SupplierBankAccImport"
					+ directoryDate + ".zip");
			// the zip file name that we will create
			File zipFileName = supplierZip.toFile();
			// create a zip to import into Oracle
			try (ZipOutputStream zipStream = new ZipOutputStream(
					new FileOutputStream(zipFileName))) {

				addToZipFile(supplier, zipStream);
				addToZipFile(supplier2, zipStream);
				addToZipFile(supplier3, zipStream);

			} catch (Exception e) {
				throw e;
			}

			// update the need update to false
			bankInformationDAO.updateNeedUpdatedFalse(banks);

		}

		return supplierZip;
	}

	/**
	 * Create the supplier's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createACHFile(List<Invoice> invoices, Path path, User user,
			String folderName) throws Exception {
		Path achFile = null;
		if (invoices != null && invoices.size() > 0) {

			detail = " ACH created. Total invoices processed: "
					+ invoices.size();

			// create the file for supplier
			achFile = Paths.get(path + File.separator + nameACH + directoryDate
					+ ".csv");
			achFile = Files.createFile(achFile);

			DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
			DateFormat hourFormat = new SimpleDateFormat("kkmm");
			Calendar calendar = Calendar.getInstance();

			// create BufferedWriter to supplier
			try (BufferedWriter payableFile = Files.newBufferedWriter(achFile,
					charset)) {

				// FILE HEADER RECORD (1)

				StringBuilder sb = new StringBuilder();

				sb.append("101");
				sb.append(" 267084131");
				sb.append("1462074138");
				sb.append(dateFormat.format(calendar.getTime()));
				sb.append(hourFormat.format(calendar.getTime()));

				sb.append("A");
				sb.append("094");
				sb.append("101");
				sb.append("Chase Bank             ");
				sb.append("Great VirtualWorks     ");
				sb.append("        ");

				payableFile.write(sb.toString(), 0, sb.toString().length());
				payableFile.newLine();

				// BATCH HEADER RECORD (5)
				sb = new StringBuilder();
				sb.append("5200");
				sb.append("Great VirtualWor");
				sb.append("                    ");
				sb.append("9785872001CCDVENDORS   ");
				// line 9 Efective entry date
				sb.append(dateFormat.format(calendar.getTime()));

				calendar.add(Calendar.DAY_OF_YEAR, 2);
				// future day
				sb.append(dateFormat.format(calendar.getTime()));

				sb.append("   ");
				sb.append("1");
				sb.append("26708413");
				// batch secuence
				sb.append("0000001");
				// add the line into the file
				payableFile.write(sb.toString(), 0, sb.toString().length());
				payableFile.newLine();

				int count = 1;
				long rountingNumberTotal = 0l;
				Double importTotal = 0.0;

				Map<String, Invoice> invoicesMap = new HashMap<String, Invoice>();
				// process first the invoice to group by corporation
				for (Invoice invoice : invoices) {

					// if the IBO doesn have bank account information insert
					// into log system and don't add to ACH file
					BankInformation bank = invoice.getUser().getCorporation()
							.getBankInformation() != null
							&& invoice.getUser().getCorporation()
									.getBankInformation().size() > 0 ? invoice
							.getUser().getCorporation().getBankInformation()
							.get(0) : null;
					String detail = null;
					if (bank == null || bank.getId() == null) {
						detail = "User does not have bank account information. ";
					} else if (StringUtils.isBlank(bank.getRouting())) {
						detail = "User does not have rounting number information. ";
					} else if (StringUtils.isBlank(bank.getNumber())) {
						detail = "User does not have account number information. ";
					} else if (invoice.getImportTotal() <= 0) {
						detail = "Invoice import incorrect: "
								+ invoice.getImportTotal();
					} else if (StringUtils.isBlank(invoice.getUser()
							.getCorporation().getEin())) {
						detail = "User does not have EIN number. ";
					} else if (StringUtils.isBlank(invoice.getUser()
							.getCorporation().getName())) {
						detail = "User does not have corporation name. ";
					}

					if (detail != null) {
						LogSystem logSystem = new LogSystem(
								"FinanceServiceImpl", "createACHFile",
								"Invoice NO processed. " + detail
										+ invoice.getUser().toString()
										+ " Invoice: " + invoice.toString(),
								user != null ? user.getIp() : IpServer
										.ipServer(), false,
								invoice.getId(), user, true);
						logSystemDAO.makePersistent(logSystem);
						logSystemDAO.flush();
					} else {

						Invoice invEx = invoicesMap.get(invoice.getUser()
								.getCorporation().getId());
						if (invEx == null) {
							invoicesMap.put(invoice.getUser().getCorporation()
									.getId(), invoice);
						} else {
							// if already exists SUM the import total
							invEx.setImportTotal(invEx.getImportTotal()
									+ invoice.getImportTotal());
							invoicesMap.put(invoice.getUser().getCorporation()
									.getId(), invEx);
						}
					}

				}

				// process the correct invoice
				for (Map.Entry<String, Invoice> entry : invoicesMap.entrySet()) {

					Invoice invoice = entry.getValue();

					BankInformation bank = invoice.getUser().getCorporation()
							.getBankInformation() != null
							&& invoice.getUser().getCorporation()
									.getBankInformation().size() > 0 ? invoice
							.getUser().getCorporation().getBankInformation()
							.get(0) : null;

					// ENTRY DETAIL RECORD (6)
					sb = new StringBuilder();
					// 1,2 RECORD TYPE CODE + TRANSACTION CODE
					// 22 For checking dollar
					sb.append("622");

					// 3 RECEIVING DFI ID
					sb.append(bank.getRouting());
					rountingNumberTotal += Long.parseLong(bank.getRouting());

					// 5 DFI account number
					sb.append(fixedLengthStringLeft(bank.getNumber(), 17));

					// 6 DOLLAR AMOUNT
					Double importTotal2DecimalPlaces = MathUtils.round(
							invoice.getImportTotal(), 2);
					sb.append(fixedDoubleFormattedString(
							importTotal2DecimalPlaces * 100, 10));
					importTotal += importTotal2DecimalPlaces;

					// 7 INDIVIDUAL IDENTIFICATION NUMBER
					sb.append(fixedLengthStringLeft(invoice.getUser()
							.getCorporation().getEin(), 15));

					// 8 individual name 22 characteres
					sb.append(invoice.getUser().getCorporation().getName()
							.length() > 22 ? invoice.getUser().getCorporation()
							.getName().substring(0, 22)
							: fixedLengthStringLeft(invoice.getUser()
									.getCorporation().getName(), 22));

					// 9 Discretionary data
					sb.append("  ");

					// 10 ADDENDA RECORD INDICATOR
					sb.append("0");

					// 11 TRACE NUMBER
					sb.append("26708413");

					// number line
					sb.append(fixedDoubleFormattedString(count++, 7));

					payableFile.write(sb.toString(), 0, sb.toString().length());
					payableFile.newLine();

				}
				/*
				 * BATCH CONTROL RECORD (8)
				 */
				sb = new StringBuilder();

				// 1,2 RECORD TYPE CODE + SERVICE CLASS CODE
				sb.append("8200");
				// 3 ENTRY/ADDENDA COUNT
				sb.append(fixedDoubleFormattedString(--count, 6));

				// 4 ENTRY HASH
				sb.append(fixedDoubleFormattedString(rountingNumberTotal, 10));

				// 5 TOTAL DEBIT ENTRY DOLLAR AMOUNT
				sb.append("000000000000");

				// 6 TOTAL CREDIT ENTRY DOLLAR AMOUNT
				Double total = MathUtils.round(importTotal, 2);
				sb.append(fixedDoubleFormattedString(total * 100, 12));

				// 7 COMPANY IDENTIFICATION
				sb.append("1462074138");
				// 8 MESSAGE AUTHENTICATION CODE
				sb.append("                   ");
				// 9 RESERVED
				sb.append("      ");
				// ORIGINATING DFI IDENTIFICATION
				sb.append("46207413");
				// BATCH NUMBER
				sb.append(fixedDoubleFormattedString(count, 7));

				payableFile.write(sb.toString(), 0, sb.toString().length());
				payableFile.newLine();

				/*
				 * FILE CONTROL RECORD (9)
				 */
				sb = new StringBuilder();
				// 1 RECORD TYPE CODE
				sb.append("9");
				// 2 BATCH COUNT
				sb.append("000001");

				// 3 BLOCK COUNT
				// Must be equal to the number of blocks in the file. Ex. - Ten
				// lines of data equal ‘1’ block.
				long blocks = (long) (count + 3) / 10;
				sb.append(fixedDoubleFormattedString(blocks, 6));

				// 4 Entry/Agenda Count
				sb.append(fixedDoubleFormattedString(count, 8));
				// 5 Entry Hash
				sb.append(fixedDoubleFormattedString(rountingNumberTotal, 10));

				// 6 TOTAL DEBIT ENTRY DOLLAR AMOUNT IN FILE
				sb.append("000000000000");

				// 7 TOTAL CREDIT ENTRY DOLLAR AMOUNT
				sb.append(fixedDoubleFormattedString(total * 100, 12));
				// 8 Reserve 39
				sb.append("                                       ");

				payableFile.write(sb.toString(), 0, sb.toString().length());
				payableFile.newLine();

				detail += " Total corporations ACH: " + count
						+ " Import Total: " + myFormatter.format(importTotal)
						+ " Folder's name: " + folderName;

				logSystemDAO
						.makePersistent(new LogSystem("OracleServiceImpl",
								"createPayables", detail, user != null ? user
										.getIp() : IpServer.ipServer(), false,
								null, user, true));
				logSystemDAO.flush();

			} catch (Exception e) {
				throw e;
			}

		}

		return achFile;
	}

	public static String fixedLengthStringLeft(String string, int length) {
		return String.format("%-" + length + "s", string);
	}

	public static String fixedDoubleFormattedString(double d, int cantPlace) {

		String ceros = "";
		for (int i = 0; i < cantPlace; i++) {
			ceros += "0";
		}
		DecimalFormat df2 = new DecimalFormat(ceros);
		return df2.format(d);

	}

	private static FileSystem createZipFileSystem(Path path) throws IOException {
		// convert the filename to a URI
		// final URI uri = URI.create("jar:file:" + path.toUri().getPath());

		final Map<String, String> env = new HashMap<>();
		env.put("create", "true");

		return FileSystems.newFileSystem(path, null);
	}

	/**
	 * Create the supplier's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private Path createInvoice(List<Invoice> invoices, Path path, User user,
			String folderName) throws Exception {
		Path supplierZip = null;
		if (invoices != null && invoices.size() > 0) {

			Double importTotal = 0.0;
			int totalSendOracle = 0;

			// create the file for Supplier Payees
			Path supplier = Paths.get(path + File.separator + nameInvoice);
			supplier = Files.createFile(supplier);

			// create the file for Supplier Bank Accounts
			Path supplier2 = Paths.get(path + File.separator + nameInvoiceLine);
			supplier2 = Files.createFile(supplier2);

			BufferedWriter invoiceFile = null;
			BufferedWriter invoiceLineFile = null;
			try {
				// create BufferedWriter to supplier
				invoiceFile = Files.newBufferedWriter(supplier, charset);

				invoiceLineFile = Files.newBufferedWriter(supplier2, charset);

				String line1 = "";
				String line2 = "";
				for (Invoice invoice : invoices) {

					/**
					 * process the file supplier, only is processed when the
					 * supplier number is null or was updated
					 */
					if (StringUtils.isNotBlank(invoice.getUser()
							.getCorporation().getSupplierNumber())
							&& invoice.getImportTotal() > 0) {

						totalSendOracle++;

						importTotal += invoice.getImportTotal();

						// get the Batch Identifier
						Long batch = bankInformationDAO
								.getNextValue("sq_invoice_id");

						// *Invoice ID
						line1 = batch + ",";
						// *Business Unit
						line1 += procurement + ",";
						// *Source
						line1 += "External,";
						// *Invoice Number
						line1 += invoice.getNumber()
								+ invoice.getUser().getCorporation()
										.getSupplierNumber() + ",";
						// *Invoice Amount
						line1 += myFormatter.format(invoice.getImportTotal())
								+ ",";
						// *Invoice Date
						line1 += (new SimpleDateFormat("YYYY/MM/dd"))
								.format(invoice.getPayDate()) + ",";

						// **Supplier Name
						line1 += ",";
						// **Supplier Number
						line1 += invoice.getUser().getCorporation()
								.getSupplierNumber()
								+ ",";
						// Supplier Site
						line1 += site + ",";
						// Invoice Currency
						line1 += "USD,";
						// Payment Currency
						line1 += "USD,";
						// Description
						line1 += ",";
						// Import Set
						line1 += ",";
						// *Invoice Type
						line1 += "STANDARD,";
						// Legal Entity,Customer Tax Registration
						// Number,Customer Registration Code,First-Party Tax
						// Registration Number,Supplier Tax Registration Number
						line1 += ",,,,,";
						// *Payment Terms
						line1 += "Immediate,";
						// Terms Date,Goods Received Date,Invoice Received
						// Date,Accounting Date
						line1 += ",,,,";
						// Payment Method
						line1 += "EFT,";
						// Pay Group
						line1 += "IBO,";
						// Pay Alone
						line1 += "N,";
						// Rest in blank
						line1 += ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,END";

						invoiceFile.write(line1, 0, line1.length());
						invoiceFile.newLine();

						int i = 1;
						// add every work in a new line
						for (InvoiceWork iw : invoice.getInvoiceWork()) {

							// *Invoice ID
							line2 = batch + ",";
							// Line Number
							line2 += i + ",";
							// *Line Type
							line2 += "ITEM,";
							// *Amount
							line2 += myFormatter.format(iw
									.getImportTotalFinance()) + ",";
							// Invoice Quantity
							line2 += myFormatter.format(iw
									.getTotalHoursFinance()) + ",";
							// Unit Price
							line2 += myFormatter.format(iw.getAmount()) + ",";
							// UOM,Description,PO Number, etc....
							line2 += ",,,,,,,,,,,,,,,,";
							// Distribution Set
							line2 += "IBO Services,";
							line2 += ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,END";

							invoiceLineFile.write(line2, 0, line2.length());
							invoiceLineFile.newLine();

							i++;
						}

						// add every incentive in a new line
						for (Incentive incentive : invoice.getIncentive()) {

							// *Invoice ID
							line2 = batch + ",";
							// Line Number
							line2 += i + ",";
							// *Line Type
							line2 += "ITEM,";
							// *Amount
							line2 += myFormatter.format(incentive.getAmount())
									+ ",";
							// Invoice Quantity
							line2 += ",";
							// Unit Price
							line2 += ",";
							// UOM,Description,PO Number, etc....
							line2 += ",,,,,,,,,,,,,,,,";
							// Distribution Set
							line2 += "IBO Services,";
							line2 += ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,END";
							// the incentive the Type Incentive
							invoiceLineFile.write(line2, 0, line2.length());
							invoiceLineFile.newLine();

							i++;
						}

						// add the admin fee like a line
						// *Invoice ID
						line2 = batch + ",";
						// Line Number
						line2 += i + ",";
						// *Line Type
						line2 += "ITEM,";
						// *Amount
						line2 += "-"
								+ myFormatter.format(invoice.getAdminFee())
								+ ",";
						// Invoice Quantity
						line2 += ",";
						// Unit Price
						line2 += ",";
						// UOM,Description,PO Number, etc....
						line2 += ",,,,,,,,,,,,,,,,";
						// Distribution Set
						line2 += "IBO Admin Fee,";
						line2 += ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,END";

						invoiceLineFile.write(line2, 0, line2.length());
						invoiceLineFile.newLine();

					} else {

						if (StringUtils.isBlank(invoice.getUser()
								.getCorporation().getSupplierNumber())) {
							detail = "Supplier Number not defined. ";
						} else if (invoice.getImportTotal() < 0) {
							detail = "Invoice import incorrect: "
									+ invoice.getImportTotal();
						}

						LogSystem logSystem = new LogSystem(
								"FinanceServiceImpl", "createOracleFile",
								"Invoice NO processed. " + detail + " "
										+ invoice.getUser().toString()
										+ " Invoice: " + invoice.toString(),
								user != null ? user.getIp() : IpServer
										.ipServer(), false,
								invoice.getId(), user, true);
						logSystemDAO.makePersistent(logSystem);
						logSystemDAO.flush();

					}

				}

				detail = " Direct Deposit processed. Total invoices: "
						+ invoices.size() + " Total processed: "
						+ totalSendOracle + " Import Total: "
						+ myFormatter.format(importTotal) + " Folder's name: "
						+ folderName;

				logSystemDAO
						.makePersistent(new LogSystem("FinanceServiceImpl",
								"createPayables", detail, user != null ? user
										.getIp() : IpServer.ipServer(), false,
								null, user, true));
				logSystemDAO.flush();

			} catch (Exception e) {
				throw e;
			} finally {

				if (invoiceFile != null)
					invoiceFile.close();
				if (invoiceLineFile != null)
					invoiceLineFile.close();
			}

			supplierZip = Paths.get(path + "/PayablesStandardInvoiceImport"
					+ directoryDate + ".zip");

			// the zip file name that we will create
			File zipFileName = supplierZip.toFile();
			// create a zip to import into Oracle
			try (ZipOutputStream zipStream = new ZipOutputStream(
					new FileOutputStream(zipFileName))) {

				addToZipFile(supplier, zipStream);
				addToZipFile(supplier2, zipStream);

			} catch (Exception e) {
				throw e;
			}

			// update the need update to false
			invoiceDAO.updateNeedUpdatedFalse(invoices);

		}

		return supplierZip;
	}

	/**
	 * Create the supplier's file to import in Oracle
	 * 
	 * @param profiles
	 * @throws Exception
	 */
	private String createPayablesPayPal(List<Invoice> invoices, Path path,
			User user, String folderName) throws Exception {

		String result = "";
		if (invoices != null && invoices.size() > 0) {

			result += " PayPal processed. Total user: " + invoices.size();
			// get the max import per file for Paypal
			ConfigurationSystem cg = applicationBean.getSystemConf();
			Double maxImportFile = cg.getPaypalFileLimit();

			// save import total of all the invoices added to a file
			Double importSum = 0.0;
			Double importTotal = 0.0;
			Double importInvoice = 0.0;

			BufferedWriter invoiceFile = null;
			// create the file for Supplier Payees
			Path invoicePath = null;

			// name of the file
			String nameFilePayPal = (new SimpleDateFormat("MMddyyyy"))
					.format(new Date()) + namePayPal;
			Integer numberOfFile = 1;

			String line = "";

			Map<String, Invoice> invoicesMap = new HashMap<String, Invoice>();
			// process first the invoice to group by corporation
			for (Invoice invoice : invoices) {

				String id = invoice.getUser().getUser().getEmail();

				Invoice invEx = invoicesMap.get(id);
				if (invEx == null) {
					invoicesMap.put(id, invoice);
				} else {
					// if already exists SUM the import total
					invEx.setImportTotal(invEx.getImportTotal()
							+ invoice.getImportTotal());
					invoicesMap.put(id, invEx);
				}

			}

			// process the correct invoice
			for (Map.Entry<String, Invoice> entry : invoicesMap.entrySet()) {

				Invoice invoice = entry.getValue();

				// for (Invoice invoice : invoices) {

				importInvoice = invoice.getImportTotal();

				importTotal += importInvoice;

				// if is the first invoice create the document
				if (importSum.doubleValue() == 0.0
						&& invoice.getImportTotal() <= maxImportFile) {
					invoicePath = Paths.get(path + File.separator
							+ nameFilePayPal + numberOfFile + extPayPal);
					numberOfFile++;
					invoicePath = Files.createFile(invoicePath);
					invoiceFile = Files.newBufferedWriter(invoicePath, charset);
				} else if (importSum + invoice.getImportTotal() > maxImportFile
						&& invoice.getImportTotal() <= maxImportFile) {
					// need to create another file before add the new invoice
					invoiceFile.close();
					invoicePath = Paths.get(path + File.separator
							+ nameFilePayPal + numberOfFile + extPayPal);
					numberOfFile++;
					invoicePath = Files.createFile(invoicePath);
					invoiceFile = Files.newBufferedWriter(invoicePath, charset);

				} else if (invoice.getImportTotal() > maxImportFile) {

					// if only one import is > than the maximun split the import

					Double files = invoice.getImportTotal() / maxImportFile;
					Double fractionalPart = files % 1;
					Double integralPart = files - fractionalPart;

					Integer totalFiles = ((Double) (integralPart + (fractionalPart > 0 ? 1
							: 0))).intValue();

					// create all the files except the last one
					for (int i = 0; i < totalFiles - 1; i++) {

						// close the file
						if (invoiceFile != null)
							invoiceFile.close();

						invoicePath = Paths.get(path + File.separator
								+ nameFilePayPal + numberOfFile + extPayPal);
						numberOfFile++;
						invoicePath = Files.createFile(invoicePath);
						invoiceFile = Files.newBufferedWriter(invoicePath,
								charset);

						importInvoice -= maxImportFile;

						// email addres, amount, currency code
						line = invoice.getUser().getUser().getEmail() + ","
								+ myFormatter.format(maxImportFile) + ",USD";
						invoiceFile.write(line, 0, line.length());
						invoiceFile.newLine();

					}

					// close the file
					if (invoiceFile != null)
						invoiceFile.close();

					invoicePath = Paths.get(path + File.separator
							+ nameFilePayPal + numberOfFile + extPayPal);
					numberOfFile++;
					invoicePath = Files.createFile(invoicePath);
					invoiceFile = Files.newBufferedWriter(invoicePath, charset);

					// LogSystem logSystem = new LogSystem(
					// "FinanceServiceImpl",
					// "createPayablesPayPal",
					// "Invoice NO processed. The import total is greather than maximum allow by PayPal "
					// + invoice.getUser().toString()
					// + " Invoice: " + invoice.toString(),
					// user != null ? user.getIp() : IpServer.ipServer(),
					// false, invoice.getId(), user, true);
					// logSystemDAO.makePersistent(logSystem);
					// logSystemDAO.flush();

				}
				importSum += importInvoice;

				// email addres, amount, currency code
				line = invoice.getUser().getUser().getEmail() + ","
						+ myFormatter.format(importInvoice) + ",USD";
				invoiceFile.write(line, 0, line.length());
				invoiceFile.newLine();

			}

			// close the file
			if (invoiceFile != null)
				invoiceFile.close();

			// update the need update to false
			invoiceDAO.updateNeedUpdatedFalse(invoices);

			result += " Import total: " + myFormatter.format(importTotal)
					+ " Total files created: " + (numberOfFile - 1)
					+ " Folder's name: " + folderName;

			logSystemDAO.makePersistent(new LogSystem("OracleServiceImpl",
					"createPayables", result, user != null ? user.getIp()
							: IpServer.ipServer(), false, null, user, true));
			logSystemDAO.flush();

		}

		return result;

	}
}
