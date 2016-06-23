package com.ghw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.lang3.StringUtils;

public class CrearClases {

	public static void main(String[] args) {

		CrearClases.crearArbolService();
	}

	private static StringBuffer createTextService(String clase) {

		StringBuffer s = new StringBuffer();

		String ln = System.getProperty("line.separator");
		s.append("package com.ghw.service;");
		s.append(ln);
		s.append("import com.ghw.model." + clase + ";");
		s.append(ln);
		s.append("	public interface " + clase + "Service extends Service<"
				+ clase + ", String> {}");

		return s;
	}

	private static StringBuffer createTextServiceImpl(String clase) {

		StringBuffer s = new StringBuffer();

		// String ln = System.getProperty("line.separator");
		s.append("package com.ghw.service.impl;import org.springframework.stereotype.Service;import org.springframework.beans.factory.annotation.Autowired;"
				+ "import org.springframework.transaction.annotation.Transactional;"
				+ "import com.ghw.dao."
				+ clase
				+ "DAO;"
				+ "import com.ghw.model."
				+ clase
				+ ";"
				+ "import com.ghw.service."
				+ clase
				+ "Service;"
				+ "@Service(\""
				+ StringUtils.uncapitalize(clase)
				+ "Service\")"
				+ "@Transactional(readOnly = false)"
				+ "public class "
				+ clase
				+ "ServiceImpl extends ServiceImpl<"
				+ clase
				+ ", String, "
				+ clase
				+ "DAO> "
				+ "implements "
				+ clase
				+ "Service {"
				+ "private  "
				+ clase
				+ "DAO dao;	@Autowired	public void setDAO( "
				+ clase
				+ "DAO dao) {	this.dao = dao;	super.setDao(dao);	}}");

		return s;
	}

	private static StringBuffer createTextDao(String clase) {

		StringBuffer s = new StringBuffer();

		// String ln = System.getProperty("line.separator");
		s.append("package com.ghw.dao;import com.ghw.model." + clase
				+ ";public interface " + clase + "DAO extends GenericDAO<"
				+ clase + ", String> {}");

		return s;
	}

	private static StringBuffer createTextDaoImpl(String clase) {

		StringBuffer s = new StringBuffer();

		// String ln = System.getProperty("line.separator");
		s.append("package com.ghw.dao.impl;import java.util.List;"
				+ "import org.hibernate.Criteria;"
				+ "import org.springframework.stereotype.Repository;"
				+ "import com.ghw.dao." + clase + "DAO;"
				+ "import com.ghw.filter.FilterBase;" + "import com.ghw.model."
				+ clase + ";" + "@Repository(\""
				+ StringUtils.uncapitalize(clase) + "DAO\")" + "public class "
				+ clase + "DAOImpl extends GenericHibernateDAO<" + clase
				+ ", String> implements " + "" + clase + "DAO { }");

		return s;
	}

	private static void createFile(String name, String path,
			String nameAddClase, StringBuffer content) throws IOException {

		System.out.println("File " + name);

		File file = new File(path + name + nameAddClase + ".java");
		boolean crea = file.createNewFile();
		if (crea) {
			Files.write(Paths.get(file.getPath()), content.toString()
					.getBytes(), StandardOpenOption.APPEND);
		}

	}

	public static void crearArbolService() {
		try {

			String pathService = "C:\\Users\\ifernandez\\Documents\\workspace\\ims\\src\\main\\java\\com\\ghw\\service\\";
			String pathServiceImpl = "C:\\Users\\ifernandez\\Documents\\workspace\\ims\\src\\main\\java\\com\\ghw\\service\\impl\\";

			String pathDao = "C:\\Users\\ifernandez\\Documents\\workspace\\ims\\src\\main\\java\\com\\ghw\\dao\\";
			String pathDaoImpl = "C:\\Users\\ifernandez\\Documents\\workspace\\ims\\src\\main\\java\\com\\ghw\\dao\\impl\\";

			// Obtengo todos los nombres de mis clases model
			String pathModel = "C:\\Users\\ifernandez\\Documents\\workspace\\ims\\src\\main\\java\\com\\ghw\\\\model";
			File folder = new File(pathModel);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					String name = listOfFiles[i].getName().replaceAll(".java",
							"");

					if (name.equalsIgnoreCase("SkillPhoneSystem")) {
						createFile(name, pathService, "Service",
								CrearClases.createTextService(name));

						createFile(name, pathServiceImpl, "ServiceImpl",
								CrearClases.createTextServiceImpl(name));

						createFile(name, pathDao, "DAO",
								CrearClases.createTextDao(name));

						createFile(name, pathDaoImpl, "DAOImpl",
								CrearClases.createTextDaoImpl(name));
					}

				} else if (listOfFiles[i].isDirectory()) {
					System.out.println("Directory " + listOfFiles[i].getName());
				}
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}
}
