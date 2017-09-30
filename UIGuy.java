//
//  UIGuy.java
//  CalamityClams
//
//  Created by Liviu Constantinescu on 23/10/04.
//  Copyright 2004 Liviu Constantinescu. All rights reserved.
//

import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class UIGuy implements ActionListener {
	private CalamityClams	controller;	// The UIGuy links back to his controller (the Game),
										// but he alone links to the application's main
    private JFrame			mainWindow;	// JFrame object, and the JLayeredPane used to manage
    private JLayeredPane	layers;		// the overlapping Components. Thus and by using a pixel
										// based co-ordinate conversion system, the user interface
    private JLabel			background;	// remains distinct from the main simulation code.
    private final int		backSizeX = 640, backSizeY = 502;
	
	private boolean gameActive = false;
    
    private ClamLabel		clamGrid[][];
    private final int		gridX = 12, gridY = 10;
	private final int		gridOffsetX = 17, gridOffsetY = 76;
	private final int		gridSquareSide = 38;
    
    protected ImageIcon		iBackground, iClam, iPearl, iEmpty;
	
	private final Integer	BACK_LAYER = new Integer(1);	// These layer constants are used as
    private final Integer	CLAM_LAYER = new Integer(2);	// z-values in the JLayeredPane system.
	private final Integer	BUTTON_LAYER = new Integer(3);
    private final Integer	TOP_LAYER = new Integer(4);
    
	private final Rectangle backBounds			= new Rectangle(0, 0, backSizeX, backSizeY);
	// for reference, grid panel bounds are Rectangle(17, 76, 456, 380);
	private final Rectangle	scorePanelBounds	= new Rectangle(485, 108, 143, 198);
	// For reference, full button panel bounds are Rectangle(485, 343, 143, 112);
	private final Rectangle	button1Bounds	= new Rectangle(490, 351, 135, 30);
	private final Rectangle	button2Bounds	= new Rectangle(490, 386, 135, 30);
	private final Rectangle	button3Bounds	= new Rectangle(490, 421, 135, 30);
	
    private JLabel			scores;
	private final int		numScoresDisplayed = 5;
    
    private JButton			hostGame, joinGame, exitGame;
	
	private ProgressFrame	progress;
	
	private ClamServer cs; // for hosted games
		
	public UIGuy(CalamityClams passed)
    {
        try {UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());}catch(Exception e) {;}
	
		progress = new ProgressFrame();
		
		controller	= passed;
        mainWindow	= new JFrame("Calamity Clams!");
        layers		= new JLayeredPane();
		
		iBackground = new ImageIcon("ClamBack.jpg");
		iEmpty = new ImageIcon("empty.gif");
		iClam = new ImageIcon("clam.gif");
		iPearl = new ImageIcon("pearl.gif");
		
        background = new JLabel(iBackground);
        
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
		
		scores = new JLabel("  Player Name:  0 points");
		
		clamGrid = new ClamLabel[gridX][gridY];
        for (int i = 0; i < clamGrid.length; i++)
        {
            for (int j = 0; j < clamGrid[i].length; j++)
			{
				clamGrid[i][j] = new ClamLabel(i, j, iEmpty, this);
			}
        }
        
        hostGame = new JButton("Host Game");
        joinGame = new JButton("Join Game");
        exitGame = new JButton("Exit Game");
        
        buttonPanel.add(hostGame);
        buttonPanel.add(joinGame);
        buttonPanel.add(exitGame);
		
		mainWindow.setSize(backSizeX, backSizeY);	// set to the size of the background
        
        Dimension backDim = new Dimension(backSizeX, backSizeY);
        
        layers.setMinimumSize(backDim);
        layers.setMaximumSize(backDim);
        layers.setPreferredSize(backDim);
        
        layers.add(background, BACK_LAYER);
		layers.add(scores, BUTTON_LAYER);
		layers.add(hostGame, BUTTON_LAYER);
		layers.add(joinGame, BUTTON_LAYER);
		layers.add(exitGame, BUTTON_LAYER);
        
        background.setBounds(backBounds);
		scores.setBounds(scorePanelBounds);
		hostGame.setBounds(button1Bounds);
		joinGame.setBounds(button2Bounds);
		exitGame.setBounds(button3Bounds);
		
        for (int i = 0; i < clamGrid.length; i++)
        {
            for (int j = 0; j < clamGrid[i].length; j++)
			{
				layers.add(clamGrid[i][j], CLAM_LAYER);
				clamGrid[i][j].setBounds(
											(gridOffsetX + gridSquareSide * i), // X
											(gridOffsetY + gridSquareSide * j), // Y
											(gridSquareSide), // Width
											(gridSquareSide) // Height
										);
			}
        }
        
        mainWindow.getContentPane().add(layers);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.show();
        
        hostGame.addActionListener(this);
		joinGame.addActionListener(this);
		exitGame.addActionListener(this);
		
		if(GameManager.TESTER == true) TheCaptain.allHandsOnDeck(clamGrid);
	}
	
	public boolean clamClicked(int x, int y, int status)
	{
		// if we are not in an active game then exit it.
		if (!gameActive) return false;
		
		return controller.getNetworkInterface().clamClicked(x, y, status);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Exit Game"))
		{
			System.exit(0);
		} else if (e.getActionCommand().equals("Host Game")) {
			hostGame.setEnabled(false);
			joinGame.setEnabled(false);
			progress.show("Starting server...");
			
			try
			{
				cs = new ClamServer();
				cs.start();
				controller.startGame(ClamServer.DEFAULT_IP, ClamServer.DEFAULT_PORT);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(null, ex);
				ex.printStackTrace();
				hostGame.setEnabled(true);
				joinGame.setEnabled(true);
				progress.hide();
			}
				
		} else if (e.getActionCommand().equals("Join Game")) {
			String ip = JOptionPane.showInputDialog(null, "Enter the IP address to connect to.");
			if (ip == null || ip.trim().equals("")) {
				return;
			}
			
			int port = ClamServer.DEFAULT_PORT;
			
/*			String portNo = JOptionPane.showInputDialog("Enter the port number to connect to:", "2009");
			if (portNo == null || portNo.trim().equals(""))
			{
				progress.hide();
				return;
			}
			
			try
			{
				port = Integer.parseInt(portNo);
			}
			catch (NumberFormatException ex)
			{
				JOptionPane.showMessageDialog(null,"The port number was in the wrong format: WXYZ expected.");
				return;
			}*/
			
			hostGame.setEnabled(false);
			joinGame.setEnabled(false);
			progress.show("Connecting to server...");
			try
			{
				controller.startGame(ip,port);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(null, ex);
				ex.printStackTrace();
				hostGame.setEnabled(true);
				joinGame.setEnabled(true);
				progress.hide();
			}
			
		} else if (e.getActionCommand().equals("Disconnect")) {
			controller.endGame();
			if (cs != null) {
				cs.endGame();
				cs = null;
			}
		}
	}
	
	public void connected(){
		progress.setText("Waiting for other players");
		String playerName;
		
		if(GameManager.TESTER == true)
		{
			playerName = "Picard";
		}
		else
		{
			playerName = JOptionPane.showInputDialog(null, "Please enter your name.");
		
			if (playerName == null || playerName.trim().equals("")) {
				controller.endGame();
				if (cs != null) {
					cs.endGame();
					cs = null;
				}
				progress.hide();
				return;
			}
		}
		controller.getNetworkInterface().setPlayerName(playerName);
		
		JOptionPane.showMessageDialog(null, "Please click OK when you are ready to begin!");
		controller.getNetworkInterface().ready();
	}
	
	public void beginGame() {
		progress.hide();
		if(GameManager.TESTER == true)
		{
			gameActive = true;
			TheCaptain.makeItSo();
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Let the games begin!");
			gameActive = true;
		}
		exitGame.setText("Disconnect");
	}
	
	public void endGame() {
		gameActive = false;
		hostGame.setEnabled(true);
		joinGame.setEnabled(true);
		exitGame.setText("Exit Game");

		for (int i = 0; i < clamGrid.length; i++)
        {
            for (int j = 0; j < clamGrid[i].length; j++)
			{
				clamGrid[i][j].setStatus(2);
				clamGrid[i][j].updateIcon();
			}
        }
		
		JOptionPane.showMessageDialog(null, "Disconnected from server.");
		
	}
	
	public void placeObject(int x, int y, int status) { //another player placed an object
		clamGrid[x][y].setStatus(status);
	}
	
	public void updateScore(String s) {
		scores.setText(s);
	}
}
