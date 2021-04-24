import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

//import org.opencv.core.Core;


public class Board extends JComponent {

	private BufferedImage image;
	private BufferedImage staticImage;
	private boolean started = false;
	private FileManager fileManager;

	private ArrayList<Color> colors = new ArrayList<Color>();
	private ArrayList<Pen> pens = new ArrayList<Pen>();
	private Celula[][] map;

	private int espessura;
	private int linhas = 60;
	private int colunas = 60;

	private boolean grid;
	private Point selected;
	private int dx;
	private int dy;
	private int imageWidth;
	private int imageHeight;

	public Board() {
		super();
		espessura = 4;
		grid = false;
		criarCelulas();
		//loadImage();
		sort();
	}

	@Override
	public void paintComponent(Graphics g) {

	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		super.paint(g2);
		this.setBackground(Color.BLACK);

		if (started) {

			dx = getWidth() / colunas;
			dy = getHeight() / linhas;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			if (grid) {

				g2.setColor(Color.gray);
				// desenha linhas
				for (int i = 0; i < linhas; i++) {
					g2.drawLine(0, i * dy, getWidth(), i * dy);
				}
				// desenha colunas
				for (int i = 0; i < colunas; i++) {
					g2.drawLine(i * dx, 0, i * dx, getHeight());
				}

				// preencher celulas
				for (int i = 0; i < linhas; i++) {
					for (int j = 0; j < colunas; j++) {

						int x = (int) ((j % colunas) * dx) + 1;
						int y = (int) ((i % linhas) * dy) + 1;

						if (map[i][j].getColor() != null) {
							g2.setColor(map[i][j].getColor());
							g2.fillRect(x , y, dx-1 , dy -1);
						}
						
						
						if (map[i][j].isOcupado()) {
							g2.setColor(new Color(0, 0, 0, 0.25f));
							g2.fillRect(x + 1, y + 1, dx - 2, dy - 2);
						}
						if (map[i][j].isSelected()) {
							g2.setColor(Color.cyan);
							g2.fillRect(x + 1, y + 1, dx - 2, dy - 2);
						}
						if (map[i][j].isBloqueado()) {
							g2.setColor(Color.orange);
							g2.fillRect(x + 1, y + 1, dx - 2, dy - 2);
						}
						if (map[i][j].isFlag()) {
							g2.setColor(Color.red);
							g2.fillRect(x + 1, y + 1, dx - 2, dy - 2);
						}
						
					}
				}
			}

			// pens.get(0).getPontos().size()*pens.size());
			g2.setStroke(new BasicStroke(espessura, BasicStroke.CAP_SQUARE,
					BasicStroke.JOIN_ROUND));

			// texto
			g2.setFont(new Font("Arial", Font.BOLD, 20));
			g2.drawString("Gustavo Bakker", 10, 20);

			// desenhos
			for (int i = 0; i < pens.size(); i++) {

				g2.setColor(pens.get(i).getColor());

				for (int j = 0; j < pens.get(i).getPontos().size(); j++) {

					g2.drawLine(pens.get(i).getPontos().get(j).x, pens.get(i)
							.getPontos().get(j).y,
							pens.get(i).getPontos().get(j).x, pens.get(i)
									.getPontos().get(j).y);
				}
			}

			g2.drawImage(image, 0, 0, imageWidth, imageHeight, null);
		}
	}

	public void criarCelulas() {

		// ou updateCelulas() seria melhor para constantemente atualizar..
		map = new Celula[linhas][colunas];

		for (int i = 0; i < linhas; i++) {
			for (int j = 0; j < colunas; j++) {
				map[i][j] = new Celula(j, i);
			}
		}

	}

	public void buildMap() {

		try {

			BufferedImage img = fileManager.getImage();
			int w = img.getWidth();
			int h = img.getHeight();
			// Graphics2D g = img.createGraphics();

			if (w > getWidth() || h > getHeight()) {
				imageWidth = 400;
				imageHeight = 500;
			} else {
				imageWidth = w;
				imageHeight = h;
			}
			image = img;

			ArrayList<String> textMap = fileManager.getTextMap();
			System.out.println(textMap);
			for (int i = 0; i != textMap.size(); i++) {
				for (int j = 0; j != textMap.get(0).length(); j++) {

					if (textMap.get(i).charAt(j) == '#') {
						map[i][j].setBloqueado(true);
					}
					if (textMap.get(i).charAt(j) == '.') {
						map[i][j].setFlag(true);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Point convertToGrid(Point point) {

		int x = (int) (point.getX() / dx);
		int y = (int) (point.getY() / dy);

		return new Point(x, y);
	}

	public Point convertToPixel(Point point) {

		int x = (int) ((point.x % colunas) * dx);
		int y = (int) ((point.y % linhas) * dy);
		int a = (int) (x + dx / 2);
		int b = (int) (y + dy / 2);

		return new Point(a, b);
	}

	public void pixelate(int pixelSize) {

		// Get the raster data (array of pixels)
		Raster src = image.getData();

		// Create an identically-sized output raster
		WritableRaster dest = src.createCompatibleWritableRaster();

		// Loop through every PIX_SIZE pixels, in both x and y directions
		for (int y = 0; y < src.getHeight(); y += pixelSize) {
			for (int x = 0; x < src.getWidth(); x += pixelSize) {

				// Copy the pixel
				double[] pixel = new double[3];
				pixel = src.getPixel(x, y, pixel);

				// "Paste" the pixel onto the surrounding PIX_SIZE by PIX_SIZE
				// neighbors
				// Also make sure that our loop never goes outside the bounds of
				// the image
				for (int yd = y; (yd < y + pixelSize)&& (yd < dest.getHeight()); yd++) {
					for (int xd = x; (xd < x + pixelSize)&& (xd < dest.getWidth()); xd++) {
						dest.setPixel(xd, yd, pixel);
					}
				}
			}
		}

		// Save the raster back to the Image
		image.setData(dest);
		convertImageToCells();
	}

	public void convertImageToCells() {

		//corrigir: o conertTo esta a usar tamanho da Grid, e n�o do Raster size...
		
		int width = image.getWidth();
		int height = image.getHeight();
		Point p = convertToGrid(new Point(width,height));
		
		for (int row = 0; row < p.y; row++) {
			for (int col = 0; col < p.x; col++) {
				Point g = convertToPixel(new Point(col,row));
				int x = image.getRGB(g.x, g.y);
				int  red   = (x & 0x00ff0000) >> 16;
				int  green = (x & 0x0000ff00) >> 8;
				int  blue  =  x & 0x000000ff;
				map[row][col].setColor(new Color(red,green,blue));
			}
		}
		
		image = null;
	}
	
	public void loadImage(){
		
		try {
			//image = ImageIO.read(new File("amelie.jpg"));
			//image = thresholdImage(result, 90);
			
			image = fileManager.getImage();
			imageWidth = image.getWidth();
			imageHeight = image.getHeight();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void shuffleMatrix(Celula[][] matrix){
		
		//shuffle matriz inteira ou apenas determinadas celulas??...
		// Implementing Fisher�Yates shuffle 
		for (int row = 0; row < matrix.length; row++) {
			Celula[] ar = matrix[row];
			Random rnd = new Random();
		    for (int i = ar.length - 1; i > 0; i--){
		      int index = rnd.nextInt(i + 1);
		      Celula a = ar[index];
		      ar[index] = ar[i];
		      ar[i] = a;
		    }
		}
	}
	
	// ColorImage to BinaryImage with threshold in [0,255]
	public static BufferedImage thresholdImage(BufferedImage image, int threshold) {
	   
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
	    result.getGraphics().drawImage(image, 0, 0, null);
	    WritableRaster raster = result.getRaster();
	    int[] pixels = new int[image.getWidth()];
	    for (int y = 0; y < image.getHeight(); y++) {
	        raster.getPixels(0, y, image.getWidth(), 1, pixels);
	        for (int i = 0; i < pixels.length; i++) {
	            if (pixels[i] < threshold) pixels[i] = 0;
	            else pixels[i] = 255;
	        }
	        raster.setPixels(0, y, image.getWidth(), 1, pixels);
	    }
	    return result;
	}
	
	public void sort() {
		
		int[] array = new int[10];
		Random rand = new Random();
		for (int i = 0; i < array.length; i++)
		    array[i] = rand.nextInt(100) + 1;
		
		System.out.println(Arrays.toString(array));
		Arrays.sort(array);
		System.out.println(Arrays.toString(array));
	}
	
	public void detectFace(){
		
	}
	
	public void setStarted(boolean n) {
		started = n;
	}

	public ArrayList<Pen> getPens() {
		return pens;
	}

	public void setPens(ArrayList<Pen> pens) {
		this.pens = pens;
	}

	public void clean() {
		colors.clear();
		pens.clear();
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setEspessura(int espessura) {
		this.espessura = espessura;
	}

	public Point getSelected() {
		return selected;
	}

	public boolean isGrid() {
		return grid;
	}

	public void setGrid(boolean grid) {
		this.grid = grid;
	}

	public void setLinhas(int linhas) {
		this.linhas = linhas;
	}

	public void setColunas(int colunas) {
		this.colunas = colunas;
	}

	public int getLinhas() {
		return linhas;
	}

	public int getColunas() {
		return colunas;
	}

	public Celula[][] getMap() {
		return map;
	}

}