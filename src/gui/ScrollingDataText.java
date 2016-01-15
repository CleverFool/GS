package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneLayout;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

/*
 * Justin Fu
 * github.com/jf21
 * 1/13/16
 * MFly 
 */

public class ScrollingDataText extends JScrollPane{

	private String data="MFly Data Log";
	private JTextArea textArea;
	private boolean autoScroll = true;
	private HighlightPainter highlightP;
	
	public ScrollingDataText(JCheckBox autoScrollIndicator){

			textArea = new JTextArea(data);
			textArea.setFont(new Font("Courier", Font.PLAIN, 11));
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
			
			
			setViewportView(textArea);
			setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			setPreferredSize(new Dimension(250, 200));
			
			getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
		        public void adjustmentValueChanged(AdjustmentEvent e) { 
		        	if(autoScroll){
		        		e.getAdjustable().setValue(e.getAdjustable().getMaximum());
		        	}
		        }
		    });
			
			addMouseWheelListener(new MouseWheelListener(){
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					// TODO Auto-generated method stub
					autoScroll = false;
					
					if(autoScrollIndicator != null){
					autoScrollIndicator.setSelected(false);
					autoScrollIndicator.setText("Auto Scroll: OFF");
					}
				}
			});
	}
	
	public void toggleAutoScroll(boolean mode){
		autoScroll = mode;
	}
	
	public void update(String newData){
		data = data+newData;
		textArea.setText(data);
	}
	
	//for use during individual debugging of scrolling data text area
	public static void main(String[] args){
		ScrollingDataText d = new ScrollingDataText(null);

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