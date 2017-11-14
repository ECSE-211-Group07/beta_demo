package ca.mcgill.ecse211.beta;


import java.util.Map;

import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.UltrasonicLocalizer;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometryDisplay;
import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.ColorController;
import ca.mcgill.ecse211.sensor.Poller;
import ca.mcgill.ecse211.wifi.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class Tester {

	private static final String SERVER_IP = "192.168.43.193";
	private static final int TEAM_NUMBER = 7;
	private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		Resources resources = new Resources("A", "D", "B", "S2", "S4", "S3", "S1");
		final TextLCD t=LocalEV3.get().getTextLCD();
		Odometer odometer = Resources.getOdometer();
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
		// OdometryDisplay odometryDisplay=new OdometryDisplay(odometer,t);
		Poller poller = new Poller(Resources.getUltrasonicController(), Resources.getColorController());
		ColorController color = new ColorController();

		//WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);

		int buttonChoice, zipX = 0, zipY = 0, startCorner = 1;
		
		odometer.start();
		odometryDisplay.start();
		poller.start();
		
		do {
			t.clear();
			t.drawString("  PRESS ENTER  ", 0, 1);
			t.drawString("  TO START           ", 0, 2);
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_ENTER);
	
//		try {
//			Map data = conn.getData();
//
//			//zipX = ((Long) data.get("ZC_R_x")).intValue();
//			//zipY = ((Long) data.get("ZC_R_y")).intValue();
//			startCorner = ((Long) data.get("RedCorner")).intValue();
//		} catch (Exception e) {
//			System.err.println("Error: " + e.getMessage());
//		}
		t.clear();
		
		//initialize robots position based on the inputed starting corner
		if (startCorner == 0) {
			//UltrasonicLocalizer.doLocalization();
			odometer.setTheta(0);
			LightLocalizer.doLocalization(1, 1, startCorner);
			Navigation.travelTo(2, 1);
		} else if (startCorner == 1) {
			//UltrasonicLocalizer.doLocalization();
			odometer.setTheta(0);
			LightLocalizer.doLocalization(7, 1, startCorner);
			Navigation.travelTo(6, 1);
		} else if (startCorner == 2) {
			//UltrasonicLocalizer.doLocalization();
			odometer.setTheta(180);
			LightLocalizer.doLocalization(7, 7, startCorner);
			Navigation.travelTo(6, 7);
			//Navigation.travelTo(7, 5);
		} else if (startCorner == 3) {
			odometer.setTheta(180);
			LightLocalizer.doLocalization(1, 7, startCorner);
			Navigation.travelTo(2, 7);
		}

		odometer.start();
		odometryDisplay.start();
		poller.start();
		
		/*
		 * If you are looking to stop the reverse, please look into doLocalization and 
		 * remove Navigation.driveDistance call
		 */
//
//		Navigation.turnTo(-360*10, true);
//		
		
//		Navigation.driveDistance(5, true);
//		UltrasonicLocalizer.doLocalization();
//		Navigation.driveDistance(10, true);
//		LightLocalizer.doLocalization(1, 1);
		
		//		UltrasonicLocalizer.doLocalization();
//		Navigation.driveDistance(10, true);
//		LightLocalizer.doLocalization(1, 1);
////		
		Navigation.travelToCorrection(0, 7);
////		// Navigation.driveDistance(16, false);
////		Navigation.travelTo(1, 1);
//		Sound.beep();
//		Navigation.travelTo(1,  6);

		//Navigation.travelTo(2, 2);
//		LightLocalizer.doLocalization(1, 6);
//		Navigation.pointTo(90);
//		Navigation.driveZipline();
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE);
		System.exit(0);

	}
}
