package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.*;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

public class GroundStationMain extends JFrame implements IDataReceiveListener{
	
	//Constants
	private static final long serialVersionUID = -5652170290197609712L;
	
	//Information for initializing Xbees
	private static final String COM_PORT = "/dev/tty.usbserial-DA01OPLP";  //PLACEHOLDER
	private static final int BAUD_RATE = 9600; //PLACEHOLDER
	
	//Location of specific telemetry mapped out in English instead of numbers
	private static final int TIME = 2;
	private static final int ALTITUDE = 3;
	private static final int AIRSPEED = 4;
	
	private static final int B_ALTITUDE = 2;
	private static final int NUM_DROPPED = 1;
	
	private DataChart altChart;
	private Instruments altitudeSpeed;
	private DropStatusPane payloadDrop;
	private XBeeDevice xbee;
	private int messageNumber =0; //FIX SO IT'S NOT HARD CODED

	
	
	//MAIN
	public static void main(String[] args) {
		GroundStationMain gs = new GroundStationMain();
		gs.setExtendedState(gs.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		gs.setVisible(true);
		gs.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
/*for(int i = 0; i<60; i++){
String raw =	"A,MFLY,"+(Math.random()+i)+","+Math.random()*100+","+Math.random()*20+",";
if (i%6 == 0){
	raw = "B,"+(int)(Math.random()*2)+","+Math.random()*100+",";
}
gs.update(raw);
	 try {Thread.sleep(1000);} catch (InterruptedException e){};
}*/ // Random Data generator for testing without xbee telemetry

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
		
		altitudeSpeed = new Instruments();
		payloadDrop = new DropStatusPane();
		
		JComponent fpvCamera = new JButton("CAMERA");
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
			String altStr = getRelevantData(newData, ALTITUDE);
			String timeStr = getRelevantData(newData, TIME);
			String airSpeedStr = getRelevantData(newData, AIRSPEED);
			double alt = Double.parseDouble(altStr);
			double time = Double.parseDouble(timeStr);
			double airSpeed = Double.parseDouble(airSpeedStr);
			
			Point2D.Double p = new Point2D.Double(time, alt);
System.out.println(p.getX()+" "+p.getY());
			altChart.update(p); //Update Graphs
			altitudeSpeed.update((int) alt, (float)airSpeed); //Update Numbers
		
		}else if(newData.substring(0,1).equals("B")){ //Update Drop Status
			String altStr = getRelevantData(newData, B_ALTITUDE);
			String numDropStr = getRelevantData(newData, NUM_DROPPED);
			double alt = Double.parseDouble(altStr);
			int numDropped = Integer.parseInt(numDropStr);
			payloadDrop.payloadDropped((long)alt, numDropped);
		}
		
	}
	
	//takes the raw "csv" type data string and extracts the relevant element in the string
	public String getRelevantData(String rawData, int commaNumber){
		int startCommaIndex = 0, endCommaIndex;
		int commaCount = 0;
		while(commaCount < commaNumber){
			startCommaIndex = rawData.indexOf(',',startCommaIndex+1);
			commaCount++;
		}
		endCommaIndex = rawData.indexOf(',',startCommaIndex+1);
		String relevantData = rawData.substring(startCommaIndex+1 ,endCommaIndex);
		return relevantData;
	}
 
	//Remember to implement address-specific listening
	@Override
	public void dataReceived(XBeeMessage message) {
		//XBee64BitAddress address = message.getDevice().get64BitAddress();
		//byte[] data = message.getData();
		//boolean isBroadcast = message.isBroadcast();
		String stringOutput = message.getDataString();
		update(stringOutput);
		
	}
}
		
