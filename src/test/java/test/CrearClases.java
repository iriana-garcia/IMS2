package test;

import java.io.File;

public class CrearClases {
	
	public static void main(String[] args) {
		
		CrearClases.crearArbolService();
	}

	public static void crearArbolService() {
		try {

			String pathService = "D:\\workspace\\rehab\\src\\main\\java\\com\\iriana\\service\\";
			String pathServiceImpl = "D:\\workspace\\rehab\\src\\main\\java\\com\\iriana\\service\\impl\\";
			
			String pathDao = "D:\\workspace\\rehab\\src\\main\\java\\com\\iriana\\dao\\";
			String pathDaoImpl = "D:\\workspace\\rehab\\src\\main\\java\\com\\iriana\\dao\\impl\\";


			// Obtengo todos los nombres de mis clases model
			String pathModel = "D:\\workspace\\rehab\\src\\main\\java\\com\\iriana\\model";
			File folder = new File(pathModel);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					String name = listOfFiles[i].getName().replaceAll(".java", "");

					System.out.println("File " + name);

					File file = new File(pathService + name + "Service.java");
					file.createNewFile();
					
					File file2 = new File(pathServiceImpl + name + "ServiceImpl.java");
					file2.createNewFile();
					
					File file3 = new File(pathDao + name + "Dao.java");
					file3.createNewFile();
					
					File file4 = new File(pathDaoImpl + name + "DaoImpl.java");
					file4.createNewFile();

				} else if (listOfFiles[i].isDirectory()) {
					System.out.println("Directory " + listOfFiles[i].getName());
				}
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

}
