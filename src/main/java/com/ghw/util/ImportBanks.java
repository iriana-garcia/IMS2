package com.ghw.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ghw.model.Bank;
import com.ghw.model.RoutingNumbers;
import com.ghw.service.BankService;
import com.ghw.service.RoutingNumbersService;

public class ImportBanks {

	public static void main(String[] args) {

		try {

			ApplicationContext context = new ClassPathXmlApplicationContext(
					"bank-servlet.xml");

			BankService bankService = (BankService) context
					.getBean("bankService");

			RoutingNumbersService routingNumbersService = (RoutingNumbersService) context
					.getBean("routingNumbersService");

			String fedACHdirFile = "C:\\Users\\ifernandez\\Documents\\MY DOCUMENTS\\IMS\\Oracle\\Bank Import\\FedACHdir.txt";
			String fpddirFile = "C:\\Users\\ifernandez\\Documents\\MY DOCUMENTS\\IMS\\Oracle\\Bank Import\\fpddir.txt";

			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";

			Map<String, Bank> banks = new HashMap<String, Bank>();
			Map<String, RoutingNumbers> rnumbers = new HashMap<String, RoutingNumbers>();

			// get all the banks
			List<Bank> banksExisting = bankService.getData();
			for (Bank bank : banksExisting) {
				banks.put(bank.getName(), bank);
			}
			List<RoutingNumbers> routingExistinf = routingNumbersService
					.getData();
			for (RoutingNumbers routingNumbers : routingExistinf) {
				rnumbers.put(routingNumbers.getNumber(), routingNumbers);
			}

			List<String> routingRepeated = new ArrayList<String>();

			br = new BufferedReader(new FileReader(fpddirFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String routingNummber = line.substring(0, 9);
				String bankName = ImportBanks.convertBankName(line.substring(
						27, 63));

				Bank bank = banks.get(bankName);
				// first insert the bank if not exists
				if (bank == null) {

					bank = new Bank();
					bank.setName(bankName);
					bank = bankService.makePersistent(bank);
					banks.put(bankName, bank);
				}

				System.out.println(bank.getId() + " " + bank.getName());

				// add the routing number
				String rn = routingNummber;
				RoutingNumbers routingNumbers = rnumbers.get(rn);
				if (routingNumbers == null) {
					routingNumbers = new RoutingNumbers();
					routingNumbers.setBank(bank);
					routingNumbers.setNeedUpdate(true);
					routingNumbers.setNumber(rn);

					System.out.println(bank.getId() + " " + bank.getName());

					routingNumbersService.makePersistent(routingNumbers);

					rnumbers.put(rn, routingNumbers);
				} else
					routingRepeated.add(rn);
			}

			// String newRouting = "000000000";

			// List<String> routingRepeated = new ArrayList<String>();
			//
			// br = new BufferedReader(new FileReader(fedACHdirFile));
			// while ((line = br.readLine()) != null) {
			//
			// // use comma as separator
			// String routingNummber = line.substring(0, 9);
			// String routingNummberNew = line.substring(26, 35);
			// String bankName = ImportBanks.convertBankName(line.substring(
			// 35, 70));
			//
			// Bank bank = banks.get(bankName);
			// // first insert the bank if not exists
			// if (bank == null) {
			//
			// bank = new Bank();
			// bank.setName(bankName);
			// bank = bankService.makePersistent(bank);
			// banks.put(bankName, bank);
			// }
			//
			// System.out.println(bank.getId() + " " + bank.getName());
			//
			// // add the routing number
			// String rn = routingNummberNew.equals(newRouting) ? routingNummber
			// : routingNummberNew;
			// RoutingNumbers routingNumbers = rnumbers.get(rn);
			// if (routingNumbers == null) {
			// routingNumbers = new RoutingNumbers();
			// routingNumbers.setBank(bank);
			// routingNumbers.setNeedUpdate(true);
			// routingNumbers.setNumber(rn);
			//
			// System.out.println(bank.getId() + " " + bank.getName());
			//
			// routingNumbersService.makePersistent(routingNumbers);
			//
			// rnumbers.put(rn, routingNumbers);
			// } else
			// routingRepeated.add(rn);
			// }

			System.out.println(routingRepeated);
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}

	}

	public static String convertBankName(String bank) {

		bank = StringUtils.lowerCase(bank).trim();
		String finalBank = "";

		String[] separate = bank.split(" ");
		for (String a : separate) {
			finalBank += StringUtils.capitalize(a) + " ";
		}

		return finalBank;
	}
}
