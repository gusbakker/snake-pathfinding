public class Main {

	public static void main(String[] args) {

		Interface interfac = new Interface();
		FileManager fileManager = new FileManager();
		interfac.setFileManager(fileManager);
		interfac.getBoard().setFileManager(fileManager);
		interfac.init();

	}

}