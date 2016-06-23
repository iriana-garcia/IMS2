package test;

public class MyTra implements Runnable {

	String name;

	public MyTra(String name) {
		this.name = name;
	}

	public void run() {
		System.out.println(name + " comenzando");
		try {

			for (int i = 0; i < 10; i++) {
				//Thread.sleep(400);

				System.out.println("In " + name + " count is " + i);
			}

		} catch (Exception e) {
			System.out.println("error");
		}

		System.out.println(name + " termino");

	}

}
