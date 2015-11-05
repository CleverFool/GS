package gui;

import javax.swing.JPanel;
import java.awt.TextField;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class IndividualDropPanel extends JPanel {
	String dropStatus = "Unreleased";
	int dropNumber;
	JLabel dropName;
	TextField dropStatusDisplay;
	public IndividualDropPanel(int dropNumber) {
		this.dropNumber = dropNumber;
		BorderLayout layout = new BorderLayout();
		super.setLayout(layout);

		dropName = new JLabel("Payload " + dropNumber);
		super.add(dropName, BorderLayout.NORTH);

		dropStatusDisplay = new TextField(dropStatus) {
			// Prevent this from gaining focus. This is read-only so we don't want it 
			// grabbing focus from other objects.
			public boolean isFocusable() {
				return false;
			}
		};
		dropStatusDisplay.setEditable(false);
		super.add(dropStatusDisplay, BorderLayout.CENTER);
	}

	public void updateDropStatus(String newStatus) {
		dropStatus = newStatus;
		dropStatusDisplay.setText(newStatus);
	}

}
