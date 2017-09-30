//
//  ClamServer.java
//  Jonathan Boles

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class ClamServer extends Thread {
	private ServerSocket servsock;
	public static final String DEFAULT_IP = "127.0.0.1";
	public static final int DEFAULT_PORT = 2009;
	private ArrayList clients;
	private GameManager mgr;
	private int nextId;
	
	public ClamServer(int port) throws Exception
	{
		this.setDaemon(true);
		nextId = 0;
		clients = new ArrayList();
		mgr = new GameManager(this);
		servsock = new ServerSocket(port);
	}
	
	public ClamServer() throws Exception
	{
		this(DEFAULT_PORT);
	}

	public void run() {
		// thread by request.
		while (true) {
			Socket client = null;
			try {
				client = servsock.accept();
			} catch (IOException e) {
				break;
			}
			
			// THREAD PER REQUEST
			ServerNetworkInterface handler = new ServerNetworkInterface(this);
			handler.acceptClient(client, nextId);
			nextId++;
			clients.add(handler);
			handler.start();
			
			
			// THREAD POOLING
			// have something like:
			/*
			ServerNetworkInterface handler = <get it from the pool>
			handler.acceptClient(client, nextId);
			nextId++;
			handler.start();
			*/
		}
	}
	
	public void disconnected(int id) {
		System.out.println("client # "+id+" has disconnected from the server.");
		for (int i = 0; i < clients.size(); i++) {
			if (((ServerNetworkInterface)clients.get(i)).getPlayerId() == id) {
				clients.remove(i);
				break;
			}
		}
	}
	
	public void endGame() {
		// we don't need to end the game for client 0, since it is the server.
		System.out.println("Server shutdown!");
		for (int i = 1; i < clients.size(); i++) {
			System.out.println("shutting down client # "+i+"'s connection.");
			((ServerNetworkInterface)clients.get(i)).close();
		}
		try {
			if (servsock != null) servsock.close();
		} catch (IOException e) {}
	}
	
	public ArrayList getClients() {
		return clients;
	}
	public GameManager getGameManager() {
		return mgr;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		ClamServer cs = new ClamServer();
		// standalone server, so we want to keep it running.
		cs.setDaemon(false);
		cs.start();
	}
	
	public void released(ServerNetworkInterface sni) {
		// for thread pooling.
		
		// here, you would add the interface back to the pool eg.
		// vacantThreads.add(sni);
		
		
		// thread-by-request implementations should have no need to put any code inside this method :)
	}
}
