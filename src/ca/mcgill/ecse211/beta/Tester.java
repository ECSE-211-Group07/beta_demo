package ca.mcgill.ecse211.beta;

import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.UltrasonicLocalizer;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometryDisplay;
import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.ColorController;
import ca.mcgill.ecse211.sensor.Poller;
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
	
	public static void main(String[] args) {
		Resources resources = new Resources("A", "D", "B", "S4", "S2", "S1");
		final TextLCD t=LocalEV3.get().getTextLCD();
		Odometer odometer = Resources.getOdometer();
		// OdometryDisplay odometryDisplay=new OdometryDisplay(odometer,t);
		Poller poller = new Poller(Resources.getUltrasonicController(), Resources.getColorController());
		ColorController color = new ColorController();
		int buttonChoice;
		do {
			t.clear();
			t.drawString("  PRESS ENTER  ", 0, 1);
			t.drawString("  TO START           ", 0, 2);
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_ENTER);
		odometer.start();
		// odometryDisplay.start();
		poller.start();
		
		/*
		 * If you are looking to stop the reverse, please look into doLocalization and 
		 * remove Navigation.driveDistance call
		 */
//
//		Navigation.turnTo(-360*10, true);
//		
//		UltrasonicLocalizer.doLocalization();
//		Navigation.driveDistance(10, true);
		LightLocalizer.doLocalization(1, 1);
//		
//		// Navigation.driveDistance(16, false);
//		Navigation.travelTo(1, 1);
//		Sound.beep();
//		Navigation.travelTo(1,  6);
		/*
		 * If you want to test Navigation more rigorously 
		 * maybe add a LightLocalization call in between each instance
		 */
		//Navigation.travelTo(2, 2);
//		LightLocalizer.doLocalization(1, 6);
//		Navigation.pointTo(90);
//		Navigation.driveZipline();
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE);
		System.exit(0);
		
		
	}
}
