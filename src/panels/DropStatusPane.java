package panels;

import javax.swing.JPanel;
import java.awt.Font;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.JLabel;

/**
 * This pane shows information about the status of the two drops.
 * It creates displays for each drop and manages updated them with new info.
 */
public class DropStatusPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel title;
	private boolean dropToUpdate = false;
	private IndividualDropPanel[] drops;
	private final int FONT_SIZE = 30;

	public DropStatusPane() {
		// Setup Layout for Pane
		
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);

		// Setup Title
		
		title = new JLabel("Drop Status");
		add(title);
		
		// Set font size and alignment
		
		Font labelFont = title.getFont();
		Font newFont = new Font(labelFont.getName(), Font.PLAIN, FONT_SIZE);

		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont(newFont);

		// Create individual drop panels and add them to this panel
		
		drops = new IndividualDropPanel[2];
		drops[0] = new IndividualDropPanel(1);
		drops[1] = new IndividualDropPanel(2);
		add(drops[0]);
		add(drops[1]);
	}

	/**
	 * Updates one of the displays for the drops. Alternates which display is updated.
	 * @param duration Time in seconds since start of flight.
	 * @param altitude How high the plane was when it dropped the payload.
	 */
	public void payloadDropped(long time, long altitude, int numDropped) {
		String newPayloadStatus = "";//Construct a drop status message
		newPayloadStatus += time;
		newPayloadStatus += "s; ";
		newPayloadStatus += altitude;
		newPayloadStatus += "ft"; // Change to whatever units the altitude is given in.
		newPayloadStatus += ";\n ";
		newPayloadStatus += numDropped + " dropped";
		
		// Code to alternate which drop status panel to update
		drops[dropToUpdate ? 1 : 0].updateDropStatus(newPayloadStatus);
		dropToUpdate = !dropToUpdate;
	}

	/**
	 * Changes duration from a number format to a string time format.
	 * @param duration Time in seconds since start of flight.
	 * @return A string representation of the duration.
	 */ //Not sure if this method is still needed
	/*
	protected String timeElapsedToString(long duration) {
		final long HpS = 60 * 60;
		final long MpS = 60;
		long hours = duration / HpS;
		duration -= hours * HpS;
		long minutes = duration / MpS;
		duration -= duration / MpS;
		long seconds = duration;
		String time = hours + ":" + minutes + ":" + seconds;
		return time;
	}
	*/
}
