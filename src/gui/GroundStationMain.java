package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

import gnu.io.CommPortIdentifier;
import panels.DataChart;
import panels.DropStatusPane;
import panels.Instruments;
import panels.ScrollingDataText;

//TODO: Update this and lots of cleanup

public class GroundStationMain extends JFrame implements IDataReceiveListener, ActionListener {

	// Debug boolean
	private static final boolean DEBUG_WITHOUT_RADIO = true;

	// Constants
	private static final long serialVersionUID = -5652170290197609712L;

	// Information for initializing Xbees
	//private static final String COM_PORT = "/dev/tty.usbserial-DA01OPLP"; // PLACEHOLDER
	private static final int BAUD_RATE = 9600; // PLACEHOLDER
	
	private static final String TRANSMITTER_ADDRESS = "0013A20040A5422E";
	// Old Address: "0013A20040E6D613";

	// Location of specific telemetry in XBee message mapped out in English
	// instead of numbers
	
	// Message type: A
	private static final int TIME = 2;
	private static final int ALTITUDE = 3;
	private static final int AIRSPEED = 7;

	// Message type: B
	private static final int B_ALTITUDE = 3;
	private static final int NUM_DROPPED = 2;
	private int lastNumDropped = -1;

	private String comPort;

	// Member objects for each of the panels
	private Container pane;
	private JPanel westPanel;
	private ScrollingDataText dataScroll;
	private DataChart altChart;
	private Instruments altitudeSpeed;
	private DropStatusPane payloadDrop;
	private XBeeDevice xbee;
	private long startTime; // start time of the program used for calculating
							// time elapsed
	private PrintWriter out;
	private PrintWriter xbeeLogOut;
	//private int messageNumber = 0; // FIX SO IT'S NOT HARD CODED

	// Menu Bar Variables
	private JMenuBar menuBar;
	private JMenu comMenu;
	private JMenuItem currentCom;
	private JTextArea comStatus;

	// MAIN
	public static void main(String[] args) {
		new GroundStationMain();
	}

	/**
	 * Default constructor.
	 */
	public GroundStationMain() {

		// Set the window to take maximize to fill the whole screen.
		super.setExtendedState(super.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		// Exit the program when you close the window.
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// move this over to window closing event
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {

				try {
					if (xbee != null)
						xbee.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					out.close();
					xbeeLogOut.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("The Gui has been exited.");
			}
		});

		detectComPorts();

		initGui();
		System.out.println("Hi");

		super.setVisible(true);

		if (DEBUG_WITHOUT_RADIO) {
			comStatus.setText("Com Status: Debugging GUI without radio");
			comStatus.setBackground(Color.YELLOW);
			comStatus.setForeground(Color.BLACK);
			
			this.testXBeeMessageParsing();
			
			comStatus.setText("Com Status: No Attempts to Connect Yet");
			comStatus.setBackground(Color.BLUE);
			comStatus.setForeground(Color.YELLOW);
		}
	}

	private void detectComPorts() {

		menuBar = new JMenuBar();
		comMenu = new JMenu("Select COM Port");
		menuBar.add(comMenu);
		comStatus = new JTextArea("Com Status: No Attempts to Connect Yet");
		comStatus.setEditable(false);
		comStatus.setBackground(Color.BLUE);
		Font font = new Font("Verdana", Font.BOLD, 14);
		comStatus.setFont(font);
		comStatus.setForeground(Color.YELLOW);
		menuBar.add(comStatus);

		ImageIcon iconLogo = new ImageIcon(getClass().getResource("/res/MFlyLogo-icon.png"));
		JLabel iconLabel = new JLabel();
		iconLabel.setIcon(iconLogo);
		menuBar.add(iconLabel);

		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

		// check mark: \u2713

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			String portName = portIdentifier.getName() + " : " + getPortTypeName(portIdentifier.getPortType());
			JMenuItem menuItem = new JMenuItem("  " + portName, KeyEvent.VK_T);
			menuItem.addActionListener(this);
			comMenu.add(menuItem);
		}
	}

	String getPortTypeName(int portType) {
		switch (portType) {
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
		startTime = System.nanoTime();
		setSize(300, 200);
		setTitle("M-Fly Ground Station");

		this.setJMenuBar(menuBar);

		// might not need

		pane = getContentPane();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");
		String fileName = "M-FLY_LOG-" + sdf.format(cal.getTime()) + ".txt";
		String xbeeFileName = "Xbee " + fileName;
		
		try {
			out = new PrintWriter(fileName, "UTF-8");
			xbeeLogOut = new PrintWriter(xbeeFileName, "UTF-8");
		} catch (FileNotFoundException e) {
			// Do Nothing
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		out.print("=== START OF LOG ======" + "\n");
		out.print("TYPE, TIME, ALT, AIRSPEED, NUM_DROPPED\n");
		File file = new File(fileName);

		westPanel = new JPanel();

		JCheckBox toggleAutoScroll = new JCheckBox("Auto Scroll: ON");
		toggleAutoScroll.setSelected(true);

		altitudeSpeed = new Instruments();
		dataScroll = new ScrollingDataText(toggleAutoScroll);

		JPanel dataScrollPanel = new JPanel(new BorderLayout());
		dataScrollPanel.add(dataScroll, BorderLayout.CENTER);

		toggleAutoScroll.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					dataScroll.toggleAutoScroll(false);
					toggleAutoScroll.setText("Auto Scroll: OFF");
				} else {
					dataScroll.toggleAutoScroll(true);
					toggleAutoScroll.setText("Auto Scroll: ON");
				}
			}
		});

		westPanel.setLayout(new BorderLayout());
		westPanel.add(altitudeSpeed, BorderLayout.PAGE_START);
		westPanel.add(toggleAutoScroll, BorderLayout.CENTER);
		westPanel.add(dataScrollPanel, BorderLayout.PAGE_END);

		payloadDrop = new DropStatusPane();

		ImageIcon logo = new ImageIcon(getClass().getResource("/res/MFlyLogo.png"));
		JLabel label = new JLabel();
		label.setIcon(logo);
		label.setHorizontalAlignment(JLabel.CENTER);
		JPanel fpvCamera = new JPanel(new BorderLayout());
		fpvCamera.add(label, BorderLayout.CENTER);

		/*
		 * JFrame frame = new JFrame(); // frame, you would replace this with
		 * the JPanel frame.add(fpvCamera); // add the created opject to your
		 * Panel/Frame frame.setVisible(true); //set the master frame visible
		 * frame.setExtendedState(frame.getExtendedState() |
		 * JFrame.MAXIMIZED_BOTH);
		 */

		// JComponent graph = new JButton("GRAPH");
		int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		altChart = new DataChart("Altitude", width, 300);

		// graph.setPreferredSize(new Dimension(200, 300));

		pane.add(westPanel, BorderLayout.LINE_START);
		pane.add(fpvCamera, BorderLayout.CENTER);
		pane.add(payloadDrop, BorderLayout.LINE_END);
		pane.add(altChart, BorderLayout.PAGE_END);

		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);

		System.out.println("Path : " + file.getAbsolutePath());
		startTime = System.nanoTime();
	}

	private boolean setUpXBee(boolean firstTry) {

		if (comPort != null) {
			xbee = new XBeeDevice(comPort, BAUD_RATE);
			if (xbee.isOpen())
				return true;

			try {
				xbee.open();
				xbee.addDataListener(this);
				if (!firstTry)
					System.out.println("XBee Connected");
				return true;
			} catch (XBeeException e) {
				// System.out.println(firstTry);
				// e.printStackTrace();
				return false;
			}
		}

		return false;
	}
	
	final int ALT_ARRAY_LENGTH = 5;
	double altArray[] = new double[ALT_ARRAY_LENGTH];
	int currentAltLoc = 0;
	
	private double altAvg() {
		double sum = 0.0;
		
		for (int i = 0; i < ALT_ARRAY_LENGTH; ++i) {
			sum += altArray[i];
		}
		
		return sum / (double) ALT_ARRAY_LENGTH;
	}
	
	private void altAdd(double alt) {
		altArray[currentAltLoc++] = alt;
		
		if (currentAltLoc >= ALT_ARRAY_LENGTH) currentAltLoc = 0;
	}

	public void update(String newData) {
		double time = (System.nanoTime() - startTime) / 1000000000.0;
		xbeeLogOut.println(newData);

		if (newData.substring(0, 1).equals("A")) {
			String altStr = getRelevantData(newData, ALTITUDE);
			
			out.print("A," + time);
			out.print("," + altStr + ",");

			String airSpeedStr = getRelevantData(newData, AIRSPEED);

			out.print(airSpeedStr + ",0\n");

			double alt = Double.parseDouble(altStr);
			
			// Convert alt to feet from meters
			alt  *= 3.28084;
			
			altAdd(alt);
			alt = altAvg();
			
			// double time = Double.parseDouble(timeStr);
			double airSpeed = Double.parseDouble(airSpeedStr);

			dataScroll.update("TIME:" + ((double) Math.round(time * 1000d) / 1000) + "; ");
			dataScroll.update("ALT:" + ((double) Math.round(alt * 1000d) / 1000) + "; ");
			dataScroll.update("AIRSPEED:" + ((double) Math.round(airSpeed * 1000d) / 1000) + "\n----------\n");

			Point2D.Double p = new Point2D.Double((double) time, alt);
			altChart.update(p); // Update Graphs
			// assuming alt is in meters right now
			
			// Update Numbers
			altitudeSpeed.update((int) (alt), (float) airSpeed);

		}
		
		System.out.println(newData);
		System.out.println(newData.charAt(0));
		
		if (newData.substring(0, 1).equals("B")) { // Update Drop Status
			//TODO: Test multiple B message values
			// TODO: Work for multple 'B' message recieving

			//String altStr = getRelevantData(newData, B_ALTITUDE);
			String numDropStr = getRelevantData(newData, NUM_DROPPED);
			double alt = Double.parseDouble(getRelevantData(newData, B_ALTITUDE));
			int numDropped = Integer.parseInt(numDropStr);
			
			// Convert alt to feet from meters
			alt  *= 3.28084;
			
			out.print("B,");
			out.print(time + ",");
			out.print("" + ((double) (Math.round(alt * 1000d) / 1000)) + ",-999,");
			out.print(numDropStr + "\n");
			
			System.out.println(newData);
			System.out.println(numDropped);
			System.out.println(lastNumDropped);
			
			if (numDropped != lastNumDropped || numDropped == 1) {
	
				dataScroll.update("DROP RECIEVED - TIME: " + ((double) (Math.round(time * 1000d) / 1000)) + "; ALT: "
						+ ((double) (Math.round(alt * 1000d) / 1000)) + "; NUM_DROPPED: " + numDropped);
	
				dataScroll.update("\n----------\n");
	
				Point2D.Double p = new Point2D.Double((double) time, alt);
				altChart.update(p, true); // Update Graphs
				payloadDrop.payloadDropped((long) time, (long) alt, numDropped);
				
				lastNumDropped = numDropped;
			}

		}

		westPanel.repaint();
	}

	// takes the raw "csv" type data string and extracts the relevant element in the string
	public String getRelevantData(String rawData, int commaNumber) {
		String[] split = rawData.split(",");
		
		if (commaNumber < split.length)
			return split[commaNumber];
		else
			return null;
	}

	// Method for when data is received
	@Override
	public void dataReceived(XBeeMessage message) {
		XBee64BitAddress address = message.getDevice().get64BitAddress();
		
		if (address.toString().equals(TRANSMITTER_ADDRESS) || true) {
			update(message.getDataString());
		}
	}
	
	static int test_dropped = 0;
	
	public void testXBeeMessageParsing() {
		// Random Data generator for testing without xbee telemetry
		
		// Note: Message Documentation from Arduino FCM Code
		// A,M-Fly,ALTITUDE,GYROX,GYROY,GYROZ,AIRSPEED,VOLTAGE,DROPPED,ACCELX,ACCELY,ACCELZ
		// B,TIME,DROPPED,ALT,
		
		for (int i = 0; i < 2000; i++) {
			int time = i;
			int alt = (int) (Math.random() * 100);
			
			String raw = "A,M-Fly," + time + "," + alt + ",0,0,0," + (Math.random() * 10 + 10) + ",9," + test_dropped + ",0,0,0";
			
			if ((int) (Math.random() * 50) == 9) {
				++test_dropped;
				raw = "B," + time + "," + test_dropped + "," + alt + ",";
			}
			update(raw);
			// this.revalidate();
			/*
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// Do Nothing
			}
			*/
		}
	}

	public void actionPerformed(ActionEvent e) {
		String oldName, newName;

		if (currentCom != null) {
			oldName = currentCom.getText();
			newName = oldName.substring(0, 1) + " " + oldName.substring(2);
			currentCom.setText(newName);
		}

		currentCom = (JMenuItem) (e.getSource());
		
		oldName = currentCom.getText();
		int endSerial = oldName.indexOf(':');
		
		comPort = oldName.substring(2, endSerial - 1);
		
		System.out.println(comPort);

		if (setUpXBee(false)) {
			System.out.println("Set Up Success!");
			
			comStatus.setText("Com Status: Connected to '" + comPort + "' :: REMEMBER TO UNPLUG AND REPLUG RADIO AFTER EXIT");
			comStatus.setBackground(Color.GREEN);
			comStatus.setForeground(Color.BLACK);

			newName = oldName.substring(0, 1) + "\u2713" + oldName.substring(2);
			currentCom.setText(newName);
		} else {
			System.out.println("Could not connect to Com Port: " + comPort);
			
			comStatus.setText("Com Status: No Connection Established :: REMEMBER TO UNPLUG AND REPLUG RADIO AFTER EXIT");
			comStatus.setBackground(Color.RED);
			comStatus.setForeground(Color.BLACK);
		}
	}
}
