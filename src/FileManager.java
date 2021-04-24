import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;


public class FileManager {
	
	private File file;
	private BufferedImage image;
	private ArrayList<String> textMap = new ArrayList<String>();
	
	
	public FileManager() {
		//constructor
	}

	public void readByFileExtension() throws IOException{
		

		readImage();
		
		/*
		String filename = file.getName();
		String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

		String jpg = "jpg";
		String txt = "txt";
		
		
		if (extension.equals(jpg)) {
			readImage();
		}
		if (extension.equals(txt)) {
			readMap();
		}
		else {
			JOptionPane.showMessageDialog(null, "Choose another file type!");
		}*/
	}
	
	public void readImage() throws IOException {

		System.out.println("path!!!!!!!!!!!!: " + file.getPath());
		image = ImageIO.read(file);
		
	}

	public void readMap(){
		
		int nLinhas=0, nColunas=0;
		try {
			Scanner scanner = new Scanner(file);
		
			while(scanner.hasNext()){
				nLinhas++;
				String line = scanner.nextLine();
				if(line.length() > nColunas){
					nColunas = line.length();
				}
				textMap.add(line);
				System.out.println(line);

			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
		}
	}
	

	public File getFile() {
		return file;
	}


	public void setFile(File file) {
		this.file = file;
	}


	public ArrayList<String> getTextMap() {
		return textMap;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	
}
