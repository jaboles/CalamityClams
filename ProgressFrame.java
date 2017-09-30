//
//  ProgressFrame.java
//  Jonathan Boles

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// A simple progress bar class to provide minor user feedback as the system waits for clients.

public class ProgressFrame extends JFrame {
	private JLabel text;						// The text for the progress bar to display.
	private JProgressBar progressBar;			// The actual UI instance.
	private JPanel panel;						// The window for it to run in.
	
	public ProgressFrame() {
		text = new JLabel("                           Please wait                           ");
		text.setHorizontalAlignment(SwingConstants.CENTER);
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		panel = new JPanel(new GridLayout(2,1));		// This sets up the progress bar's UI.
		panel.add(text);
		panel.add(progressBar);
		getContentPane().add(panel);
		pack();
	}
	
	public void show(String s) {
		setText(s);						// The progress bar can be run with whatever text is desired,
		show();							// specified when it is shown.
	}
	
	public void setText(String s) {
		text.setText(s);				// The text shown by the element can also be changed while it
	}									// is being used.
}
