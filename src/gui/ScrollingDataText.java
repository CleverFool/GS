package gui;

import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
 * Justin Fu
 * github.com/jf21
 * 1/13/16
 * MFly 
 */

public class ScrollingDataText extends JScrollPane{

	private String data="MFly Data Log";
	private JTextArea textArea;
	
	public ScrollingDataText(){

			textArea = new JTextArea(data);
			textArea.setFont(new Font("Courier", Font.PLAIN, 11));
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
			
			setViewportView(textArea);
	}
	
	public void update(String newData){
		data = data+'\n'+newData;
		textArea.setText(data);
	}
	
	//for use during individual debugging of scrolling data text area
	public static void main(String[] args){
		ScrollingDataText d = new ScrollingDataText();

		JFrame frame = new JFrame(); // frame, you would replace this with the JPanel
	      frame.add(d);   // add the created opject to your Panel/Frame
	      frame.setVisible(true); //set the master frame visible
	      frame.setSize(400, 300); //set your size, should be slightly larger than construcor size
		
		System.out.println("lol lolz lol");
		
		for(int i = 0; i<100; i++){
			d.update("This is an editable JTextArea. " +
				    "A text area is a \"plain\" text component, " +
				    "which means that although it can display text " +
				    "in any font, all of the text is in the same font.");
		}
	}
	
}
