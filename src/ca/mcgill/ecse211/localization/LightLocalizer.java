package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.ColorController;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {

	private static Odometer odometer = Resources.getOdometer();
	private static double SENSOR_DISTANCE = 14;//18
	private static double[] lightData = new double[10];
	
	private static EV3LargeRegulatedMotor leftMotor = Resources.getLeftMotor();
	private static EV3LargeRegulatedMotor rightMotor = Resources.getRightMotor();

	/** Localizes about a point (x, y) by calling subsequent helper functions
	 * @param x x coordinate relative to x = 0 to localize about
	 * @param y y coordinate relative to y = 0 to localize about
	 */
	
	/*
	 * TODO We must add a condition where it localizes to the closest point it is at, ie if it has travelled
	 * 30.48, 30.48 it will determine that it needs to localize on 1, 1
	 * Takes away work from us
	 */
	public static void doLocalization(double x, double y) {

		// goToApproxOrigin();

		// Will rotate the robot and collect lines
		rotateLightSensor();
		
		// correct position of our robot using light sensor data
		correctPosition(x, y);
		
		
		// travel to 0,0 then turn to the 0 angle
		Sound.beep();
		Navigation.travelTo(x, y);
		
		Navigation.pointTo(0);
		// Navigation.setSpeed(0,0);
	}
	
	
	/* Rotates sensor around the origin and saves the theta 
	 * which the point was encoutered at
	 */
	public static void rotateLightSensor() {
		Navigation.turnTo(360, true);
		int lineIndex=1;
		while(Navigation.isNavigating()) {
			if(ColorController.readColorData() > 0.20 && lineIndex < 5) {
				lightData[lineIndex]=odometer.getThetaDegrees();
				lineIndex++;
				Sound.beep();

			}
		}
	}
	
	
	
	/** Uses mathematical calculations to compute the correct robot position
	 * @param x x value relative to x = 0 for which robot should correct its position towards
	 * @param y y value relative to y = 0 for which robot should correct its position towards
	 */
	private static void correctPosition(double x, double y) {
		//compute difference in angles
		double deltaThetaY= Math.abs(lightData[1]-lightData[3]);
		double deltaThetaX= Math.abs(lightData[2]-lightData[4]);
		
		//use trig to determine position of the robot 
		double Xnew = (x * 30.48)-SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaY) / 2);
		double Ynew = (y * 30.48)-SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaX) / 2);
		
		odometer.setPosition(new double [] {Xnew, Ynew, 0}, 
					new boolean [] {true, true, false});

	}

}
