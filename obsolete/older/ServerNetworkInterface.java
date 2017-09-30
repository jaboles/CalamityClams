//
//  ServerNetworkInterface.java
//  CalamityClams
//
//  Created by Jonathan Boles on Sat Oct 23 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//


import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;


public class ServerNetworkInterface extends Thread{
	private ClamServer controller;
	private int id;
	private Socket sock;
	private PrintWriter out;
	private BufferedReader in;
	private String playerName;
	private int playerScore;
	private boolean readyToPlay;
	
	public static final boolean DEBUG = true;
	
	public ServerNetworkInterface(Socket s, ClamServer controller, int id)
	{
		this.setDaemon(true);
		this.controller = controller;
		this.id = id;
		this.sock = s;
		readyToPlay = false;
		playerScore = 0;
		playerName = "";
	}
	
	public ServerNetworkInterface(ClamServer controller) {
		this.setDaemon(true);
		this.controller = controller;
	}

	public synchronized void acceptClient(Socket s, int id) {
		playerScore = 0;
		playerName = "";
		readyToPlay = false;
		this.sock = s;
		this.id = id;
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true); // true = autoflush
		} catch (Exception e) {
			System.out.println("Server error:");
			System.out.println(e);
			e.printStackTrace();
		}
		
	}

	
	public void run(){
		try  {
			while (sock.isConnected() && !sock.isClosed()) {
				if (in.ready()) {
					synchronized(this) {
						handleCommand(read());
					}
				}
				Thread.yield();
			}
		} catch (Exception e) {
			System.out.println("Server error:");
			System.out.println(e);
			e.printStackTrace();
		} finally {
			disconnected();
			controller.released(this);
		}
	}

	
	private void write(String s) {
		try {
			out.println(s);
			out.flush();
		} catch (Exception e) {
			try {
				sock.close();
			} catch (Exception e2) {}
			disconnected();
			System.out.println("Server error:");
			System.out.println(e);
			e.printStackTrace();
		}
	}
	private String read(){
		String s = null;
		try {
			s = in.readLine();
		} catch (Exception e) {
			try {
				sock.close();
			} catch (Exception e2) {}
			disconnected();
			System.out.println("Server error:");
			System.out.println(e);
			e.printStackTrace();
		}
		return s;
	}
	
	
	public synchronized void handleCommand(String command) {
		if (command.equals("PING")) {
			// Got a ping, return it.
			write("+");
			System.out.println("Ping from # "+id);
		} else if (command.equals("SETNAME")) {
			// Got a ping, return it.
			playerName = read();
			write("+");
			System.out.println("Client # "+id+" set their name to "+playerName);
		} else if (command.equals("READY")) {
			write("+");
			System.out.println("Client # "+id+" has indicated they are ready to start playing");
			readyToPlay = true;
			controller.getGameManager().checkPlayersReady();
		} else if (command.equals("CLICK")) {
			int clickX = Integer.parseInt(read());
			int clickY = Integer.parseInt(read());
			int clickStatus = Integer.parseInt(read());
			System.out.println("Client # "+id+" (name "+playerName+") requested a click at x="+clickX+" y="+clickY+" (status="+clickStatus+")");
			int pointsScored = controller.getGameManager().clamClicked(clickX, clickY, clickStatus, id);
			if (pointsScored >= 0) {
				playerScore += pointsScored;
				write("+");
				controller.getGameManager().updateScore();
			} else {
				write("-");
			}
		} else if (command.equals("DISCONNECT")) {
			write("+");
			try {
				sock.close();
				System.out.println("Server: closed the socket to client # "+id);
			} catch (Exception e) {} // dealing with an exception on socket close is stupid and pointless ;-)		
		}
	}
	
	
	/*private synchronized boolean waitForResponse() {
		String response = read();
		if (response.equals("+")) {
			return true;
		} else if (response.equals("-")) {
			return false;
		} else {
			System.out.println("Server error:unknown response: "+response);
			return false;
		}
	}*/
	
	private synchronized boolean waitForResponse() {
		return waitForResponse(0);
	}
	private synchronized boolean waitForResponse(int level) {
		String response = read();
		if (level == 5) {
			// we have been unable to get a response, after 5 intervening commands from the server.
			// assume failure.
			return false;
		}
		if (response.equals("+")) {
			return true;
		} else if (response.equals("-")) {
			return false;
		} else { // unknown response... treat it as a command from the other end.
			handleCommand(response);
			return waitForResponse(level+1);
		}
	}
	


	public synchronized void disconnected() {
		System.out.println("Client # "+id+" disconnected.");
		controller.disconnected(id);
	}

	public int getPlayerId() {
		return id;
	}
	public String getPlayerName() {
		return playerName;
	}
	public boolean isReadyToPlay() {
		return readyToPlay;
	}
	public int getPlayerScore() {
		return playerScore;
	}
	
	
	
	// server -> client commands
	public synchronized void beginGame() {
		write("BEGINGAME");
		waitForResponse();
	}
	public void placeObject(int x, int y, int what) {
		write("PLACEOBJECT");
		write(Integer.toString(x));
		write(Integer.toString(y));
		write(Integer.toString(what));
		waitForResponse();
	}

	public synchronized void close() {
		write("DISCONNECT");
		waitForResponse();
		try {
			sock.close();
			System.out.println("Server: closed the socket to client # "+id);
		} catch (Exception e) {} // dealing with an exception on socket close is stupid and pointless ;-)		
	}
	
	public void updateScore(String[] names, int[] scores) {
		write("SCORES");
		write(Integer.toString(names.length));
		for (int i = 0; i < names.length; i++) {
			write(names[i]);
			write(Integer.toString(scores[i]));
		}
		waitForResponse();
	}
}
