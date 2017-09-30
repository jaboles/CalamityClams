//
//  SplashScreen.java
//  CalamityClams
//
//  Created by Liviu Constantinescu on Mon Oct 13 2003.
//  Copyright (c) 2003 Liviu Constantinescu. All rights reserved.
//

import javax.swing.*;
import java.util.Timer;
import java.awt.event.*;

public class SplashScreen extends Thread implements FocusListener {
    boolean timeout = false;
    JFrame splashWin;
    JLabel splashImage;

    public SplashScreen()
    {
        splashWin = new JFrame("Welcome to Calamity Clams!");
        splashImage = new JLabel(new ImageIcon("howtoplay.jpg"));
        
        splashWin.setResizable(false);
        splashWin.setUndecorated(true);
        splashWin.getContentPane().add(splashImage);
        splashWin.pack();
        splashWin.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        int screenWidth = (int)splashWin.getGraphicsConfiguration().getBounds().getWidth();
        int screenHeight = (int)splashWin.getGraphicsConfiguration().getBounds().getHeight();
        int windowWidth = (int)splashWin.getBounds().getWidth();
        int windowHeight = (int)splashWin.getBounds().getHeight();
        
        splashWin.setBounds((screenWidth - windowWidth) / 2, (screenHeight - windowHeight) /  2, windowWidth, windowHeight);
        
        splashWin.show();
        splashWin.addFocusListener(this);
        start();
    }
     
    public void run()
    {
        (new Timer(true)).schedule(new DelayStart(this), 6000);
        
        while(!timeout)
        {
            ;
        }
        
        if(splashWin.isDisplayable()) splashWin.dispose();
    }
    
    public void focusGained(FocusEvent e) {;}
    
    public void focusLost(FocusEvent e) {
        splashWin.toFront();
    }
    
    public void endWindow()
    {
        timeout = true;
    }
}