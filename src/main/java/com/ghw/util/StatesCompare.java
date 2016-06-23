package com.ghw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ghw.model.Country;
import com.ghw.model.States;
import com.ghw.service.StatesService;

public class StatesCompare {

	public static void main(String[] args) {
		try {

			ApplicationContext context = new ClassPathXmlApplicationContext(
					"spring-servlet.xml");

			JdbcTemplate jdbcTemplate = (JdbcTemplate) context
					.getBean("jdbcTemplate");

			StatesService statesService = (StatesService) context
					.getBean("statesService");

			String sql = "select * from VCSdb.tbl_state";

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

			List<States> statesVcs = new ArrayList<States>();

			int cant = 0;
			for (Map<String, Object> map : list) {
				States s = new States();
				s.setCountry(new Country("52f03556-162b-4963-8ea1-62fe2c373ede"));
				s.setName((String) map.get("state"));
				s.setAbbreviation((String) map.get("state_code"));

				statesService.makePersistent(s);

				statesVcs.add(s);
			}

			System.out.println(cant);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
