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

	private final int FONT_SIZE = 30;

	private JLabel dropName;
	
	private JTextArea dropStatusDisplay;
	private final String DEFAULT_STATUS = "Unreleased";
	
	public IndividualDropPanel(int payloadNumber) {
		
		// Create Layout for Panel
		
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		
		// Setup Drop Name for Payload
		
		dropName = new JLabel("Payload " + payloadNumber);
		add(dropName);
		
		// Set new Font with size and alignment
		
		Font labelFont = dropName.getFont();
		Font newFont = new Font(labelFont.getName(), Font.PLAIN, FONT_SIZE);

		dropName.setFont(newFont);
		dropName.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Setup Drop Status Display

		dropStatusDisplay = new JTextArea(DEFAULT_STATUS) {
			private static final long serialVersionUID = 1L;

			// Prevent this from gaining focus. This is read-only so we don't want it 
			// grabbing focus from other objects.
			public boolean isFocusable() {
				return false;
			}
		};
		
		// Set Status Display Properties
		
		dropStatusDisplay.setFont(newFont);
		dropStatusDisplay.setEditable(false);
		dropStatusDisplay.setBackground(Color.ORANGE);
		add(dropStatusDisplay);
	}

	public void updateDropStatus(String newStatus) {
		dropStatusDisplay.setText(newStatus);
		dropStatusDisplay.setBackground(Color.GREEN);
	}

}
