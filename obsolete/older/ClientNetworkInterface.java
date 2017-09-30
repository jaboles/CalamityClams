//
//  ClientNetworkInterface.java
//  Jonathan Boles

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class ClientNetworkInterface extends Thread {
	private CalamityClams controller;
	private Socket sock;
	private PrintWriter out;
	private BufferedReader in;
	
	public ClientNetworkInterface(CalamityClams controller)
	{
		this.setDaemon(true);
		this.controller = controller;
	}
	
	// throw exception?
	public boolean connect(String ip, int port) throws Exception {
		if (port==0) port=2009; //default to port 2009
		
		sock = new Socket(ip, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new PrintWriter(sock.getOutputStream(), true); // true = autoflush
		controller.getUI().connected();
		return true;
	}

	
	public void run(){
		try {
			while (sock.isConnected() && !sock.isClosed()) {
				if (in.ready()) {
					synchronized(this) {
						handleCommand(read());
					}
				}
				Thread.yield();
			}
		} catch (Exception e) {
		} finally {
			controller.getUI().endGame();
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
			System.out.println("Client error:");
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
			System.out.println("Client error:");
			System.out.println(e);
			e.printStackTrace();
		}
		return s;
	}
	
	
	
	public synchronized void handleCommand(String command) {
		if (command.equals("PING")) {
			// Got a ping, return it.
			write("+");
		} else if (command.equals("BEGINGAME")) {
			write("+");
			controller.getUI().beginGame();
		} else if (command.equals("PLACEOBJECT")) {
			int x = Integer.parseInt(read());
			int y = Integer.parseInt(read());
			int status = Integer.parseInt(read());
			write("+");
			controller.getUI().placeObject(x, y, status);
		} else if (command.equals("DISCONNECT")) {
			write("+");
			try {
				sock.close();
				System.out.println("Client: closed the socket");
			} catch (Exception e) {} // dealing with an exception on socket close is stupid and pointless ;-)
		} else if (command.equals("SCORES")) {
			int playerCount = Integer.parseInt(read());
			String scoreString = "<html>";
			for (int i = 1; i <= playerCount; i++) {
				scoreString = scoreString + read() + ":   " + read() + "<br>";
			}
			scoreString = scoreString + "</html>";
			write("+");
			controller.getUI().updateScore(scoreString);
		}
	}
	
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
	
	public synchronized boolean setPlayerName(String name) {
		write("SETNAME");
		write(name);
		return waitForResponse();
	}
	public synchronized void ready() {
		write("READY");
		waitForResponse();
	}
	public synchronized boolean clamClicked(int x, int y, int status) {
		write("CLICK");
		write(Integer.toString(x));
		write(Integer.toString(y));
		write(Integer.toString(status));
		return waitForResponse();
	}
	
	public synchronized void close() {
		write("DISCONNECT");
		waitForResponse();
		try {
			sock.close();
			System.out.println("Client: closed the socket");
		} catch (Exception e) {} // dealing with an exception on socket close is stupid and pointless ;-)		
	}
	
	
}
