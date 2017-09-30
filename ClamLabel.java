//
//  ClamLabel.java
//  CalamityClams
//
//  Created by Liviu Constantinescu on 30/10/04.
//  Copyright 2004 Liviu C. All rights reserved.
//

import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClamLabel extends JLabel implements MouseListener {
	public final int	sEmpty = 2, sClam = 1, sPearl = 3;	
	private int			status = sEmpty;
	private int			gridX;
	private int			gridY;
	private UIGuy		controller;
	
	public ClamLabel (int x, int y, ImageIcon initialIcon, UIGuy theGuy)
	{
		super(initialIcon);
		
		gridX = x;
		gridY = y;
		
		controller = theGuy;
		
		this.addMouseListener(this);
	}
	
	public int getStatus()
	{
		return status;
	}
	public void setStatus(int s) {
		status = s;
		updateIcon();
	}
	
	public void mousePressed(MouseEvent e) {
		;
	}
	public void mouseReleased(MouseEvent e) {
		;
	}
	public void mouseEntered(MouseEvent e) {
		;
	}
	public void mouseExited(MouseEvent e) {
		;
	}
	
	public void mouseClicked(MouseEvent e) {
		int button = e.getButton();
		if((e.getMouseModifiersText(e.getModifiers()).startsWith("Shift")) && (status == sEmpty))
		{
			button = sPearl;
		}
		if (status == sEmpty) {  //dont do anything if the cell has already been clicked
			if (controller.clamClicked(gridX, gridY, button))
			{ // click needs to be validated with the server
				status = button;
				updateIcon();
			}
		}
	}
	
	// For testing:
	public void attackPatternPicardDeltaOne(int button) {
		if (status == sEmpty) {  //dont do anything if the cell has already been clicked
			if (controller.clamClicked(gridX, gridY, button))
			{ // 'click' needs to be validated with the server
				status = button;
				updateIcon();
			}
		}
	}
	
	protected void updateIcon()
	{
		switch(status)
		{
			case sClam:
				setIcon(controller.iClam);
				break;
			case sPearl:
				setIcon(controller.iPearl);
				break;
			default:
				setIcon(controller.iEmpty);
		}
	}
}