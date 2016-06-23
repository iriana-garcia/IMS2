package com.ghw.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ghw.model.ConfigurationGeneral;
import com.ghw.model.TypeContract;

public class IboUtil {

	public static TypeContract getTypeContract(String distinguishedName,
			ConfigurationGeneral conf) {

		List<String> dns = getAllDN(conf.getLdapDnIdc());
		for (String s : dns) {
			if (distinguishedName.toLowerCase().contains(s.toLowerCase()))
				return TypeContract.INT_INDEP_CONTRACT;
		}

		dns = getAllDN(conf.getLdapDnInternational());
		for (String s : dns) {
			if (distinguishedName.toLowerCase().contains(s.toLowerCase()))
				return TypeContract.INTERNATIONAL;
		}

		return TypeContract.DOMESTIC;

	}

	public static List<String> getAllDN(String dn) {

		List<String> dns = new ArrayList<String>();

		if (StringUtils.isNotBlank(dn)) {
			String[] separateDn = dn.split(";");

			dns.addAll(Arrays.asList(separateDn));
		}

		return dns;
	}

	public static boolean isDNBelongIbo(String distinguishedName,
			ConfigurationGeneral conf) {

		List<String> dns = getAllDN(conf.getLdapDnIdc());
		dns.addAll(getAllDN(conf.getLdapDnIbo()));
		dns.addAll(getAllDN(conf.getLdapDnInternational()));

		for (String s : dns) {
			if (distinguishedName.toLowerCase().contains(s.toLowerCase()))
				return true;
		}

		return false;
	}

	public static boolean isDNBelongPa(String distinguishedName,
			ConfigurationGeneral conf) {

		List<String> dns = getAllDN(conf.getLdapDnSme());

		for (String s : dns) {
			if (distinguishedName.toLowerCase().contains(s.toLowerCase()))
				return true;
		}

		return false;

	}
}
