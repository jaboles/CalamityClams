//
//  GameManager.java
//  CalamityClams
//
//  Created by Ashnil Kumar (with help from Jonathan Boles) on Sun Oct 24 2004.
//  Copyright (c) 2004 Clammers!. All rights reserved.
//

import java.util.*;

public class GameManager
{
	private ClamServer controller; // the server
	private ArrayList clients; // the clients playing the game
	
	private static final int XSIZE = 12; // the number of columns
	private static final int YSIZE = 10; // the number of rows
	private Integer grid[][] = new Integer[XSIZE][YSIZE]; // the grid with XSIZE * YSIZE squares
	
	private static final int XMIN = 0; // the minimum x co-ordinate
	private static final int XMAX = XSIZE - 1; // the maximum x co-ordinate (number - 1)

	private static final int YMIN = 0; // the minimum y co-rodinate
	private static final int YMAX = YSIZE - 1; // the maximum y co-ordinate
	
	private static final int BLANK = 2; // represents a blank grid square
	private static final int CLAM = 1; // represents a clam in a grid square
	private static final int PEARL = 3; // represents a pearl in a grid square
	
	private static final boolean DEBUG = true;
	
	private boolean updating;
	
	public GameManager(ClamServer controller)
	{
		this.controller = controller;
		clients = controller.getClients();
		
		// intilize grid
		for (int x = 0; x < XSIZE; x++)
		{
			for (int y = 0; y < YSIZE; y++)
			{
				grid[x][y] = new Integer(BLANK);
			}
		}
		updating = false;
	}
	
	public synchronized void checkPlayersReady()
	{
		for (int i = 0; i < clients.size(); i++)
		{
			if (!((ServerNetworkInterface)clients.get(i)).isReadyToPlay()) return;
		}
		System.out.println("all players ready, begin the game");
		
		for (int i = 0; i < clients.size(); i++)
		{
			((ServerNetworkInterface)clients.get(i)).beginGame();
		}
	}
	
	public synchronized int clamClicked(int x, int y, int what, int id)
	{
		// COARSE GRAINED LOCK -- Lock the entire grid then check the squares that may contribute to a point:
		int points = 0;

		// if the square is already filled, reject the requested entry.
		if (grid[x][y].intValue() != BLANK)
		{   // BLANK == empty square
			return -1;
		}
		grid[x][y] = new Integer(what);
		if (DEBUG) System.out.println("GameManager.clamClicked: Placement successfuly. checking points.");
		
		if (what == CLAM) {
			points = checkClamPoints(x, y);
		} else {
			points = checkPearlPoints(x, y);
		}

		if (DEBUG) System.out.println("GameManager.clamClicked: "+ points + " points scored. Updating clients...");
				
		return points;
	}
	
	private int checkClamPoints(int x, int y)
	{
		// C = a square that needs to be checked to see if it is a clam.
		// P = a square that needs to be checked to see if it is a pearl.
		// N = the new CLAM square.
		//   ---------------------
		//   | C |   | C |   | C |
		//   ---------------------
		//   |   | P | P | P |   |
		//   ---------------------
		//   | C | P | N | P | C |
		//   ---------------------
		//   |   | P | P | P |   |
		//   ---------------------
		//   | C |   | C |   | C |
		//   ---------------------
		// a point is scored for every CPC, in a row, column or diagonal.
		
		int points = 0;
		
		// going clockwise, starting from top left.
		if (x>XMIN+1 &&
			y>YMIN+1 &&
			grid[x-2][y-2].intValue()==CLAM &&
			grid[x-1][y-1].intValue()==PEARL) points++;
			
		if (y>YMIN+1 && 
			grid[x][y-2].intValue()==CLAM && 
			grid[x][y-1].intValue()==PEARL) points++;
		
		if (x<=XSIZE-3 && 
			y>YMIN+1 &&
			grid[x+2][y-2].intValue()==CLAM && 
			grid[x+1][y-1].intValue()==PEARL) points++;
		
		if (x<=XSIZE-3 &&
			grid[x+2][y].intValue()==CLAM &&
			grid[x+1][y].intValue()==PEARL) points++;
		
		if (x<=XSIZE-3 &&
			y<=YSIZE-3 &&
			grid[x+2][y+2].intValue()==CLAM &&
			grid[x+1][y+1].intValue()==PEARL) points++;
		
		if (y<=YSIZE-3 && 
			grid[x][y+2].intValue()==CLAM && 
			grid[x][y+1].intValue()==PEARL) points++;
		
		if (x>XMIN+1 &&
			y<=YSIZE-3 &&
			grid[x-2][y+2].intValue()==CLAM &&
			grid[x-1][y+1].intValue()==PEARL) points++;
		
		if (x>XMIN+1 &&
			grid[x-2][y].intValue()==CLAM &&
			grid[x-1][y].intValue()==PEARL) points++;
		
		return points;
	}
	
	private int checkPearlPoints(int x, int y)
	{
		// C = a square that needs to be checked to see if it is a clam.
		// N = the new PEARL square.
		//   -------------
		//   | C | C | C |
		//   -------------
		//   | C | N | C |
		//   -------------
		//   | C | C | C |
		//   -------------
		// a point is scored for every CPC, in a row, column or diagonal.
		
		int points=0;
		
		if ((x==XMIN) || (x==XMAX))
		{
			// at X edges we can only have | (a column)
			
			if ((y != YMIN) && (y != YMAX))
			{
				// only points when pearl is not placed in a corner
				if ((grid[x][y-1].intValue() == CLAM) && (grid[x][y+1].intValue() == CLAM))
				{
					// increase the points
					points++;
				}
			}
		}
		else if ((y == YMAX) || (y == YMIN))
		{
			// here we have a Y edge but no X edge, so not a corner
			// since it is a Y edge we can only have - (a row)
			
			if ((grid[x-1][y].intValue() == CLAM) && (grid[x+1][y].intValue() == CLAM))
			{
					// increase the points
					points++;
			}
		}
		else // the pearl was not placed at any edge so we can have 4 different cases
		{
			// check the \ diagonal
			if ((grid[x-1][y-1].intValue() == CLAM) && (grid[x+1][y+1].intValue() == CLAM))
			{
				// increase the points
				points++;
			}
			
			// check the / diagonal
			if ((grid[x-1][y+1].intValue() == CLAM) && (grid[x+1][y-1].intValue() == CLAM))
			{
				// increase the points
				points++;
			}
		
			// check the | (column)
			if ((grid[x][y-1].intValue() == CLAM) && (grid[x][y+1].intValue() == CLAM))
			{
				// increase the points
				points++;
			}
			
			// check the  - (row)
			if ((grid[x-1][y].intValue() == CLAM) && (grid[x+1][y].intValue() == CLAM))
			{
				// increase the points
				points++;
			}
		}
		
		return points;
	}
	
	
	public boolean isUpdating()
	{
		return updating;
	}
	
	public ArrayList getClients() {
		return clients;
	}
}
