package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import javax.swing.*;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

import gnu.io.CommPortIdentifier;

public class GroundStationMain extends JFrame implements IDataReceiveListener, WindowListener{

	//Debug boolean
	private static final boolean DEBUG_WITHOUT_RADIO = true;
	
	// Constants
	private static final long serialVersionUID = -5652170290197609712L;
	
	//Information for initializing Xbees
	private static final String COM_PORT = "/dev/tty.usbserial-DA01OPLP";  //PLACEHOLDER
	private static final int BAUD_RATE = 9600; //PLACEHOLDER
	private static final String TRANSMITTER_ADDRESS = "0013A20040E6D613";

	// Location of specific telemetry in XBee message mapped out in English instead of numbers
	// Messgae type: A
	private static final int TIME = 2;
	private static final int ALTITUDE = 3;
	private static final int AIRSPEED = 7;

	// Message type: B
	private static final int B_ALTITUDE = 3;
	private static final int NUM_DROPPED = 2;

	// Member objects for each of the panels
	private JMenuBar menuBar;
	private DataChart altChart;
	private Instruments altitudeSpeed;
	private DropStatusPane payloadDrop;
	private XBeeDevice xbee;
	private long startTime; //start time of the program used for calculating time elapsed
	private PrintWriter out;
	private int messageNumber =0; //FIX SO IT'S NOT HARD CODED

	
	
	//MAIN
	public static void main(String[] args) {
		GroundStationMain gs = new GroundStationMain();
	}

	/**
	 * Default constructor.
	 */
	public GroundStationMain() {
		startTime = System.nanoTime();
		// Set the window to take maximize to fill the whole screen.
		super.setExtendedState(super.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		// Exit the program when you close the window.
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while( portEnum.hasMoreElements()){
			CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
        }        
		
		initGui();
		System.out.println("Hi");
		
		if(!DEBUG_WITHOUT_RADIO){
	    	while (!setUpXBee(false)) { //multiple times, no error msg
				try {Thread.sleep(1000);}
				catch (InterruptedException e) {}
			}  
		}
		
		
		
		super.setVisible(true);
	}

	 String getPortTypeName( int portType )
	    {
	        switch ( portType )
	        {
	            case CommPortIdentifier.PORT_I2C:
	                return "I2C";
	            case CommPortIdentifier.PORT_PARALLEL:
	                return "Parallel";
	            case CommPortIdentifier.PORT_RAW:
	                return "Raw";
	            case CommPortIdentifier.PORT_RS485:
	                return "RS485";
	            case CommPortIdentifier.PORT_SERIAL:
	                return "Serial";
	            default:
	                return "unknown type";
	        }
	    }

	private void initGui() {
		setSize(300, 200);
		setTitle("M-Fly Ground Station");
		
		menuBar = new JMenuBar();
		JMenu menu = new JMenu("Select COM Port");
		menuBar.add(menu);
		
		JMenuItem menuItem = new JMenuItem("\u2713 A text-only menu item", KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
		        "This doesn't really do anything");
		menu.add(menuItem);
	    this.setJMenuBar(menuBar);
		
		
		if(!DEBUG_WITHOUT_RADIO){
			xbee = new XBeeDevice(COM_PORT, BAUD_RATE);
			setUpXBee(true); //first time, print an error
		}
		
		Container pane = getContentPane();
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
        String fileName = "M-FLY_LOG-" + sdf.format(cal.getTime()) + ".txt";
        try {out = new PrintWriter(fileName,"UTF-8");} catch (FileNotFoundException e) {} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        out.print("Hello World");
        File file = new File(fileName);
        

		altitudeSpeed = new Instruments();
		payloadDrop = new DropStatusPane();
		
		JComponent fpvCamera = new JButton("CAMERA");
		//JComponent graph = new JButton("GRAPH");
		int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		altChart = new DataChart("Altitude", width,300);

		
		
		//graph.setPreferredSize(new Dimension(200, 300));
		
		pane.add(altitudeSpeed, BorderLayout.LINE_START);
		pane.add(fpvCamera, BorderLayout.CENTER);
		pane.add(payloadDrop, BorderLayout.LINE_END);
		pane.add(altChart, BorderLayout.PAGE_END);
	
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.addWindowListener(this);
		this.setVisible(true);
    
        System.out.println("Path : " + file.getAbsolutePath());
		if(DEBUG_WITHOUT_RADIO){
		// Simulate XBee messages to test that we interpret them correctly
		this.testXBeeMessageParsing();
		}
	}
    
	private boolean setUpXBee(boolean firstTry) {
		
		if (xbee.isOpen()) return true;
		try {
			xbee.open();
			xbee.addDataListener(this);
			if (!firstTry) System.out.println("XBee Connected");
			return true;
		} catch (XBeeException e) {
		//System.out.println(firstTry);
			if (firstTry) e.printStackTrace();
			
			return false;
		}
	}
	public void update(String newData) {
		
		double time = (System.nanoTime()-startTime)/1000000000.0;
		if (newData.substring(0,1).equals("A")) {
			String altStr = getRelevantData(newData, ALTITUDE);
//String timeStr = getRelevantData(newData, TIME);
			String airSpeedStr = getRelevantData(newData, AIRSPEED);
			double alt = Double.parseDouble(altStr);
//double time = Double.parseDouble(timeStr);
			double airSpeed = Double.parseDouble(airSpeedStr);
			Point2D.Double p = new Point2D.Double((double)time, alt);
			altChart.update(p); //Update Graphs
			//assuming alt is in meters right now
			altitudeSpeed.update((int) (alt*3.28), (float)airSpeed); //Update Numbers

		}else if(newData.charAt(0) == 'B'){ //Update Drop Status
			System.out.println("Drop Recieved: "+newData);
			String altStr = getRelevantData(newData, B_ALTITUDE);
			String numDropStr = getRelevantData(newData, NUM_DROPPED);
//String timeStr = getRelevantData(newData, TIME);
			double alt = Double.parseDouble(altStr);
			int numDropped = Integer.parseInt(numDropStr);
//double time = Double.parseDouble(timeStr);
			Point2D.Double p = new Point2D.Double((double)time, alt);
			altChart.update(p); //Update Graphs
			payloadDrop.payloadDropped((long)time,(long)alt, numDropped);
			
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
		if (address.toString().equals(TRANSMITTER_ADDRESS)){//check if data is from the correct address
		String stringOutput = message.getDataString();
System.out.println(stringOutput);//+" "+stringOutput.substring(0, 1)+" "+stringOutput.substring(0, 1).equals("B"));
		update(stringOutput);
		}
	}
	public void testXBeeMessageParsing() {
		for(int i = 0; i<60; i++) {
			String raw = "A,MFLY,"+(Math.random()+i)+","+Math.random()*100+",,,,"+Math.random()*20+",";
			if (i%10 == 9) {
				raw = "B,1.4,"+(int)(Math.random()*2)+","+Math.random()*100+",";
			}
			update(raw);
			try {Thread.sleep(1000);} catch (InterruptedException e){};
		} // Random Data generator for testing without xbee telemetry
	}

	//needed functions for WindowListener. Only here to close the writer when the X is clicked
	@Override
	public void windowActivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowClosing(WindowEvent arg0) {out.close(); System.exit(0);}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}
		
