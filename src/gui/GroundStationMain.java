package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

import javax.swing.*;

public class GroundStationMain extends JFrame {
	private static final long serialVersionUID = -5652170290197609712L;
	private DataChart altChart;
	public static void main(String[] args) {
		GroundStationMain gs = new GroundStationMain();
		gs.setExtendedState(gs.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		gs.setVisible(true);
		gs.setDefaultCloseOperation(EXIT_ON_CLOSE);
		for (int i = 0; i < 60; i++) {
			gs.update();
			try {Thread.sleep(1000);} catch (InterruptedException e) {}
		}

	}
	
	public GroundStationMain() {
		initGui();
	}
	
	private void initGui() {
		setSize(300, 200);
		setTitle("M-Fly Ground Station");
		
		Container pane = getContentPane();
		
		JComponent altitudeSpeed = new Instruments();
		JComponent fpvCamera = new JButton("CAMERA");
		JComponent payloadDrop = new DropStatusPane();
		//JComponent graph = new JButton("GRAPH");
		altChart = new DataChart("Altitude", 3200,300);
		//graph.setPreferredSize(new Dimension(200, 300));
		
		pane.add(altitudeSpeed, BorderLayout.LINE_START);
		pane.add(fpvCamera, BorderLayout.CENTER);
		pane.add(payloadDrop, BorderLayout.LINE_END);
		pane.add(altChart, BorderLayout.PAGE_END);
		
		
		
	   }
	
	public void update() {
		Random rand = new Random();
		int x = rand.nextInt(100), y = rand.nextInt(100);
		altChart.update(new Point(x, y));
	}
}
		
