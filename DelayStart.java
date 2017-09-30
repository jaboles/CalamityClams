//
//  DelayText.java
//  CalamityClams
//
//  Created by Liviu Constantinescu on Mon Oct 13 2003.
//  Copyright (c) 2003 Liviu Constantinescu. All rights reserved.
//

import java.util.Timer;
import java.util.TimerTask;

class DelayStart extends TimerTask
{
    private SplashScreen	controller;
    
    /**
    * Creates a timer to remove the startup screen
    * after a pre-specified interval.
    * @param passed The SplashScreen class.
    */
    public DelayStart(SplashScreen passed)
    {
        controller = passed;
    }
    
    /**
    * Causes the splash screen to end.
    */
    public void run()
    {
        controller.endWindow();
    }
    
    // Usage: (new Timer(true)).schedule(new DelayStart(controller), delay);
}