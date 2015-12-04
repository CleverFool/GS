package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.*;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

/**
 * Main class for the project. Creates the top-level gui object. Also listens for XBee messages.
 */
public class GroundStationMain extends JFrame implements IDataReceiveListener{

	// Constants
	private static final long serialVersionUID = -5652170290197609712L;

	// Information for initializing Xbees
	private static final String COM_PORT = "/dev/tty.usbserial-DA01OPLP";  //PLACEHOLDER
	private static final int BAUD_RATE = 9600; //PLACEHOLDER

	// Location of specific telemetry in XBee message mapped out in English instead of numbers
	// Messgae type: A
	private static final int TIME = 2;
	private static final int ALTITUDE = 3;
	private static final int AIRSPEED = 7;

	// Message type: B
	private static final int B_ALTITUDE = 4;
	private static final int NUM_DROPPED = 3;

	// Member objects for each of the panels
	private DataChart altChart;
	private Instruments altitudeSpeed;
	private DropStatusPane payloadDrop;

	// The XBee device
	private XBeeDevice xbee;

	/**
	 * Entry point for the ground station.
	 * @param args Command line arguments. Not used.
	 */
	public static void main(String[] args) {
		GroundStationMain gs = new GroundStationMain();

		// Simulate XBee messages to test that we interpret them correctly
		//gs.testXBeeMessageParsing();

	}


	/**
	 * Default constructor.
	 */
	public GroundStationMain() {
		// Set the window to take maximize to fill the whole screen.
		super.setExtendedState(super.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		// Exit the program when you close the window.
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		initGui();

		super.setVisible(true);
	}

	/**
	 * Initialize the gui
	 */
	private void initGui() {
		// Set the window's title
		setTitle("M-Fly Ground Station");

		// Create a new XBee device object
		xbee = new XBeeDevice(COM_PORT, BAUD_RATE);


		// Try opening and connecting to the device
		try {
			xbee.open();
			xbee.addDataListener(this);
			System.out.println("setup success!");
		} catch (XBeeException e) {
			System.out.println("ERROR: THIS IS FOR ADAM THIS IS INFACT AN ERROR");
			e.printStackTrace();
		}


		altitudeSpeed = new Instruments();
		payloadDrop = new DropStatusPane();

		// Add a placeholder panel for the video feed
		JComponent fpvCamera = new JButton("CAMERA");

		// Make a new chart and set it to the size of the screen so it fills the window
		int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		altChart = new DataChart("Altitude", width,300);

		// Add all the components to the window
		super.add(altitudeSpeed, BorderLayout.LINE_START);
		super.add(fpvCamera, BorderLayout.CENTER);
		super.add(payloadDrop, BorderLayout.LINE_END);
		super.add(altChart, BorderLayout.PAGE_END);
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
//System.out.println(p.getX()+" "+p.getY());
			altChart.update(p); //Update Graphs
			//assuming alt is in meters right now
			altitudeSpeed.update((int) (alt*3.28), (float)airSpeed); //Update Numbers

//System.out.println(alt);
			
		}else if(newData.substring(0,1).equals("B")){ //Update Drop Status
			String altStr = getRelevantData(newData, B_ALTITUDE);
			String numDropStr = getRelevantData(newData, NUM_DROPPED);
			String timeStr = getRelevantData(newData, TIME);
			double alt = Double.parseDouble(altStr);
			int numDropped = Integer.parseInt(numDropStr);
			double time = Double.parseDouble(timeStr);
			payloadDrop.payloadDropped((long)time,(long)alt, numDropped);
			
			System.out.println("B "+newData);
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
	public void dataReceived(XBeeMessage message) { //Method for when data is recieved

		XBee64BitAddress address = message.getDevice().get64BitAddress();
//System.out.println(address.toString()=="0013A20040E6D613");
		//if (address.toString() == "0013A20040E6D613"){//check if data is from the correct address
		String stringOutput = message.getDataString();
//System.out.println(stringOutput);
		update(stringOutput);
		//}
		
		

	}

	public void testXBeeMessageParsing() {
		for(int i = 0; i<60; i++) {
			String raw = "A,MFLY,"+(Math.random()+i)+","+Math.random()*100+","+Math.random()*20+",";
			if (i%6 == 0) {
				raw = "B,"+(int)(Math.random()*2)+","+Math.random()*100+",";
			}
			update(raw);
			try {Thread.sleep(1000);} catch (InterruptedException e){};
		} // Random Data generator for testing without xbee telemetry
	}
}
