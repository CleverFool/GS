package panels;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.Color;

public class IndividualDropPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	String dropStatus = "Unreleased";
	int dropNumber;

	private final int FONT_SIZE = 30;

	JLabel dropName;
	JTextArea dropStatusDisplay;
	public IndividualDropPanel(int dropNumber) {
		this.dropNumber = dropNumber;
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		super.setLayout(layout);

		dropName = new JLabel("Payload " + dropNumber);
		super.add(dropName);
		Font labelFont = dropName.getFont();
		Font newFont = new Font(labelFont.getName(), Font.PLAIN, FONT_SIZE);

		dropName.setFont(newFont);
		dropName.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		

		dropStatusDisplay = new JTextArea(dropStatus) {
			private static final long serialVersionUID = 1L;

			// Prevent this from gaining focus. This is read-only so we don't want it 
			// grabbing focus from other objects.
			public boolean isFocusable() {
				return false;
			}
		};
		dropStatusDisplay.setFont(newFont);
		dropStatusDisplay.setEditable(false);
		dropStatusDisplay.setBackground(Color.ORANGE);
		super.add(dropStatusDisplay);
	}

	public void updateDropStatus(String newStatus) {
		dropStatus = newStatus;
		dropStatusDisplay.setText(newStatus);
		dropStatusDisplay.setBackground(Color.GREEN);
	}

}