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
	private enum State {WIFI, InitialLocalization, Navigation, Localization, Zipline};
	private static State state = State.WIFI;
	
	private static void initializeOdometer(int startCorner, Odometer odometer) {
		if (startCorner == 0) {			
			odometer.setX(1 * 30.48);
			odometer.setY(1 * 30.48);
			Navigation.pointTo(0);
			odometer.setTheta(0);
			Sound.beepSequence();
		} else if (startCorner == 1) {
			odometer.setX(7 * 30.48);
			odometer.setY(1 * 30.48);
			Navigation.pointTo(0);
			odometer.setTheta(0);
			Sound.beepSequence();
		} else if (startCorner == 2) {
			odometer.setX(7 * 30.48);
			odometer.setY(7 * 30.48);
			Navigation.pointTo(0);
			odometer.setTheta(180);
			Sound.beepSequence();
		} else if (startCorner == 3) {
			odometer.setX(1 * 30.48);
			odometer.setY(7 * 30.48);
			Navigation.pointTo(0);
			odometer.setTheta(180);
			Sound.beepSequence();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		Resources resources = new Resources("A", "D", "B", "S4", "S3");
		final TextLCD t=LocalEV3.get().getTextLCD();
		Odometer odometer = Resources.getOdometer();
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
		Poller poller = new Poller(Resources.getUltrasonicController(), Resources.getColorController());
		ColorController color = new ColorController();

		//WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);

		int buttonChoice, zipX = 0, zipY = 0, x0 = 2, y0 = 2, startCorner = 1
						, redTeamNo = 0, greenTeamNo = 0;
		
		odometer.start();
		odometryDisplay.start();
		poller.start();
		
//		do {
//			t.clear();
//			t.drawString("  PRESS ENTER  ", 0, 1);
//			t.drawString("  TO START           ", 0, 2);
//			buttonChoice = Button.waitForAnyPress();
//		} while (buttonChoice != Button.ID_ENTER);
//	
//		try {
//			Map data = conn.getData();
//			redTeamNo = ((Long) data.get("RedTeam")).intValue();
//			greenTeamNo = ((Long) data.get("GreenTeam")).intValue();
//			
//			if (redTeamNo == 7) {
//				x0 = ((Long) data.get("ZO_R_x")).intValue();
//				y0 = ((Long) data.get("ZO_R_y")).intValue();
//				zipX = ((Long) data.get("ZC_R_x")).intValue();
//				zipY = ((Long) data.get("ZC_R_y")).intValue();
//				startCorner = ((Long) data.get("RedCorner")).intValue();
//			} else {
//				x0 = ((Long) data.get("ZO_G_x")).intValue();
//				y0 = ((Long) data.get("ZO_G_y")).intValue();
//				zipX = ((Long) data.get("ZC_G_x")).intValue();
//				zipY = ((Long) data.get("ZC_G_y")).intValue();
//				startCorner = ((Long) data.get("GreenCorner")).intValue();
//			}
//			
//		
//		} catch (Exception e) {
//			//System.err.println("Error: " + e.getMessage());
//		}
		t.clear();
		
		while (true) {
			switch (state) {
				case WIFI:
					state = State.InitialLocalization;
				case InitialLocalization:
					Navigation.setSpeed(200, 200);
					Navigation.driveDistance(10, false);
					odometer.setTheta(0);
					Navigation.driveDistance(13, true);
					Navigation.turnTo(-90, false);
					Navigation.driveDistance(10, false);
					Navigation.driveDistance(13, true);
					initializeOdometer(startCorner, odometer);
					Navigation.travelTo(2, 1);
					System.out.println("theta before localization: " + odometer.getTheta());
					LightLocalizer.doLocalization(2, 1);
					System.out.println("theta after localization: " + odometer.getTheta());
					Navigation.travelTo(2, 2);
					Navigation.driveZipline();
	
					
//					Navigation.setSpeed(100, 100);
//					Navigation.driveDistance(10, false);
//					Navigation.driveDistance(13, true);
//					odometer.setTheta(0);
//					Navigation.turnTo(90, false);
//					Navigation.driveDistance(10, false);
//					Navigation.driveDistance(13, true);
//					odometer.setTheta(90);
//					odometer.setX(30.48);
//					odometer.setY(30.48);
//					Navigation.travelTo(3, 1);
//					odometer.setTheta(90);
//					LightLocalizer.doLocalization(3, 1);
//					Navigation.travelTo(3, 3);
					
					
					
//					UltrasonicLocalizer.doLocalization(startCorner);
//					odometer.setTheta(0);
//					LightLocalizer.doLocalization(startCorner);
//					initializeOdometer(startCorner, odometer);
//					System.out.println("x before travelTo: " + odometer.getX());
//					System.out.println("y before travelTo: " + odometer.getY());
//					System.out.println("theta before travelTo: " + odometer.getTheta());
//					Navigation.travelTo(3, 1);
//					LightLocalizer.doLocalization(3, 1);
//					Navigation.travelTo(3, 2);
//					state = State.Navigation;
					
				case Navigation:
					break;
			}
			
			
			while(Button.waitForAnyPress()!=Button.ID_ESCAPE);
			System.exit(0);
		}
		
		

	}
}
