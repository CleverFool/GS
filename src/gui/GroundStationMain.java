package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;


import javax.swing.*;

public class GroundStationMain extends JFrame {
	private static final long serialVersionUID = -5652170290197609712L;

	public static void main(String[] args) {
		GroundStationMain gs = new GroundStationMain();
		gs.setExtendedState(gs.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		gs.setVisible(true);

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
		JComponent graph = new JButton("GRAPH");
		
		graph.setPreferredSize(new Dimension(200, 300));
		
		pane.add(altitudeSpeed, BorderLayout.LINE_START);
		pane.add(fpvCamera, BorderLayout.CENTER);
		pane.add(payloadDrop, BorderLayout.LINE_END);
		pane.add(graph, BorderLayout.PAGE_END);
	}
}
