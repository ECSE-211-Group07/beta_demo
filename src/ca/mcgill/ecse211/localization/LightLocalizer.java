package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.ColorController;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/** Class that enables us to light localize to x = 0, y = 0 using color sensor
 * @author Adam Gobran, Ali Shobeiri, Abe Yesgat, Reda El Khili
 *
 */
public class LightLocalizer {

	private static Odometer odometer = Resources.getOdometer();
	private static double SENSOR_DISTANCE = 15.5;//18
	private static double[] lightData = new double[5];
	private static double[] lightDataRight = new double[5];
	private static double[] lightDataLeft = new double[5];
	
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
	public static void doLocalization(double x, double y, int corner) {

		// goToApproxOrigin();
		if (corner == 0 || corner == 2) {
			Navigation.pointTo(odometer.getThetaDegrees() + 45);
		} else {
			Navigation.pointTo(odometer.getThetaDegrees() - 45);
		}
		
		// Will rotate the robot and collect lines
		rotateLightSensor();
		Sound.beep();
		// correct position of our robot using light sensor data
		correctPosition(x, y, corner);
		
		// travel to 0,0 then turn to the 0 angle
		Navigation.travelTo(x, y);
		
		// Navigation.setSpeed(0,0);
		
	}
	
	
	/* Rotates sensor around the origin and saves the theta 
	 * which the point was encoutered at
	 */
	public static void rotateLightSensor() {
		Navigation.turnTo(360, true);
		int lineIndex = 1;
		while(Navigation.isNavigating()) {
			if(ColorController.middleLineDetected() && lineIndex < 5) {
				lightData[lineIndex]=odometer.getThetaDegrees();
				lineIndex++;
				Sound.beep();
			}
			
//			if(ColorController.rightLineDetected() && lineIndexRight < 5) {
//				lightDataRight[lineIndexRight]=odometer.getThetaDegrees();
//				lineIndexRight++;
//				Sound.beepSequence();
//			}
		}
		
		//averageValues();
	}
	
	private static void averageValues() {
		for (int i=1; i < 5; i++) {
			lightData[i] = (lightDataLeft[i] + lightDataRight[i])/2;
		}
	}
	
	
	
	/** Uses mathematical calculations to compute the correct robot position
	 * @param x x value relative to x = 0 for which robot should correct its position towards
	 * @param y y value relative to y = 0 for which robot should correct its position towards
	 */
	private static void correctPosition(double x, double y, int corner) {
		//compute difference in angles
		double deltaThetaY, deltaThetaX, Xnew = 0, Ynew = 0;
		if (corner == 0 || corner == 2) {
			deltaThetaY= lightData[2]-lightData[4];
			deltaThetaX= lightData[1]-lightData[3];
			if (corner == 2) {
				Xnew = (x * 30.48)+SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaY) / 2);
				Ynew = (y * 30.48)-SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaX) / 2);
			} else {
				Xnew = (x * 30.48)-SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaY) / 2);
				Ynew = (y * 30.48)-SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaX) / 2);
			}
		} else if (corner == 1 || corner == 3) {
			deltaThetaY= lightData[1]-lightData[3];
			deltaThetaX= lightData[2]-lightData[4];
			if (corner == 1) {
				Xnew = (x * 30.48)+SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaY) / 2);
				Ynew = (y * 30.48)-SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaX) / 2);
			} else {
				Xnew = (x * 30.48)-SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaY) / 2);
				Ynew = (y * 30.48)-SENSOR_DISTANCE*Math.cos(Math.toRadians(deltaThetaX) / 2);
			}
		} else {
			if (Navigation.getHeading() == 0 || Navigation.getHeading() == 180) {
				deltaThetaY= lightData[2]-lightData[4];
				deltaThetaX= lightData[1]-lightData[3];
			} else {
				deltaThetaY= lightData[1]-lightData[3];
				deltaThetaX= lightData[2]-lightData[4];
			}
		}
		
		
		odometer.setPosition(new double [] {Xnew, Ynew, 0}, 
					new boolean [] {true, true, false});

	}

}
