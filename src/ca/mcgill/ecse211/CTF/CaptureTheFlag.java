package ca.mcgill.ecse211.CTF;

import java.util.Map;

import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.UltrasonicLocalizer;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometryDisplay;
import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.ColorController;
import ca.mcgill.ecse211.sensor.Poller;
import ca.mcgill.ecse211.sensor.UltrasonicController;
import ca.mcgill.ecse211.wifi.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

/** Main CaptureTheFlag class that executes all other classes and threads. Implements state machine and handles all receiving of wifi parameters.
 * @author Adam Gobran
 *
 */
public class CaptureTheFlag {

	// wifi config variables
	private static final String SERVER_IP = "192.168.43.193";
	private static final int TEAM_NUMBER = 7;
	private static final boolean ENABLE_DEBUG_WIFI_PRINT = false;

	// state machine, represented by a Java enumeration
	private enum State {
		WIFI, InitialLocalization, NavigateZip, NavigateRiver, TraverseZip, TraverseRiver, BlockSearch, ReturnZip, ReturnRiver
	};

	// initialize state to wifi
	private static State state = State.WIFI;

	//global variables
	//boolean to indicate whether we will traverse horizontal or vertical component of river first
	private static boolean riverHorizontal = false;
	private static double blockLength = 30.48;

	// global variables obtained from wifi
	private static int buttonChoice, startCorner = 0, ZO_R_x = 0, ZO_R_y = 0, ZC_R_x = 0, ZC_R_y = 0, ZO_G_x = 0,
			ZO_G_y = 0, ZC_G_x = 0, ZC_G_y = 0, SH_LL_x = 0, SH_LL_y = 0, SH_UR_x = 0, SH_UR_y = 0, SV_LL_x = 0,
			SV_LL_y = 0, SV_UR_x = 0, SV_UR_y = 0, Red_LL_x = 0, Red_LL_y = 0, Red_UR_x = 0, Red_UR_y = 0,
			SR_LL_x = 0, SR_LL_y = 0, SR_UR_x, SR_UR_y, SG_LL_x = 0, SG_LL_y = 0, SG_UR_x = 0, SG_UR_y = 0,
			OG = 0, OR = 0,  
			Green_LL_x = 0, Green_LL_y = 0, Green_UR_x = 0, Green_UR_y = 0, redTeamNo = 0, greenTeamNo = 0;

	/**
	 * Method that computes new state based on current state and global variables
	 * 
	 * @param currentState
	 *            Current state
	 */
	public static void changeState(State currentState) {
		switch (currentState) {
		case WIFI:
			state = State.InitialLocalization;
			break;
		case InitialLocalization:
			// change state according to team color in order to navigate to appropriate mode
			// of transportation
			if (greenTeamNo == TEAM_NUMBER) {
				state = State.NavigateZip;
			} else {
				state = State.NavigateRiver;
			}
			break;
		case NavigateZip:
			state = State.TraverseZip;
			break;

		case NavigateRiver:
			state = State.NavigateZip;
			break;

		case TraverseZip:
			state = State.NavigateRiver;
			break;
		}
	}

	/**
	 * Method that initializes robots odometer based on its starting corner. Assumes
	 * robot has localized properly.
	 * 
	 * @param startCorner
	 *            Starting corner of robot
	 * @param odometer
	 *            Odometer used to keep track of robot's position
	 */
	public static void initializeOdometer(int startCorner, Odometer odometer) {
		if (startCorner == 0) {
			odometer.setX(1 * blockLength);
			odometer.setY(1 * blockLength);
			Navigation.pointTo(0);
			odometer.setTheta(0);
			Sound.beepSequence();
		} else if (startCorner == 1) {
			odometer.setX(11 * blockLength);
			odometer.setY(1 * blockLength);
			Navigation.pointTo(0);
			odometer.setTheta(0);
			Sound.beepSequence();
		} else if (startCorner == 2) {
			odometer.setX(11 * blockLength);
			odometer.setY(11 * blockLength);
			Navigation.pointTo(0);
			odometer.setTheta(180);
			Sound.beepSequence();
		} else if (startCorner == 3) {
			odometer.setX(1 * blockLength);
			odometer.setY(11 * blockLength);
			Navigation.pointTo(0);
			odometer.setTheta(180);
			Sound.beepSequence();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		Resources resources = new Resources("A", "D", "B", "S4", "S2", "S3");
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = Resources.getOdometer();
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
		Poller poller = new Poller(Resources.getUltrasonicController(), Resources.getColorController());
		ColorController color = new ColorController();

		WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);

		// start threads
		odometer.start();
		odometryDisplay.start();
		poller.start();

		try {
			//initialize variables from wifi
			Map data = conn.getData();
			redTeamNo = ((Long) data.get("RedTeam")).intValue();
			greenTeamNo = ((Long) data.get("GreenTeam")).intValue();
			startCorner = ((Long) data.get("RedCorner")).intValue();

			Red_LL_x = ((Long) data.get("Red_LL_x")).intValue();
			Red_LL_y = ((Long) data.get("Red_LL_y")).intValue();
			Red_UR_x = ((Long) data.get("Red_UR_x")).intValue();
			Red_UR_y = ((Long) data.get("Red_UR_y")).intValue();

			Green_LL_x = ((Long) data.get("Green_LL_x")).intValue();
			Green_LL_y = ((Long) data.get("Green_LL_y")).intValue();
			Green_UR_x = ((Long) data.get("Green_UR_x")).intValue();
			Green_UR_y = ((Long) data.get("Green_UR_y")).intValue();

			ZO_R_x = ((Long) data.get("ZO_R_x")).intValue();
			ZO_R_y = ((Long) data.get("ZO_R_y")).intValue();
			ZC_R_x = ((Long) data.get("ZC_R_x")).intValue();
			ZC_R_y = ((Long) data.get("ZC_R_y")).intValue();
			ZO_G_x = ((Long) data.get("ZO_G_x")).intValue();
			ZO_G_y = ((Long) data.get("ZO_G_y")).intValue();
			ZC_G_x = ((Long) data.get("ZC_G_x")).intValue();
			ZC_G_y = ((Long) data.get("ZC_G_y")).intValue();
			
			OG = ((Long) data.get("OG")).intValue();
			OR = ((Long) data.get("OR")).intValue();

			SH_LL_x = ((Long) data.get("SH_LL_x")).intValue();
			SH_LL_y = ((Long) data.get("SH_LL_y")).intValue();
			SH_UR_x = ((Long) data.get("SH_UR_x")).intValue();
			SH_UR_y = ((Long) data.get("SH_UR_y")).intValue();
			SV_LL_x = ((Long) data.get("SV_LL_x")).intValue();
			SV_LL_y = ((Long) data.get("SV_LL_y")).intValue();
			SV_UR_x = ((Long) data.get("SV_UR_x")).intValue();
			SV_UR_y = ((Long) data.get("SV_UR_y")).intValue();

			if (greenTeamNo == TEAM_NUMBER) {
				startCorner = ((Long) data.get("GreenCorner")).intValue();
			} else {
				startCorner = ((Long) data.get("RedCorner")).intValue();
			}

		} catch (Exception e) {
			// System.err.println("Error: " + e.getMessage());
		}
		t.clear();

		// main loop of application that constantly checks state
		while (true) {
			switch (state) {
			case WIFI:
				changeState(state);

			case InitialLocalization:
				// perform localization. Note we do not use light localization for initial
				// localization in the corner
				UltrasonicLocalizer.doLocalization(startCorner);
				initializeOdometer(startCorner, odometer);
				changeState(state);

			case NavigateZip:
				// Navigate to correct coordinates depending
				if (greenTeamNo == TEAM_NUMBER) {
					Navigation.travelToCorrection(ZO_G_x, ZO_G_y);
					LightLocalizer.doLocalization(ZO_G_x, ZO_G_y);
					Navigation.travelToCorrection(ZC_G_x, ZC_G_y);
					changeState(state);
				} else {
					Navigation.travelToCorrection(ZO_R_x, ZO_R_y);
					LightLocalizer.doLocalization(ZO_R_x, ZO_R_y);
					Navigation.travelToCorrection(ZC_R_x, ZC_R_y);
				}
				changeState(state);

			case NavigateRiver:
				/*
				 * travel to x coordinate and y coordinate of shallow river Note that in both
				 * cases we have to find the point of the shallow river that borders with the
				 * red region
				 */
				/*
				 * test if horizontal component of river borders with red region and if so
				 * travel to horizontal component and traverse river otherwise travel to
				 * vertical component of river
				 */
				if (Red_UR_x == SH_LL_x || Red_LL_y == SH_LL_y) {
					//riverHorizontal variable will later be used in TraverseRiver variable
					riverHorizontal = true;
					Navigation.travelToCorrection(SH_LL_x, SH_LL_y);
					LightLocalizer.doLocalization(SH_LL_x, SH_LL_y);
				} else {
					riverHorizontal = false;
					Navigation.travelToCorrection(SV_LL_x, SV_LL_y);
					LightLocalizer.doLocalization(SV_LL_x, SV_LL_y);
				}
				changeState(state);

			case TraverseZip:
				Navigation.driveZipline();
				// After traveling zipline, localize and correct odometer depnding on team color 
				if (greenTeamNo == TEAM_NUMBER) {
					LightLocalizer.doLocalization(ZC_R_x, ZC_R_y);
				} else {
					LightLocalizer.doLocalization(ZC_G_x, ZC_G_y);
				}
				changeState(state);

			case TraverseRiver:
				//traverse river based on riverHorizontal variable previously set in NavigateRiver state
				if (riverHorizontal) {
					Navigation.travelToCorrection(SH_UR_x, SH_UR_y);
					Navigation.travelToCorrection(SV_LL_x, SV_LL_y);
				} else {
					Navigation.travelToCorrection(SV_UR_x, SV_UR_y);
					Navigation.travelToCorrection(SH_LL_x, SH_LL_y);
				}
				changeState(state);

			case BlockSearch:
				//Naivgate to different search paths depending on team number
				if (greenTeamNo == TEAM_NUMBER) {
					//starting point for search
					Navigation.travelTo(SR_LL_x, SR_LL_y);
					//go through whole x component of search region, turning to look inside region for blocks
					for (int i = 1; i < SR_UR_y; i++) {
						Navigation.travelTo(SR_LL_x, SR_LL_y + i);
						Navigation.turnTo(90, false);
						if (UltrasonicController.blockDetected()) {
							//call blockMatch depending on team color
							if (ColorController.blockMatch(OG)) {
								Sound.beep();
								Sound.beep();
								Sound.beep();
							}
						}
						Navigation.turnTo(-90, false);
					}
					
				} else {
					Navigation.travelTo(SG_LL_x, SG_LL_y);
					//go through whole x component of search region, turning to look inside region for blocks
					for (int i = 1; i < SG_UR_y; i++) {
						Navigation.travelTo(SG_LL_x, SG_LL_y + i);
						Navigation.turnTo(90, false);
						if (UltrasonicController.blockDetected()) {
							//call blockMatch depending on team color
							if (ColorController.blockMatch(OR)) {
								Sound.beep();
								Sound.beep();
								Sound.beep();
							}
						}
						Navigation.turnTo(-90, false);
					}
				}
				changeState(state);
				
			}

			while (Button.waitForAnyPress() != Button.ID_ESCAPE)
				;
			System.exit(0);
		}

	}
}
