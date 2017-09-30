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

	private int clickX = 0;
	private int clickY = 0;
	private int clickStatus = 0;
	
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
				String command = "";
				if (in.ready()) {
					synchronized(this) {
						command=read();
						handleCommand(command);
					}
				}
				if (command.equals("CLICK")) {
					updateClients(clickX, clickY, clickStatus, id);
					updateScores();
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
			//if (DEBUG) System.out.println("server >>> client # "+id+" "+s);
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
			//if (DEBUG) System.out.println("server <<< client # "+id+" "+s);
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
		System.out.println("ServerNetworkInterface.handleCommand: got command "+command);
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
				clickX = Integer.parseInt(read());
				clickY = Integer.parseInt(read());
				clickStatus = Integer.parseInt(read());
				System.out.println("Client # "+id+" (name "+playerName+") requested a click at x="+clickX+" y="+clickY+" (status="+clickStatus+")");
				int pointsScored = controller.getGameManager().clamClicked(clickX, clickY, clickStatus, id);
				if (pointsScored >= 0) {
					playerScore += pointsScored;
					write("+");
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
	
	
	private boolean waitForResponse() {
		return waitForResponse(0);
	}
	private boolean waitForResponse(int level) {
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
			if (DEBUG) System.out.println("ServerNetworkInterface.waitForResponse: unknown response: "+response);
			handleCommand(response);
			if (response.equals("CLICK")) {
				updateClients(clickX, clickY, clickStatus, id);
				updateScores();
			}
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
	public synchronized void placeObject(int x, int y, int what) {
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



	private void updateClients(int x, int y, int what, int id)
	{
		ArrayList clients = controller.getGameManager().getClients();
		for (int i = 0; i < clients.size(); i++)
		{
			ServerNetworkInterface sni = (ServerNetworkInterface)clients.get(i);
			// update every other client except ourselves.
			if (sni.getPlayerId() != id) {
				if (DEBUG) System.out.println("GameManager.updateClients: updating client "+sni.getPlayerId());
				sni.placeObject(x, y, what);
			}
		}
	}
	
	private void updateScores() {
		ArrayList clients = controller.getGameManager().getClients();
		for (int i = 0; i < clients.size(); i++) {
			((ServerNetworkInterface)clients.get(i)).updateScore();
		}
	}
	
	private synchronized void updateScore() {
		ArrayList clients = controller.getGameManager().getClients();
		ServerNetworkInterface sni;
		write("SCORES");
		write(Integer.toString(clients.size()));
		for (int i = 0; i < clients.size(); i++) {
			sni = (ServerNetworkInterface)clients.get(i);
			write(sni.getPlayerName());
			write(Integer.toString(sni.getPlayerScore()));
		}
		waitForResponse();
	}
}
