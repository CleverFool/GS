package panels;

import java.awt.Component;
import java.awt.Font;

import javax.swing.*;

public class Instruments extends JPanel {
	private static final long serialVersionUID = -8679191471601739745L;
	
	private JLabel altitudeText;
	private JLabel altitude;
	private JLabel speedText;
	private JLabel speed;
	
	private final int FONT_SIZE = 30;
	
	public Instruments() {
		altitudeText = new JLabel("Altitude [ft]");
		altitude = new JLabel("0");
		speedText = new JLabel("Airspeed [ft/s]");
		speed = new JLabel("0");
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(altitudeText);
		add(altitude);
		add(speedText);
		add(speed);
		
		// Set font sizes for instruments
		Font labelFont = altitudeText.getFont();
		Font newFont = new Font(labelFont.getName(), Font.PLAIN, FONT_SIZE);
		
		altitudeText.setFont(newFont);
		altitude.setFont(newFont);
		speedText.setFont(newFont);
		speed.setFont(newFont);
		
		// Set alignment for all components
		altitudeText.setAlignmentX(Component.CENTER_ALIGNMENT);
		altitude.setAlignmentX(Component.CENTER_ALIGNMENT);
		speedText.setAlignmentX(Component.CENTER_ALIGNMENT);
		speed.setAlignmentX(Component.CENTER_ALIGNMENT);
	}
	
	public void update(int alt_in, float speed_in) {
		altitude.setText(Integer.toString(alt_in));
		
		// TODO: Replace with real airspeed
		speed.setText("???");
		//speed.setText(Float.toString(speed_in));
	}
}
