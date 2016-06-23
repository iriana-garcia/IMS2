package com.ghw.util;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.ghw.model.Address;
import com.ghw.model.BankInformation;
import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.Corporation;
import com.ghw.model.Country;
import com.ghw.model.IboState;
import com.ghw.model.IboType;
import com.ghw.model.OwnerType;
import com.ghw.model.Profile;
import com.ghw.model.States;
import com.ghw.model.User;
import com.ghw.model.UserType;
import com.ghw.model.UserUtil;
import com.ghw.service.BankInformationService;
import com.ghw.service.CorporationService;
import com.ghw.service.ProfileService;
import com.ghw.service.StatesService;
import com.ghw.service.UserService;

public class ImportIBO {

	public static void main(String[] args) {
		try (FileWriter writer = new FileWriter(
				"C:\\Users\\ifernandez\\Documents\\MY DOCUMENTS\\IMS\\IBO_IMPORT.csv")) {

			ApplicationContext context = new ClassPathXmlApplicationContext(
					"spring-servlet.xml");

			JdbcTemplate jdbcTemplate = (JdbcTemplate) context
					.getBean("jdbcTemplate");

			DriverManagerDataSource dataSourceVCS = (DriverManagerDataSource) context
					.getBean("dataSourceVCS");

			DriverManagerDataSource dataSourceDD = (DriverManagerDataSource) context
					.getBean("dataSourceDirectDeposit");

			StatesService statesService = (StatesService) context
					.getBean("statesService");
			UserService userService = (UserService) context
					.getBean("userService");
			ProfileService profileService = (ProfileService) context
					.getBean("profileService");
			BankInformationService bankInformationService = (BankInformationService) context
					.getBean("bankInformationService");
			CorporationService corporationService = (CorporationService) context
					.getBean("corporationService");

			StringEncryptor stringEncryptor = (StringEncryptor) context
					.getBean("stringEncryptor");

			// DriverManagerDataSource dataSourceIncontact =
			// (DriverManagerDataSource) context
			// .getBean("dataSourceVCS");
			//
			// jdbcTemplate.setDataSource(dataSourceIncontact);
			// cargo los IBO with number
			// String csvFile =
			// "C:\\Users\\ifernandez\\Documents\\MY DOCUMENTS\\IMS\\IBO_User_ID.csv";
			//
			// BufferedReader br = null;
			// String line = "";
			// String cvsSplitBy = ",";
			//
			// Map<String, String> users = new HashMap<String, String>();
			//
			// br = new BufferedReader(new FileReader(csvFile));
			// while ((line = br.readLine()) != null) {
			//
			// // use comma as separator
			// String[] country = line.split(cvsSplitBy);
			//
			// users.put(country[0], country[1]);
			//
			// }

			// get all in direct deposit

			jdbcTemplate.setDataSource(dataSourceDD);
			List<Map<String, Object>> listDD = jdbcTemplate
					.queryForList("SELECT email,dd_routing_nbr,dd_bank_acct_nbr,dd_tax_id "
							+ "FROM dd_user "
							+ "where email is not null and dd_tax_id is not null and dd_routing_nbr is not null and dd_bank_acct_nbr is not null");

			Map<String, BankInformation> bank = new HashMap<String, BankInformation>();

			for (Map<String, Object> map : listDD) {
				String email = (String) map.get("email");
				String ein = (String) map.get("dd_tax_id");
				if (StringUtils.isNotBlank(email)
						&& StringUtils.isNotBlank(ein)) {
					System.out.println(ein);
					BankInformation bi = new BankInformation();
					bi.setRouting(stringEncryptor.decrypt((String) map
							.get("dd_routing_nbr")));
					bi.setNumber(stringEncryptor.decrypt((String) map
							.get("dd_bank_acct_nbr")));
					bi.setNeedUpdate(true);

					// String id = email.toLowerCase() + "-" + ein;
					String id = ein;

					bank.put(id, bi);
				}
			}

			ActiveDirectory activeDirectory = new ActiveDirectory();

			ConfigurationGeneral configuration = new ConfigurationGeneral();
			configuration.setLdapServer("ghw.com");
			configuration.setLdapPort("389");
			configuration.setLdapDn("DC=ghw,DC=com");
			configuration.setLdapDnIbo("OU=Homebase Agents,DC=ghw,DC=com");
			configuration.setLdapUser("ifernandez@ghw.com");
			configuration.setLdapPass("Espiraculo,16");

			activeDirectory.openConnection(configuration);

			// get all the IBO from AD to assigned the username
			List<UserUtil> iboAD = activeDirectory
					.searchAllUser("OU=Homebase Agents,DC=ghw,DC=com");
			Map<String, UserUtil> emailName = new HashMap<String, UserUtil>();
			for (UserUtil userUtil : iboAD) {
				if (StringUtils.isNotBlank(userUtil.getEmail()))
					emailName.put(userUtil.getEmail().toLowerCase(), userUtil);
			}

			String sql = "SELECT c.fname, c.mname, c.lname, c.phone,c.email, c.utype,"
					+ "d.corporation_name,d.corp_address,d.corp_city,d.corp_state,d.corp_zip,d.ibo_ein,d.principal_owner_name,"
					+ "d.principal_owner_title,d.principal_owner_email, s.state_code,c.region "
					+ "FROM VCSdb.tbl_users c "
					+ "join VCSdb.tbl_vs_uvc_users_details d ON d.uid = c.uid "
					+ "join VCSdb.tbl_state s ON s.state_id = d.corp_state "
					+ "where c.utype IN ('IBO','UIBO','RETENTION') and ibo_status = 0";

			jdbcTemplate.setDataSource(dataSourceVCS);
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

			List<Corporation> corpoWithOutBank = new ArrayList<Corporation>();
			int cant = 0, cantBi = 0;

			writer.append("First Name, Last Name,Email,");
			writer.append('\n');

			Map<String, Corporation> listCorporation = new HashMap<String, Corporation>();

			for (Map<String, Object> map : list) {
				String fullName = ((String) map.get("fname")).trim() + " "
						+ ((String) map.get("lname")).trim();

				UserUtil userAd = emailName.get(((String) map.get("email"))
						.toLowerCase());

				if (userAd == null) {
					writer.append((String) map.get("fname"));
					writer.append(',');
					writer.append((String) map.get("lname"));
					writer.append(',');
					writer.append((String) map.get("email"));
					writer.append(',');
					writer.append('\n');
					System.out.println((String) map.get("email"));
				} else {
					User user = new User();
					user.setName(userAd.getUserName());
					user.setFirstName(userAd.getFirstName());
					user.setMiddleName(userAd.getMiddleName());
					user.setLastName(userAd.getLastName());
					user.setEmail(userAd.getEmail());
					user.setType(new UserType("2"));
					user.setPhone((String) map.get("phone"));
					user.setCreatedDate(new Date());
					user.setUserCreated(new User("1"));
					user.setNeedUpdate(true);

					user = userService.makePersistent(user);

					// search if is one of the number predefine
					Profile profile = new Profile();
					// profile.setNumber(users.get(fullName));
					profile.setRegion((String) map.get("region"));
					profile.setIboState(new IboState("5"));
					profile.setCreatedDate(new Date());
					profile.setUserCreated(new User("1"));
					profile.setUser(user);

					String ein = ((String) map.get("ibo_ein")).replaceAll("-",
							"");
					if (ein.equals("0"))
						ein = "000000000";

					Corporation corporation = listCorporation.get(ein);

					if (corporation == null) {
						profile.setOwnerType(OwnerType.PRINCIPAL);
						corporation = new Corporation();
						corporation.setName((String) map
								.get("corporation_name"));
						// profile.setPrincOwner((String) map
						// .get("principal_owner_name"));
						// profile.setPrincEmail((String) map
						// .get("principal_owner_email"));
						profile.setPrincTitle((String) map
								.get("principal_owner_title"));
						corporation.setEin(ein);

						corporation.setUserCreated(new User("1"));
						corporation.setCreatedDate(new Date());

						Address address = new Address();
						address.setCity((String) map.get("corp_city"));
						address.setCountry(new Country(
								"52f03556-162b-4963-8ea1-62fe2c373ede"));
						States states = statesService
								.getStateByAbbreviation((String) map
										.get("state_code"));
						address.setStates(states);
						address.setDescription((String) map.get("corp_address"));
						address.setZipCode((String) map.get("corp_zip"));
						address.setCreatedDate(new Date());
						address.setUserCreated(new User("1"));
						address.setCorporation(corporation);
						// address.setProfile(profile);

						List<Address> addresses = new ArrayList<Address>();
						addresses.add(address);

						corporation.setAddress(addresses);

						corporation = corporationService
								.makePersistent(corporation);

						listCorporation.put(corporation.getEin(), corporation);

						BankInformation bi = bank.get(corporation.getEin());
						if (bi != null) {
							// System.out.println(profile.toString());
							// System.out.println(bi.getRouting() + "-"
							// + bi.getNumber());
							bi.setCorporation(corporation);
							bankInformationService.makePersistent(bi);
							cantBi++;
						} else {
							corpoWithOutBank.add(corporation);
						}
					} else {
						profile.setOwnerType(OwnerType.SECONDARY);
					}

					profile.setCorporation(corporation);

					String iboType = "";
					switch ((String) map.get("utype")) {
					case "IBO":
						iboType = "1";
						break;
					case "UIBO":
						iboType = "3";
						break;
					case "RETENTION":
						iboType = "2";
						break;
					}
					profile.setType(new IboType(iboType));

					// List<IbosClientApplications> caInser = new
					// ArrayList<IbosClientApplications>();
					//
					// IbosClientApplications ica = new
					// IbosClientApplications();
					// ica.setClientApplication(new ClientApplication(
					// "361c6433-930a-4234-9d85-3e188f340859"));
					// ica.setUser(profile);
					//
					// IbosClientApplications ica2 = new
					// IbosClientApplications();
					// ica2.setClientApplication(new ClientApplication(
					// "fbd9bf1d-255e-4637-a7f3-cdb60cd2b19b"));
					// ica2.setUser(profile);
					//
					// caInser.add(ica);
					// caInser.add(ica2);
					//
					// profile.setClientApplications(caInser);

					profileService.makePersistent(profile);

					cant++;
				}

			}
			
			writer.append('\n');
			writer.append('\n');			
			writer.append("Corporation without Bank Account Information");
			writer.append('\n');
			for (Corporation c : corpoWithOutBank) {
			
				writer.append(c.getName());
				writer.append(',');
				writer.append(c.getEin());
				writer.append('\n');
			}
			
			writer.append('\n');
			writer.append('\n');	

			writer.append("Total IBO inserted: " + cant
					+ " Total Bank inserted: " + cantBi + " Total VCS: "
					+ list.size() + " Total AD: " + emailName.size()
					+ " Total DD: " + bank.size());

			System.out.println("IBO:" + cant);
			System.out.println("Bank:" + cant);

		} catch (Exception e) {
			System.out.println(e.getMessage());

		}

	}
}
