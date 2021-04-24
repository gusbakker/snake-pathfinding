import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Timer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Interface {

	public Board board;
	private FileManager fileManager;
	private JFrame frame;
	private JTextField text1;
	private JButton start, stop, add, shuffle, detect, openButton, saveButton;
	private JSlider slider, slider2;
	private JFileChooser fc;
	private JTextArea log;
	private final String newline = "\n";
	private Timer timer;

	private static final int min = 0;
	private static final int max = 20;
	private static final int med = 10;

	private int N;
	private int c = 0;
	private JCheckBox checkbox1, checkbox2;
	

	public Interface() {
		
	
		board = new Board();
		frame = new JFrame("Desenho");
		Container c = frame.getContentPane();
		Border blackline = BorderFactory.createLineBorder(Color.black);
		fc = new JFileChooser();

		// norte
		JPanel northPanel = new JPanel();
		northPanel.setBorder(BorderFactory.createTitledBorder("Options"));
		slider = new JSlider(JSlider.HORIZONTAL, min, max, med);
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		openButton = new JButton("Open a File...");
		saveButton = new JButton("Save a File...");

		northPanel.add(slider);
		northPanel.add(openButton);
		northPanel.add(saveButton);
		c.add(northPanel, BorderLayout.NORTH);

		// Sul
		JPanel southPanel = new JPanel();
		start = new JButton("Start");
		checkbox1 = new JCheckBox("Pixelate");
		checkbox1.setEnabled(false);
		checkbox2 = new JCheckBox("Grid");
		checkbox2.setEnabled(false);
		stop = new JButton("Stop");
		stop.setEnabled(false);
		add = new JButton("Add Pen");
		add.setEnabled(false);
		shuffle = new JButton("Shuffle");
		shuffle.setEnabled(false);
		detect = new JButton("Detect");
		detect.setEnabled(false);

		southPanel.add(start);
		southPanel.add(checkbox1);
		southPanel.add(checkbox2);
		southPanel.add(stop);
		southPanel.add(add);
		southPanel.add(shuffle);
		southPanel.add(detect);
		c.add(southPanel, BorderLayout.SOUTH);

		// Painel Lateral
		JPanel lateralPanel = new JPanel(new GridLayout(4, 2));
		c.add(lateralPanel, BorderLayout.EAST);

		JPanel info = new JPanel(new GridLayout(4, 2));
		info.setBorder(BorderFactory.createTitledBorder("Info"));
		text1 = new JTextField();
		log = new JTextArea(40, 10);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		slider2 = new JSlider(JSlider.VERTICAL, min, max / 10, med / 10);
		slider2.setMajorTickSpacing(100);
		slider2.setPaintTicks(true);
		slider2.setPaintLabels(true);
		info.add(new JLabel("Quantidade: "));
		info.add(text1);
		info.add(new JLabel("Log Events: "));
		info.add(log);
		lateralPanel.add(info);
		lateralPanel.add(slider2);

		// Centro
		board.setBorder(blackline);
		board.setFocusable(true);
		board.requestFocusInWindow();
		c.add(board, BorderLayout.CENTER);

		

		board.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				Point p = board.convertToGrid(e.getPoint());
				board.getMap()[p.y][p.x].setSelected(true);
			}
		});

		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (source.getValueIsAdjusting()) {

					board.pixelate(source.getValue());
				}
			}
		});

		slider2.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (source.getValueIsAdjusting()) {
					board.setEspessura((int) source.getValue());
				}
			}
		});

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				timer = new Timer();				
				
				if (!text1.getText().isEmpty()) {
					N = Integer.parseInt(text1.getText());
					startPens();					
				}
				
				checkbox1.setEnabled(true);
				checkbox2.setEnabled(true);
				board.setStarted(true);
				start.setEnabled(false);
				stop.setEnabled(true);
				add.setEnabled(true);
				shuffle.setEnabled(true);
				detect.setEnabled(true);
				board.repaint();

			}
		
		});
		checkbox1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(checkbox1.isSelected()){
					board.pixelate(10);
				}
				else{
					// despixelate
				}
				board.repaint();
			}
		});
		checkbox2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(checkbox2.isSelected()){
					board.setGrid(true);
				}
				else{
					board.setGrid(false);
				}
				board.repaint();
			}
		});
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				stop();
			}
		});
		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				for (int i = 0; i < 1; i++) {
					Pen pen = new Pen(board, Interface.this, i);
					board.getPens().add(pen);
					pen.start();
					Interface.this.c++;
				}

			}
		});
		shuffle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				board.shuffleMatrix(board.getMap());
				board.repaint();
			}
		});
		detect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				board.detectFace();
				board.repaint();
			}
		});

		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int returnVal = fc.showOpenDialog(board);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					fileManager.setFile(file);
					try {
						fileManager.readByFileExtension();
						board.loadImage();
						//board.buildMap();
						board.repaint();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					log.append("Opening: " + file.getName() + "." + newline);
				} else {
					log.append("Open command cancelled by user." + newline);
				}
				log.setCaretPosition(log.getDocument().getLength());

			}
		});

		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int returnVal = fc.showSaveDialog(board);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// This is where a real application would save the file.
					log.append("Saving: " + file.getName() + "." + newline);
				} else {
					log.append("Save command cancelled by user." + newline);
				}
				log.setCaretPosition(log.getDocument().getLength());

			}
		});

		frame.setSize(1000, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(100, 0);
	}

	public void startPens() {

		for (int i = 0; i < N; i++) {
			Pen pen = new Pen(board, this, i);
			board.getPens().add(pen);
			pen.start();
		}
	}

	
	public void stop() {

		text1.setText(null);
		board.setStarted(false);
		board.clean();

		start.setEnabled(true);
		stop.setEnabled(false);
		add.setEnabled(false);

	}
	
	public void cpuUsageInfo(){
		
		final OperatingSystemMXBean myOsBean = ManagementFactory.getOperatingSystemMXBean();
		double load = myOsBean.getSystemLoadAverage();
		log.setText("" + load);
	}

	public int getN() {
		return N;
	}

	public void init() {
		frame.setVisible(true);
	}

	public Board getBoard() {
		return board;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

}
