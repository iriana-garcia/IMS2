package com.ghw.util;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.ghw.model.BankInformation;
import com.ghw.model.Corporation;
import com.ghw.service.BankInformationService;
import com.ghw.service.CorporationService;
import com.ghw.service.ProfileService;
import com.ghw.service.StatesService;
import com.ghw.service.UserService;

public class ProcessBank {

	public static void main(String[] args) {
		try (FileWriter writer = new FileWriter(
				"C:\\Users\\ifernandez\\Documents\\MY DOCUMENTS\\IMS\\BANK_IMPORT.csv")) {

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
					.queryForList("SELECT email,dd_routing_nbr,dd_bank_acct_nbr,dd_tax_id, dd_company_name "
							+ "FROM dd_user "
							+ "where email is not null and dd_tax_id is not null and dd_routing_nbr is not null and dd_bank_acct_nbr is not null");

			Map<String, BankInformation> bank = new HashMap<String, BankInformation>();

			for (Map<String, Object> map : listDD) {
				String email = (String) map.get("email");
				String ein = (String) map.get("dd_tax_id");
				if (StringUtils.isNotBlank(email)
						&& StringUtils.isNotBlank(ein)) {
					// System.out.println(ein);
					BankInformation bi = new BankInformation();
					bi.setRouting(stringEncryptor.decrypt((String) map
							.get("dd_routing_nbr")));
					bi.setNumber(stringEncryptor.decrypt((String) map
							.get("dd_bank_acct_nbr")));
					Corporation corporation = new Corporation("",
							(String) map.get("dd_company_name"));
					corporation.setEin(ein);
					bi.setCorporation(corporation);

					// String id = email.toLowerCase() + "-" + ein;
					String id = ein;

					bank.put(id, bi);
				}
			}

			List<Corporation> listCorporation = corporationService.loadAll();
			for (Corporation c : listCorporation) {

				BankInformation cDD = bank.get(c.getEin());

				writer.append(c.getName());
				writer.append(',');
				if (cDD != null)
					writer.append(cDD.getCorporation().getName());
				writer.append(',');

				writer.append(c.getEin());
				writer.append(',');
				if (cDD != null)
					writer.append(cDD.getCorporation().getEin());
				writer.append(',');

				if (c.getBank() != null)
					writer.append(c.getBank().getRouting());
				writer.append(',');
				if (cDD != null)
					writer.append(cDD.getRouting());
				writer.append(',');
				if (c.getBank() != null)
					writer.append(c.getBank().getNumber());
				writer.append(',');
				if (cDD != null)
					writer.append(cDD.getNumber());
				writer.append(',');

				writer.append('\n');
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());

		}

	}
}
