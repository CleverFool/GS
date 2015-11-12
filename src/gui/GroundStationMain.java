package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

import javax.swing.*;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

public class GroundStationMain extends JFrame implements IDataReceiveListener{
	private static final long serialVersionUID = -5652170290197609712L;
	private DataChart altChart;
	private XBeeDevice xbee;
	private int messageNumber =0; //FIX SO IT'S NOT HARD CODED
	private static final String COM_PORT = "COM5";  //PLACEHOLDER
	private static final int BAUD_RATE = 9600; //PLACEHOLDER
	
	
	//MAIN
	public static void main(String[] args) {
		GroundStationMain gs = new GroundStationMain();
		gs.setExtendedState(gs.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		gs.setVisible(true);
		gs.setDefaultCloseOperation(EXIT_ON_CLOSE);


	}
	
	public GroundStationMain() {
		initGui();
	}
	
	private void initGui() {
		xbee = new XBeeDevice(COM_PORT, BAUD_RATE);
		setSize(300, 200);
		setTitle("M-Fly Ground Station");
		
		
		try {

			xbee.open();
			xbee.addDataListener(this);
		} catch (XBeeException e) {
			System.out.println("ERROR: THIS IS FOR ADAM THIS IS INFACT AN ERROR");
			e.printStackTrace();}
		
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
	
	public void update(String newData) {
		if (newData.substring(0,1).equals("A")) {
			int firstComma = newData.indexOf(',');
			int secondComma = newData.indexOf(',', firstComma+1);
			int thirdComma = newData.indexOf(',', secondComma+1);
			String usefullData = newData.substring(secondComma + 1, thirdComma);
			int alt = Integer.parseInt(usefullData);
			altChart.update(new Point(messageNumber++, alt));
		}
		
	}

	@Override
	public void dataReceived(XBeeMessage message) {
		//XBee64BitAddress address = message.getDevice().get64BitAddress();
		//byte[] data = message.getData();
		//boolean isBroadcast = message.isBroadcast();
		String stringOutput = message.getDataString();
		update(stringOutput);
		
	}
}
		
