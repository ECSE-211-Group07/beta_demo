package ca.mcgill.ecse211.beta;

<<<<<<< HEAD
import java.util.Map;

import ca.mcgill.ecse211.WiFiClient.WifiConnection;
=======
import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.UltrasonicLocalizer;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometryDisplay;
import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.Poller;
>>>>>>> 79c4aef00776170a8f78d7ec44dc2ec6f17cdd82
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Tester {

	private static final String SERVER_IP = "192.168.43.193";
	private static final int TEAM_NUMBER = 7;
	private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);
		Resources resources = new Resources("A", "D", "B", "S4", "S1");
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = Resources.getOdometer();
<<<<<<< HEAD
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
		UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer(odometer);
		LightLocalizer lightLocalizer = new LightLocalizer(odometer);
=======
		OdometryDisplay odometryDisplay=new OdometryDisplay(odometer,t);
>>>>>>> 79c4aef00776170a8f78d7ec44dc2ec6f17cdd82
		Poller poller = new Poller(Resources.getUltrasonicController(), Resources.getColorController());

		int buttonChoice, zipX = 0, zipY = 0;
		do {
			t.clear();
			t.drawString("  PRESS ENTER  ", 0, 1);
			t.drawString("  TO START           ", 0, 2);
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_ENTER);
		try {
			Map data = conn.getData();

			zipX = ((Long) data.get("ZC_R_x")).intValue();
			zipY = ((Long) data.get("ZC_R_y")).intValue();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println(" " + zipX + " " + zipY);
		odometer.start();
		odometryDisplay.start();
		poller.start();
<<<<<<< HEAD
		usLocalizer.doLocalization();
		// Navigation.turnTo(360, false);
		Navigation.driveDistance(10, true);
		lightLocalizer.doLocalization(1, 1);
		Sound.beep();
		Navigation.travelTo(zipX, zipY);
		

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
=======
		
		/*
		 * If you are looking to stop the reverse, please look into doLocalization and 
		 * remove Navigation.driveDistance call
		 */
		UltrasonicLocalizer.doLocalization();
		Navigation.driveDistance(10, true);
		LightLocalizer.doLocalization(1, 1);
		Navigation.travelTo(1,  6);
		Navigation.turnTo(-360, false);
		/*
		 * If you want to test Navigation more rigorously 
		 * maybe add a LightLocalization call in between each instance
		 */
		//Navigation.travelTo(2, 2);
		LightLocalizer.doLocalization(1, 6);
		Navigation.pointTo(90);
		Navigation.driveZipline();
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE);
>>>>>>> 79c4aef00776170a8f78d7ec44dc2ec6f17cdd82
		System.exit(0);

	}
}
