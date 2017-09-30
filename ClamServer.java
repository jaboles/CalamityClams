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
	private ServerSocket servsock;	// Handles the server end of communications, for clients to connect to.
	public static final String DEFAULT_IP = "127.0.0.1";	// If running a server, run on this machine.
	public static final int DEFAULT_PORT = 2009;			// This is the port to run the server on.
	private ArrayList threadPool;					// A threadpool is created to store network interfaces,
	private ArrayList clients;						// and a list of clients to hold connected players.
	private GameManager mgr;						// It is the communications server that runs the main
	private int nextId;								// game server class, which handles concurrent play.
	private int poolSize = 3;						// By default, there are three threads in the pool.
	
	public ClamServer(int port) throws Exception
	{
		this.setDaemon(true);	// This server, unless running standalone, should quit when the app does.
		nextId = 0;
		clients = new ArrayList();
		mgr = new GameManager(this);
		servsock = new ServerSocket(port);
		//Start of threadPooling code
		threadPool = new ArrayList();
		for(int i = 0; i < poolSize; i++)
		{
			ServerNetworkInterface newClient = new ServerNetworkInterface(this);
			threadPool.add(newClient);
			System.out.println("Added a new Thread to the pool, new poolsize = " + threadPool.size());
		}
		//End of threadPooling code
	}
	
	public ClamServer() throws Exception
	{
		this(DEFAULT_PORT);	// By default this uses the port 2009
	}

		public void run() {
		// thread by request.
		System.out.println("Running the Server Client with threads available = " + threadPool.size());
		while (true) {
			Socket client = null;
			try {
				client = servsock.accept();
			} catch (IOException e) {
				break;
			}

			/* THREAD PER REQUEST
			ServerNetworkInterface handler = new ServerNetworkInterface(this);
			handler.acceptClient(client, nextId);
			nextId++;
			clients.add(handler);
			handler.start();
			*/


			// THREAD POOLING
			try
			{
				ServerNetworkInterface handler;
				synchronized (threadPool)
				{
					if(threadPool.size() == 0)
					{
						handler = new ServerNetworkInterface(this);
						System.out.println("There are no available threads, making new threads");
					}
						else
                        {
								handler = (ServerNetworkInterface)threadPool.get(0);
								threadPool.remove(0);
						}
					}
				handler.acceptClient(client, nextId);
				nextId++;
				clients.add(handler);
				handler.start();
			}
			catch (IndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
			System.out.println("Finished the server client run method with threads available = " + threadPool.size());
			//End of threadPooling			
		}
	}
	
	public void disconnected(int id) {
		System.out.println("client # "+id+" has disconnected from the server.");
		for (int i = 0; i < clients.size(); i++) {
			if (((ServerNetworkInterface)clients.get(i)).getPlayerId() == id) {
				//ThreadPool code
				ServerNetworkInterface handler = (ServerNetworkInterface)clients.get(i);
				threadPool.add(handler);
				//End of threadPool code
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
}
