import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.*;


public class Pen extends Thread {

	private static final int size = 5;
	private int id;
	private Board board;
	private Interface interfac;
	private double speedX;
	private double speedY;
	private Point position;
	private Point location;
	private Point destiny; 
	private Color color;
	private ArrayList<Point> pontos = new ArrayList<Point>();
	private ArrayList<Point> destinies = new ArrayList<Point>();
	private int time;
	
	
	public Pen(Board board, Interface interfac, int id) {
		super();
		this.id = id;
		this.interfac = interfac;
		this.board = board;
		destiny = new Point();
		setLocation(new Point());
	}
	
	
	@Override
	public void run() {

		try {
			
			position = randomPosition();
			location = board.convertToGrid(position);
			setRandomColor();
			speedY = 0;
			speedX = 0;
			time=0;
			int count = 0;
			
			long startTime = System.currentTimeMillis();
			long elapsedTime = 0L;
			int n = interfac.getN();
			
			while (elapsedTime < (10*60*1000)/(n*0.1)) {
				
				elapsedTime = (new Date()).getTime() - startTime;
				
				sleep(30);
				move();
				count++;
				if(count>10){
					checkLocation();
					count = 0;
				}		

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	public void move() {

		findRoute();
/*
		if (position.x <= size / 2) {
			position.x = size / 2;
			speedX = -speedX;
		}
		if (position.x + size / 2 >= board.getWidth()) {
			position.x = board.getWidth() - size / 2;
			speedX = -speedX;
		}
		if (position.y <= size / 2) {
			position.y = size / 2;
			speedY = -speedY;
		}
		if (position.y + size / 2 >= board.getHeight()) {
			position.y = board.getHeight() - size / 2;
			speedY = -speedY;
		}
		*/
		position.x += speedX;
		position.y += speedY;
		location = board.convertToGrid(position);
		
		Point ponto = new Point(position);
		pontos.add(ponto);
		
		try {
			board.getMap()[location.y][location.x].setOcupado(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		board.repaint();
	}
	
	public void checkLocation(){  
		
		if(location.equals(destiny)){
			board.getMap()[destiny.y][destiny.x].setSelected(false);
		}
		
		radar();
		
	}
	
	public void radar(){ 
		//radar		
		destinies.clear();
		
		for (int i=0; i<board.getLinhas(); i++) {
			for (int j=0; j<board.getColunas(); j++) {

				if(board.getMap()[i][j].isSelected()){
					Point p = new Point(board.getMap()[i][j].getPosition());
					destinies.add(p);
				}
			}
		}
		if(destinies.size()==0){
			destiny = randomPosition();
		}
		else{
			findShortestWay();
		}
		
		
		System.out.println(destinies.size());
	}
	
	public void findShortestWay(){ 
		
		Point posMin = new Point();	
		for (int i=0; i<destinies.size(); i++) {		
			Point p = destinies.get(i);	
			if (calculateDistance(p) < calculateDistance(posMin)){
				posMin = p;
			}
		}
		destiny = posMin;
	}
	
	public double calculateDistance(Point point){
		
		double dx = location.getX() - point.getX();
		double dy = location.getY() - point.getY();
		double deltaX = Math.abs(dx);
		double deltaY = Math.abs(dy);
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		
		return distance;
	}
	
	public void findRoute() {
		
		double dx = location.getX() - destiny.getX();
		double dy = location.getY() - destiny.getY();
		double deltaX = Math.abs(dx);
		double deltaY = Math.abs(dy);
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

		if (dx < 0)
			speedX = 2 * (deltaX / distance);
		if (dy < 0)
			speedY = 2 * (deltaY / distance);
		if (dx > 0)
			speedX = -2 * (deltaX / distance);
		if (dy > 0)
			speedY = -2 * (deltaY / distance);

		/*
		 System.out.println("speedX: " + speedX + " speedY: " + speedY +
		 " deltaX/distance: " + (deltaX / distance) + " distance: " + distance
		 + " deltaX: " + deltaX );*/
		 
	}

	
	public Point getPosition() {
		return position;
	}
	
	public void setPosition(Point position) {
		this.position = position;
	}
	
	public Point randomPosition() {
		int rx = (int)(Math.random()*board.getWidth());
		int ry = (int)(Math.random()*board.getHeight());
		Point position = new Point(rx,ry);
		
		return position;
	}
	
	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		this.color = color;
	}
	public void setRandomColor() {
		int R = (int)(Math.random()*256);
		int G = (int)(Math.random()*256);
		int B = (int)(Math.random()*256);
		color = new Color(R, G, B);
	}

	public ArrayList<Point> getPontos() {
		return pontos;
	}


	public void setPontos(ArrayList<Point> pontos) {
		this.pontos = pontos;
	}

	public double getSpeedX() {
		return speedX;
	}

	public void setSpeedX(double speedX) {
		this.speedX = speedX;
	}

	public double getSpeedY() {
		return speedY;
	}

	public void setSpeedY(double speedY) {
		this.speedY = speedY;
	}


	public Point getLocation() {
		return location;
	}


	public void setLocation(Point location) {
		this.location = location;
	}
	
}
