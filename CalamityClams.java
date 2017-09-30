//
//  CalamityClams.java
//  CalamityClams
//
//  Created by Liviu Constantinescu on 23/10/04.
//  Copyright (c) 2004 Liviu Constantinescu. All rights reserved.
//
import java.util.*;

/*
	This version of the Calamity Clams code that of Liviu Constantinescu
	Features:	* Thread Pooling
				* Coarse-grained Locking
*/

public class CalamityClams {

	private	UIGuy					theGUI;
	private ClientNetworkInterface	cni;
	
	public CalamityClams()
	{
		theGUI = new UIGuy(this);
	}

    public static void main (String args[]) {
		new SplashScreen();
		new CalamityClams();
    }
	
	// check for excepton for false return?
	public ClientNetworkInterface startGame(String ip, int port) throws Exception {
		cni = new ClientNetworkInterface(this);
		cni.connect(ip, port);
		cni.start();
		return cni;
	}
	public void endGame() {
		cni.close();
	}
	
	public ClientNetworkInterface getNetworkInterface() {
		return cni;
	}
	public UIGuy getUI() {
		return theGUI;
	}
}
