//
//  TheCaptain.java
//  CalamityClams
//
//  Created by Liviu Constantinescu on 30/10/04.
//  Copyright 2004 Liviu C. All rights reserved.
//

import java.util.*;
import javax.swing.JLabel;

public class TheCaptain {

	private static ClamLabel	galaxy[][];
	private static int			cahntDewItCaptain = 0;
	private	static boolean		engagingKlingons = false;
	private static int			quadrant = 0;
	private static int			starSystem = -1;
	private static int			phasers = 0;
	private static final int	HEAT = 2, STUN = 3, KILL = 1;
	private static long			stardate;
	private static ArrayList	portDeflectorArray = new ArrayList();

//                  xxxXRRRMMMMMMMMMMMMMMMxxx,.
//              xXXRRRRRXXXVVXVVXXXXXXXRRRRRMMMRx,
//            xXRRXRVVVVVVVVVVVVVVVXXXXXRXXRRRMMMMMRx.
//          xXRXXXVVVVVVVVVVVVVVVVXXXXVXXXXXXRRRRRMMMMMxx.
//        xXRRXXVVVVVttVtVVVVVVVVVtVXVVVVXXXXXRRRRRRRMMMMMXx
//      xXXRXXVVVVVtVttttttVtttttttttVXXXVXXXRXXRRRRRRRMMMMMMXx
//     XRXRXVXXVVVVttVtttVttVttttttVVVVXXXXXXXXXRRRRRRRMMMMMMMMVx
//    XRXXRXVXXVVVVtVtttttVtttttittVVVXXVXVXXXRXRRRRRMRRMMMMMMMMMX,
//   XRRRMRXRXXXVVVXVVtttittttttttttVVVVXXVXXXXXXRRRRRMRMMMMMMMMMMM,
//   XXXRRRRRXXXXXXVVtttttttttttttttttVtVXVXXXXXXXRRRRRMMMMMMMMMMMMM,
//   XXXXRXRXRXXVXXVtVtVVttttttttttttVtttVXXXXXXXRRRRRMMMMMMMMMMMMMMMR
//   VVXXXVRVVXVVXVVVtttititiitttttttttttVVXXXXXXRRRRRMRMMMMMMMMMMMMMMV
//   VttVVVXRXVVXtVVVtttii|iiiiiiittttttttitXXXRRRRRRRRRRMMMMMMMMMMMMMM
//   tiRVVXRVXVVVVVit|ii||iii|||||iiiiiitiitXXXXXXXXRRRRRRMMMMMMMMMMMMM
//    +iVtXVttiiii|ii|+i+|||||i||||||||itiiitVXXVXXXRRRRRRRRMMMMMMRMMMX
//    `+itV|++|tttt|i|+||=+i|i|iiii|iiiiiiiitiVtti+++++|itttRRRRRMVXVit
//     +iXV+iVt+,tVit|+=i|||||iiiiitiiiiiiii|+||itttti+=++|+iVXVRV:,|t
//     +iXtiXRXXi+Vt|i||+|++itititttttttti|iiiiitVt:.:+++|+++iXRMMXXMR
//     :iRtiXtiV||iVVt||||++ttittttttttttttttXXVXXRXRXXXtittt|iXRMMXRM
//      :|t|iVtXV+=+Xtti+|++itiiititittttVttXXXXXXXRRRXVtVVtttttRRMMMM|
//        +iiiitttt||i+++||+++|iiiiiiiiitVVVXXRXXXRRRRMXVVVVttVVVXRMMMV
//         :itti|iVttt|+|++|++|||iiiiiiiittVVXRRRMMMMMMRVtitittiVXRRMMMV
//           `i|iitVtXt+=||++++|++++|||+++iiiVVXVRXRRRV+=|tttttttiRRRMMM|
//             i+++|+==++++++++++++++|||||||||itVVVViitt|+,,+,,=,+|itVX'
//              |+++++.,||+|++++=+++++++|+|||||iitt||i||ii||||||itXt|
//              t||+++,.=i+|+||+++++++++++++|i|ittiiii|iiitttttXVXRX|		  ____________________________
//              :||+++++.+++++++++|++|++++++|||iii||+:,:.-+:+|iViVXV		_/                            \
//              iii||+++=.,+=,=,==++++++++++|||itttt|itiittXRXXXitV'	<	  All hands, battle stations.  |
//             ;tttii||++,.,,,.,,,,,=++++++++++|iittti|iiiiVXXXXXXV			-- Arm photon torpedoes! Gent- /
//            tVtttiii||++++=,,.  . ,,,=+++++++|itiiiiiii||||itttVt			  \ lemen, we're about to test/
//           tVVttiiiii||||++++==,. ..,.,+++=++iiiiiitttttVVXXRRXXV			   \ some CalamityClams Code!/
//        ..ttVVttitttii||i|||||+|+=,.    .,,,,==+iittVVVXRRMXRRRV				------------------------
//...'''ittitttttitVttttiiiiii|ii|++++=+=..... ,.,,||+itiVVXXVXV
//      ,|iitiiitttttttiiiii||ii||||||||+++++,.i|itVt+,,=,==.........
//        ,|itiiiVtVtiii||iiiiii|||||||++||||tt|VXXRX|  ....  ..     ' ' '.
//          ,,i|ii||i||+|i|i|iiiiiiii||||ittRVVXRXRMX+, .  ...   .         ,
//    .       .,+|++|||||ii|i|iiiitttVVttXVVXVXRRRRXt+. .....  . .       ,. .
//  . .          ,,++|||||||i|iiitVVVXXXXVXXVXXRRRV+=,.....  ....  ..       ..
//                  .,,++|||i|iittXXXXRMViRXXXXRVt+=, ..    ...... .        ..
//                   ,XX+.=+++iitVVXXXRXVtXXVRRV++=,..... .,, .              .
//            ....       +XX+|i,,||tXRRRXVXti|+++,,. .,,. . . .. .      . ....
//  . .          .      ..  ..........++,,..,...,.... ..             .. ...

	public static void allHandsOnDeck(ClamLabel timeSpaceAnomalyDetected[][])
	{
		galaxy = timeSpaceAnomalyDetected;
	}
	
	public static void hailingFrequenciesOpen()
	{
		tryDiplomaticSolution();
		
		if(engagingKlingons) firePhasers();
		else reportToStarFleet();
	}
	public static void hailingFrequenciesOpen(boolean dilithiumCrystalsCharged)
	{
		if(!dilithiumCrystalsCharged) cahntDewItCaptain++;
		
		hailingFrequenciesOpen();
	}
	
	public static void makeItSo()
	{
		engagingKlingons = true;
		
		firePhasers();
	}
	
	public static void firePhasers()
	{
		switch(phasers)
		{
			case 0:
			case STUN:
				setPhasersTo(KILL);
				break;
			case KILL:
				setPhasersTo(STUN);
				break;
		}
		
		captainsLog(stardate(200410.30));
		
		int whereNoManHasGoneBefore = starSystem + 1;
		
		warpNineTo(whereNoManHasGoneBefore);
		
		galaxy[quadrant][starSystem].attackPatternPicardDeltaOne(phasers);
	}
	
	public static void setPhasersTo(int setting)
	{
		phasers = setting;
	}
	
	public static void warpNineTo(int deltaQuadrant)
	{
		if(deltaQuadrant >= galaxy[quadrant].length)
		{
			quadrant += 1;
			starSystem = 0;
		}
		else
		{
			starSystem = deltaQuadrant;
		}
		
		if((quadrant == galaxy.length - 1) && (starSystem == galaxy[galaxy.length - 1].length - 1))
		{
			engagingKlingons = false;
		}
	}
	
	public static void tryDiplomaticSolution()
	{
		portDeflectorArray.add(reroutedPower());
	}
	
	public static void reportToStarFleet()
	{
		int admiral = 0;
		
		for(int erstellarPeace = 0; erstellarPeace < portDeflectorArray.size(); erstellarPeace++)
		{
			admiral += ((Integer)portDeflectorArray.get(erstellarPeace)).intValue();
		}
		
		admiral = admiral / portDeflectorArray.size();
		
		System.out.println("Testing complete: average instruction time " + admiral + " milliseconds. " + cahntDewItCaptain + " transmission errors.");
	}
	
	public static long stardate(double numberTheComputerProbablyIgnores)
	{
		return (new Date()).getTime();
	}
	
	public static void captainsLog(long logDate)
	{
		stardate = logDate;
	}
	
	public static Integer reroutedPower()
	{
		return new Integer((int)(stardate(210111.21) - stardate));
	}
}
