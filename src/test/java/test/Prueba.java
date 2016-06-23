package test;

public class Prueba {

	public static void main(String[] args) {

		String[] prueba = new String[3];
		prueba[0] = " http://sadasd.sdfsdf/  sdfsdfsadf http://aaa.com/";
		prueba[1] = " sdasdasdasd sadasdasdasdhttp://otro.sdf.com/asdasd/asdasd  sdfsdfsadf";
		prueba[2] = " sdasdasdasd sadasdasdasd://otro.sdf.comasdasdasdasd  sdfsdfsadf http://sd125as.final/dfsdfdf";

		Prueba.printDomains(prueba);

	}

	static void printDomains(String[] arr) {

		if (arr.length < 1 || arr.length > 1700)
			System.out.println("Too long html");
		else {

			String validDomain = "";
			String buscaInicio = "http://";
			String buscaTerminacion = "/";

			for (String string : arr) {
				// valido si contiene http://

				if (string.contains(buscaInicio)) {

					String eliminado = string;
					while (eliminado.contains(buscaInicio)) {

						// busco la primera aparicion de busca
						int indexPrimero = eliminado.indexOf(buscaInicio);

						int indexFinal = eliminado.indexOf(buscaTerminacion, indexPrimero + 7);

					
						if (indexFinal < 0 && eliminado.length() > indexPrimero + 8)
							validDomain += ";" + eliminado.substring(indexPrimero + 7, eliminado.length());

						if (indexFinal < 0)
							break;

						validDomain += ";" + eliminado.substring(indexPrimero + 7, indexFinal);

						eliminado = eliminado.substring(indexFinal, eliminado.length());

					}

				}

			}

			System.out.println(validDomain.length() > 0 ? validDomain.substring(1, validDomain.length()) : "");
		}

	}

}
