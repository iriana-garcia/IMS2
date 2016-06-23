package com.ghw.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ghw.dao.LogSystemDAO;
import com.ghw.model.AgentStateDetail;
import com.ghw.model.Event;
import com.ghw.model.LogSystem;
import com.ghw.model.Schedule;
import com.ghw.model.SkillPhoneSystem;
import com.ghw.model.User;

@Component
public class RestUtil {

	RestTemplate restTemplate = new RestTemplate();

	public final static String urlEvent = "/api/events/";

	public final static String urlSchedule = "/api/schedule/";

	public final static String urlSkillsPhoneSystem = "/api/skillsPhoneSystem/";

	public final static String urlAgentStateDetail = "/api/agentstatedetail/";

	public final static String urlAgentStateDetailUpdate = "/api/agentstatedetail/updated/";

	private final static String user = "imsadmin";

	private final static String password = "IM48S6!daT09855gr3";

	private SimpleDateFormat formatterAgentState = new SimpleDateFormat(
			"MM-dd-yyyy HH:mm:ss");

	private SimpleDateFormat formatterSchedule = new SimpleDateFormat(
			"MM-dd-yyyy HH:mm");

	@Autowired
	private LogSystemDAO logSystemDAO;

	public boolean isRunning(String url) {
		Boolean isRunning = false;
		try {

			HttpClient httpclient = new HttpClient();
			GetMethod getmethod = new GetMethod(url);
			int rCode = httpclient.executeMethod(getmethod);
			isRunning = rCode == HttpStatus.SC_OK;

		} catch (Exception e) {
			String errorMessage = Context.getUIMsg(e.getMessage());
			// insert logg system like an error
			LogSystem log = new LogSystem();
			log.setClassName("GDWConnect");
			log.setDetail("Error: "
					+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
							: errorMessage));
			log.setMethod("isRunning");
			log.setUser(null);
			log.setError(true);
			log.setIp(IpServer.ipServer());

			logSystemDAO.makePersistent(log);

		}

		return isRunning;
	}

	public String isRunningWebService(String url) {

		try {

			HttpClient httpclient = new HttpClient();
			GetMethod getmethod = new GetMethod(url);
			int rCode = httpclient.executeMethod(getmethod);
			return rCode == HttpStatus.SC_OK ? null : "HTTP Code: " + rCode;

		} catch (HttpClientErrorException e) {
			return "HTTP Code: " + e.getStatusCode().value()
					+ " HTTP Message: " + e.getStatusCode().name();

		} catch (Exception e) {
			return e.getMessage();

		}
	}

	public String getToken(String url) {
		try {

			String object = restTemplate
					.getForObject(
							url
									+ "/oauth/token?grant_type=password&client_id=restapp&client_secret=restapp&password="
									+ password + "&username=" + user,
							String.class);

			JSONParser parser = new JSONParser();
			Object oJson = parser.parse(object);

			JSONObject jsonUser = (JSONObject) oJson;

			return (String) jsonUser.get("access_token");
		} catch (Exception e) {

			String errorMessage = Context.getUIMsg(e.getMessage());
			// insert logg system like an error
			LogSystem log = new LogSystem();
			log.setClassName("GDWConnect");
			log.setDetail("Error: "
					+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
							: errorMessage));
			log.setMethod("getToken");
			log.setUser(null);
			log.setError(true);

			logSystemDAO.makePersistent(log);

		}
		return null;
	}

	public String getBody(String token, String url, String urlEvent) {

		String body = getBodyF(token, url, urlEvent);
		// first validate if the token is expired
		if (StringUtils.isNotBlank(body) && body.equals("401")) {
			token = getToken(url);
			body = getBodyF(token, url, urlEvent);
		}

		return body;
	}

	private String getBodyF(String token, String url, String urlEvent) {
		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + token);

			HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
			ResponseEntity<String> response = restTemplate.exchange(url
					+ urlEvent, HttpMethod.GET, httpEntity, String.class);

			return response.getBody();

		} catch (Exception e) {

			if (e instanceof HttpClientErrorException
					&& ((HttpClientErrorException) e).getStatusCode().value() == HttpStatus.SC_UNAUTHORIZED)
				return "401";

			String errorMessage = Context.getUIMsg(e.getMessage());
			// insert logg system like an error
			LogSystem log = new LogSystem();
			log.setClassName("GDWConnect");
			log.setDetail("Error: "
					+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
							: errorMessage));
			log.setMethod("getBody");
			log.setUser(null);
			log.setError(true);

			logSystemDAO.makePersistent(log);

		}
		return null;
	}

	public List<Event> transformToEvent(String body) throws ParseException {
		try {
			JSONParser parser = new JSONParser();
			Object o = parser.parse(body);

			JSONArray jsonObject = (JSONArray) o;
			Iterator it = jsonObject.iterator();

			List<Event> events = new ArrayList<Event>();

			while (it.hasNext()) {
				JSONObject json = (JSONObject) it.next();

				Event event = new Event();
				event.setId((String) json.get("id"));
				event.setName((String) json.get("name"));
				event.setIdPipkins((String) json.get("idPipkins"));

				events.add(event);

			}

			return events;
		} catch (Exception e) {

			String errorMessage = Context.getUIMsg(e.getMessage());
			// insert logg system like an error
			LogSystem log = new LogSystem();
			log.setClassName("GDWConnect");
			log.setDetail("Error: "
					+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
							: errorMessage));
			log.setMethod("transformToEvent");
			log.setUser(null);
			log.setError(true);

			logSystemDAO.makePersistent(log);

		}
		return null;
	}

	public List<Schedule> transformToSchedule(String body)
			throws ParseException {
		try {
			JSONParser parser = new JSONParser();
			Object o = parser.parse(body);

			JSONArray jsonObject = (JSONArray) o;
			Iterator it = jsonObject.iterator();

			List<Schedule> schedules = new ArrayList<Schedule>();

			while (it.hasNext()) {
				JSONObject json = (JSONObject) it.next();

				Schedule schedule = new Schedule();
				schedule.setId((String) json.get("id"));
				schedule.setDateEnd(formatterSchedule.parse((String) json
						.get("endDate")));
				schedule.setDateStart(formatterSchedule.parse((String) json
						.get("startDate")));
				schedule.setEvent(new Event((String) json.get("eventId")));
				schedule.setUser(new User((String) json.get("imsId")));
				schedules.add(schedule);

			}

			return schedules;

		} catch (Exception e) {

			String errorMessage = Context.getUIMsg(e.getMessage());
			// insert logg system like an error
			LogSystem log = new LogSystem();
			log.setClassName("GDWConnect");
			log.setDetail("Error: "
					+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
							: errorMessage));
			log.setMethod("transformToSchedule");
			log.setUser(null);
			log.setError(true);

			logSystemDAO.makePersistent(log);

		}
		return null;
	}

	public List<SkillPhoneSystem> transformToSkill(String body)
			throws ParseException {
		try {
			JSONParser parser = new JSONParser();
			Object o = parser.parse(body);

			JSONArray jsonObject = (JSONArray) o;
			Iterator it = jsonObject.iterator();

			List<SkillPhoneSystem> skills = new ArrayList<SkillPhoneSystem>();

			while (it.hasNext()) {
				JSONObject json = (JSONObject) it.next();

				SkillPhoneSystem skill = new SkillPhoneSystem();
				skill.setId((String) json.get("id"));
				skill.setName((String) json.get("name"));
				skill.setState((Boolean) json.get("state"));
				skill.setPlace((String) json.get("place"));
				skill.setPhoneSystemId(((Long) json.get("phoneSystemId"))
						.intValue());
				skill.setType(((Long) json.get("type")).shortValue());

				// if (json.get("cliId") != null)
				// skill.setSkill(new Skill((String) json.get("cliId")));

				skills.add(skill);

			}

			return skills;
		} catch (Exception e) {

			String errorMessage = Context.getUIMsg(e.getMessage());
			// insert logg system like an error
			LogSystem log = new LogSystem();
			log.setClassName("GDWConnect");
			log.setDetail("Error: "
					+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
							: errorMessage));
			log.setMethod("transformToSkill");
			log.setUser(null);
			log.setError(true);

			logSystemDAO.makePersistent(log);

		}
		return null;
	}

	public List<AgentStateDetail> transformToAgentStateDetail(String body)
			throws ParseException {
		try {
			JSONParser parser = new JSONParser();
			Object o = parser.parse(body);

			JSONArray jsonObject = (JSONArray) o;
			Iterator it = jsonObject.iterator();

			List<AgentStateDetail> agentStateDetails = new ArrayList<AgentStateDetail>();

			while (it.hasNext()) {
				JSONObject json = (JSONObject) it.next();

				AgentStateDetail asd = new AgentStateDetail();
				asd.setId((String) json.get("id"));
				asd.setStartDate(formatterAgentState.parse((String) json
						.get("dateStart")));
				asd.setEndDate(formatterAgentState.parse((String) json
						.get("dateEnd")));
				asd.setPlace(((Long) json.get("place")).shortValue());
				asd.setEventType(((Long) json.get("eventType")).intValue());
				asd.setReasonCode(((Long) json.get("reasonCode")).intValue());
				asd.setReasonCodeDescription((String) json
						.get("reasonCodeDescription"));
				asd.setDuration(((Long) json.get("duration")).intValue());
				asd.setNeedUpdated((Boolean) json.get("needUpdated"));
				asd.setUser(new User((String) json.get("imsId")));

				agentStateDetails.add(asd);

			}

			return agentStateDetails;
		} catch (Exception e) {

			String errorMessage = Context.getUIMsg(e.getMessage());
			// insert logg system like an error
			LogSystem log = new LogSystem();
			log.setClassName("GDWConnect");
			log.setDetail("Error: "
					+ (StringUtils.isBlank(errorMessage) ? e.getMessage()
							: errorMessage));
			log.setMethod("transformToAgentStateDetail");
			log.setUser(null);
			log.setError(true);

			logSystemDAO.makePersistent(log);

		}
		return null;
	}

	public static void main(String[] args) {
		RestUtil restUtil = new RestUtil();
		System.out.println(restUtil
				.getToken("https://qadata.greatcloudworks.com"));
		;
	}
}
