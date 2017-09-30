//
//  ProgressFrame.java
//  Jonathan Boles

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ProgressFrame extends JFrame {
	private JLabel text;
	private JProgressBar progressBar;
	private JPanel panel;
	
	public ProgressFrame() {
		text = new JLabel("                           Please wait                           ");
		text.setHorizontalAlignment(SwingConstants.CENTER);
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		panel = new JPanel(new GridLayout(2,1));
		panel.add(text);
		panel.add(progressBar);
		getContentPane().add(panel);
		pack();
	}
	
	public void show(String s) {
		setText(s);
		show();
	}
	
	public void setText(String s) {
		text.setText(s);
	}
}
