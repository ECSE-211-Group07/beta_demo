package ca.mcgill.ecse211.beta;

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
		Resources resources = new Resources("A", "D", "B", "S4", "S1");
		final TextLCD t=LocalEV3.get().getTextLCD();
		Odometer odometer = Resources.getOdometer();
		OdometryDisplay odometryDisplay=new OdometryDisplay(odometer,t);
		UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer(odometer);
		LightLocalizer lightLocalizer = new LightLocalizer(odometer);
		Poller poller = new Poller(Resources.getUltrasonicController(), Resources.getColorController());
		
		int buttonChoice;
		do {
			t.clear();
			t.drawString("  PRESS ENTER  ", 0, 1);
			t.drawString("  TO START           ", 0, 2);
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_ENTER);
		odometer.start();
		odometryDisplay.start();
		poller.start();
		usLocalizer.doLocalization();
		Navigation.driveDistance(10, true);
		lightLocalizer.doLocalization(1, 1);
		Navigation.travelTo(1,  2);
		Navigation.travelTo(2, 2);
		lightLocalizer.doLocalization(2, 2);
		
		while(Button.waitForAnyPress()!=Button.ID_ESCAPE);
		System.exit(0);
		
		
	}
}
