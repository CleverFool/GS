package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;

/**
 * This pane shows information about the status of the two drops.
 * It creates displays for each drop and manages updated them with new info.
 */
public class DropStatusPane extends JPanel {

	JLabel title;
	boolean dropToUpdate = false;
	IndividualDropPanel[] drops;

	public DropStatusPane() {
		BorderLayout layout = new BorderLayout();

		title = new JLabel("Drop Status");
		super.add(title, BorderLayout.NORTH);

		drops = new IndividualDropPanel[2];
		drops[0] = new IndividualDropPanel(1);
		drops[1] = new IndividualDropPanel(2);
		super.add(drops[0], BorderLayout.CENTER);
		super.add(drops[1], BorderLayout.SOUTH);
	}

	/**
	 * Updates one of the displays for the drops. Alternates which display is updated.
	 * @param duration Time in seconds since start of flight.
	 * @param altitude How high the plane was when it dropped the payload.
	 */
	public void payloadDropped(long duration, int altitude) {
		String newPayloadStatus = "";
		newPayloadStatus += timeElapsedToString(duration);
		newPayloadStatus += "; ";
		newPayloadStatus += altitude;
		newPayloadStatus += "ft"; // Change to whatever units the altitude is given in.

		drops[dropToUpdate ? 1 : 0].updateDropStatus(newPayloadStatus);
		dropToUpdate = !dropToUpdate;
	}

	/**
	 * Changes duration from a number format to a string time format.
	 * @param duration Time in seconds since start of flight.
	 * @return A string representation of the duration.
	 */
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
}
