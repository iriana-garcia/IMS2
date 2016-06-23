package test;

import org.apache.commons.httpclient.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ghw.model.IboState;
import com.ghw.service.IboStateService;

public class Main {

	private final static String user = "imsadmin";

	private final static String password = "IM48S6!daT09855gr3";

	RestTemplate restTemplate = new RestTemplate();

	public String getToken(String url) throws ParseException {
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

			System.out.println(e.getMessage());

		}
		return null;
	}

	public String getBody(String token, String url, String urlEvent)
			throws ParseException {
		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + token);

			HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
			ResponseEntity<String> response = restTemplate.exchange(url
					+ urlEvent, HttpMethod.GET, httpEntity, String.class);

			return response.getBody();

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == HttpStatus.SC_UNAUTHORIZED) {
				String newToken = getToken(url);
				return getBody(newToken, url, urlEvent);
			}
		} catch (Exception e) {

			System.out.println(e.getMessage());

		}
		return null;
	}

	public static void main(String[] args) {
		// System.out.println("Main start");
		try {

			// Double a = 500.00;
			// Double b = 100.0;
			//
			// double value = a / b;
			// double fractionalPart = value % 1;
			// double integralPart = value - fractionalPart;
			//
			// System.out.println(integralPart + (fractionalPart > 0 ? 1 : 0));

			ApplicationContext context = new ClassPathXmlApplicationContext(
					"bank-servlet.xml");

			IboStateService iboStateService = (IboStateService) context
					.getBean("iboStateService");

			IboState iboState = iboStateService.getById("4");

			iboState.setName("Ok mod");

			iboStateService.update(iboState);

			//
			// AgentStateDetail asd = agentStateDetailService
			// .getById("aae79268-9604-48ad-909b-5000f1f708d5");
			//
			// AgentStateDetail work = agentStateDetailService
			// .getWorkAfterTalking(
			// "358a44a0-3396-436d-94c6-ab7ba32eee3a", asd);
			//
			// System.out.println(work.getId());

			// GDWConnectService gdwConnectService = (GDWConnectService) context
			// .getBean("gdwConnectService");
			// gdwConnectService.processScheduleOnlyTest();

			// UserService userService = (UserService) context
			// .getBean("userService");
			//
			// User user = userService.loadAllByIdToEdit(new User(
			// "0c860a7d-aca4-4a83-9bd3-dbd24e70230b"));
			//
			// user.setLastLogin(new Date());
			//
			// userService.updateLastLogin(user);

			// Date dateIni = new Date();
			// User user = userService.loadAllByIdToEdit(new User(
			// "0c860a7d-aca4-4a83-9bd3-dbd24e70230b"));
			//
			//
			//
			// System.out
			// .println((new Date().getTime() - dateIni.getTime()) );
			//
			// dateIni = new Date();
			//
			// user = userService.loadAllByIdToEdit2(new User(
			// "0c860a7d-aca4-4a83-9bd3-dbd24e70230b"));
			//
			// System.out
			// .println((new Date().getTime() - dateIni.getTime()) );
			//
			// BankInformationService bankInformationService =
			// (BankInformationService) context
			// .getBean("bankInformationService");
			//
			// RoutingNumbersService routingNumbersService =
			// (RoutingNumbersService) context
			// .getBean("routingNumbersService");
			//
			// List<BankInformation> bankInformations = bankInformationService
			// .getData();
			// List<RoutingNumbers> routingNumbers = routingNumbersService
			// .getData();
			// Set<String> rnum = new LinkedHashSet<String>();
			// for (RoutingNumbers rn : routingNumbers) {
			// rnum.add(rn.getNumber());
			// }
			//
			// for (BankInformation bankInformation : bankInformations) {
			//
			// if (!rnum.contains(bankInformation.getRouting()))
			// System.out.println("Not found "
			// + bankInformation.getRouting());
			// }
			//
			// System.out.println("Finish");

			// Main main = new Main();
			// String body = main.getBody("asdasd",
			// "http://gvwiboimsqa:8080/greatdataworks",
			// "/api/skills/");
			//
			//
			// System.out.println("salio " +body);

			//

			// //
			// FTPUtil ftpUtil = new FTPUtil();
			//
			// ConfigurationGeneral cg = new ConfigurationGeneral();
			// cg.setPathOracle("GHWHTTP2");
			// cg.setPortOracle("990");
			// cg.setUserOracle("ghwfinance");
			// cg.setPasswordOracle("t3exupHabr");
			//
			// ftpUtil.uploadFTPFile(
			// "03012016185223",
			// Paths.get("C:\\Users\\ifernandez\\Documents\\OracleFolder\\03012016185223"),
			// cg);

			// String dateStart = "03/20/2016 08:29:58";
			// SimpleDateFormat format = new SimpleDateFormat(
			// "MM/dd/yyyy HH:mm:ss");
			//
			// Date d1 = format.parse(dateStart);
			//
			// Date date = new Date();
			//
			// long diff = date.getTime() - d1.getTime();
			//
			// long diffDays = diff / (24 * 60 * 60 * 1000);
			//
			// System.out.print(diffDays + " days, ");

			// C:\Users\ifernandez\Documents\OracleFolder
			// AccessController.checkPermission(new
			// FilePermission("\\Ghwlaptop6910\\oraclefolder", "read,write"));
			//
			// System.out.println(Files.isWritable(new File(
			// "\\Ghwlaptop6910\\OracleFolder").toPath()));

			// Get the actual sunday
			// Calendar sunday = Calendar.getInstance();
			// sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			// sunday.set(Calendar.HOUR_OF_DAY, 17);
			// sunday.set(Calendar.MINUTE, 0);
			// sunday.set(Calendar.SECOND, 0);
			//
			// Calendar wed = Calendar.getInstance();
			// wed.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
			// wed.set(Calendar.HOUR_OF_DAY, 0);
			// wed.set(Calendar.MINUTE, 0);
			// wed.set(Calendar.SECOND, 0);
			//
			//
			// Calendar actual = Calendar.getInstance();
			//
			// System.out.println((new SimpleDateFormat("E, M-d-yy h:mm a"))
			// .format(sunday.getTime()));
			//
			// System.out.println((new SimpleDateFormat("E, M-d-yy h:mm a"))
			// .format(wed.getTime()));
			//
			// System.out.println((new SimpleDateFormat("E, M-d-yy h:mm a"))
			// .format(actual.getTime()));
			//
			// System.out.println(actual.after(sunday) && actual.before(wed));
			//
			// Integer sec = 566;
			// System.out.println(new Double(sec) / (60 * 60));
			// System.out.println(MathUtils.round(new Double(sec) / (60 * 60),
			// 2));

			// Path source =
			// Paths.get("C:\\Users\\ifernandez\\Downloads\\Test1");
			//
			// Path dest =
			// Paths.get("C:\\Users\\ifernandez\\Documents\\Test\\Test1");
			//
			// DirUtils.copy(source, dest);

			// Path path = Paths.get("C:/Users/ifernandez/Documents/Test/1");
			// path = Files.createDirectories(path);

			// Path source = Paths.get("C:/Users/ifernandez/Downloads/Test1");
			// Path target = Paths.get("C:/Users/ifernandez/Documents/Test");
			//
			// Files.copy(source, target, StandardCopyOption.ATOMIC_MOVE);

			//
			// ApplicationContext context = new ClassPathXmlApplicationContext(
			// "spring-servlet.xml");
			// LogSystemService logSystemService = (LogSystemService) context
			// .getBean("logSystemService");

			// System.out.println(new String(
			// "Pipkins Workforce Management System will provide a data file of type to be determined and the IBO-IMS will produce 2 files to be picked up by finance for import into Oracle.  See Data Matrix for Data.").length());

			//
			// EmailService emailService = (EmailService) context
			// .getBean("emailService");
			// EmailServiceImpl emailService = new EmailServiceImpl();
			// emailService.sendMail("ifernandez@greathealthworks.com",
			// "ifernandez@greathealthworks.com", "prueba",
			// "gooooooooooooo");
			//
			// StatesService service = (StatesService) context
			// .getBean("statesService");
			//
			// String csvFile =
			// "C:\\Users\\ifernandez\\Downloads\\us_postal_codes.csv";
			//
			// BufferedReader br = null;
			// String line = "";
			// String cvsSplitBy = ",";
			//
			// ApplicationContext context = new ClassPathXmlApplicationContext(
			// "spring-servlet.xml");
			//
			// BankInformationService bankInformationService =
			// (BankInformationService) context
			// .getBean("bankInformationService");
			// List<BankInformation> listbank =
			// bankInformationService.getData();
			// for (BankInformation bi : listbank) {
			// if (bi.getRouting().equals("051000017")
			// && bi.getNumber().equals("435013357904"))
			// System.out.println(bi.getUser().getId());
			// }
			//
			// String[] array = new String[] { "20001", "20002", "20003",
			// "20004",
			// "20005", "20006", "20007", "20008", "20009", "20010",
			// "20011", "20012", "20013", "20015", "20016", "20017",
			// "20018", "20019", "20020", "20022", "20024", "20026",
			// "20027", "20030", "20032", "20035", "20036", "20037",
			// "20038", "20039", "20040", "20041", "20042", "20043",
			// "20044", "20045", "20046", "20049", "20050", "20052",
			// "20056", "20057", "20058", "20059", "20060", "20062",
			// "20064", "20065", "20066", "20068", "20070", "20071",
			// "20076", "20080", "20082", "20090", "20091", "20201",
			// "20202", "20204", "20206", "20207", "20210", "20212",
			// "20219", "20220", "20222", "20223", "20224", "20226",
			// "20228", "20229", "20230", "20233", "20237", "20240",
			// "20241", "20242", "20250", "20260", "20301", "20303",
			// "20310", "20314", "20317", "20318", "20319", "20330",
			// "20340", "20350", "20372", "20373", "20374", "20375",
			// "20376", "20380", "20388", "20390", "20391", "20392",
			// "20393", "20394", "20395", "20398", "20401", "20405",
			// "20407", "20408", "20410", "20413", "20415", "20416",
			// "20418", "20420", "20421", "20422", "20426", "20429",
			// "20431", "20433", "20435", "20436", "20439", "20447",
			// "20451", "20460", "20463", "20472", "20500", "20501",
			// "20502", "20503", "20505", "20506", "20507", "20508",
			// "20510", "20511", "20515", "20520", "20522", "20523",
			// "20525", "20526", "20527", "20528", "20529", "20530",
			// "20531", "20534", "20535", "20536", "20538", "20540",
			// "20543", "20544", "20546", "20547", "20548", "20549",
			// "20551", "20552", "20553", "20554", "20560", "20565",
			// "20566", "20571", "20572", "20577", "20579", "20580",
			// "20581", "20585", "20590", "20591", "20593" };
			//
			// List<String> list = Arrays.asList(array);

			// List<String> list = new ArrayList<String>();
			//
			// br = new BufferedReader(new FileReader(csvFile));
			// while ((line = br.readLine()) != null) {
			//
			// // use comma as separator
			// String[] country = line.split(cvsSplitBy);
			// //
			// // ZipCode zip = zipCodeService.getDataByCode((String)
			// // country[0]);
			// // if (zip == null)
			// // list.add((String) country[0]);
			// //
			// // if (list.contains((String) country[0])) {
			//
			// ZipCode zipCode = new ZipCode();
			// zipCode.setNumber((String) country[0]);
			//
			// String city = (String) country[1];
			// //city = StringUtils.lowerCase(city);
			//
			// // String finalCity = "";
			//
			// // String[] separate = city.split(" ");
			// // for (String a : separate) {
			// // finalCity += StringUtils.capitalize(a) + " ";
			// // }
			// zipCode.setCity(city);
			//
			// String state = (String) country[3];
			//
			// States states = statesService.getStateByAbbreviation(state);
			//
			// zipCode.setStates(states);
			//
			// try {
			//
			// if (states != null)
			// zipCodeService.makePersistent(zipCode);
			//
			// } catch (Exception e) {
			// System.out.println(e.getMessage());
			// }
			//
			// // }
			//
			// }
			//
			// System.out.println(list);

			//
			// User user = service.getById(1);
			// user.setPassword("Welcome01");
			//
			// service.update(user);

			// con.setLdapPass("Welcome01");
			// con.setUserUpdated(new User(1));
			//
			// service.update(con);

			// System.out.println(list.size());
			//
			// ConfigurationService configurationService =
			// (ConfigurationService) context
			// .getBean("configurationService");
			//
			// service.syncronizedUser(configurationService.getDataConfiguration());

			// System.out.println(list.size());

			// for (int i = 0; i < 100; i++) {
			//
			// LogSystem logSystem = new LogSystem();
			// logSystem.setClassName("Class nema");
			// logSystem.setDetail("Detaill " + i);
			// logSystem.setError(i % 2 > 0 ? true : false);
			// logSystem.setMethod("method " + i);
			// logSystem.setUser(new User(1));
			//
			// systemService.makePersistent(logSystem);
			//
			// }
			//
			// UserDao stateService = context.getBean(UserDao.class,
			// "userDao");
			//
			// System.out.println("User "+ stateService.getById(1));

			// System.out.println(StringUtils.uncapitalize("RolPremsi"));

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		// System.out.println("Main finally");

	}
}
