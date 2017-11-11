package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.UltrasonicController;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/** Class that enables robot to localize to theta = 0 using ultrasonic sensor
 * @author Marine Huynh, Sihui Shen
 *
 */
public class UltrasonicLocalizer {
	private static EV3LargeRegulatedMotor leftMotor = Resources.getLeftMotor();
	private static EV3LargeRegulatedMotor rightMotor = Resources.getRightMotor();
	private static TextLCD LCD=LocalEV3.get().getTextLCD();
	private static Odometer odometer = Resources.getOdometer();

	private static final int ROTATE_SPEED=200;
	private static final int D=55;
	private static int dT;
	private static double thetaA, thetaB;
	
	public static void doLocalization() {
		fallingEdge();
	}
	
	
	/**
	 * Method that uses the falling edge, where the distance seen by the robot is very large, as it is facing no wall,
	 * and falling as it turns and finds a wall. This is the falling point
	 * It uses the falling points to localize itself in space and time, to be at the 0 degree angle, which is facing
	 * the positive y-axis
	 * 
	 * @return nothing
	 */
	private static void fallingEdge() {
//		Navigation.turnTo(-360, false);
		//set rotation speed
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		//rotate clockwise until it sees no wall
		while(UltrasonicController.readUSDistance()<D) {
			leftMotor.forward();
			rightMotor.backward();
		}

			//keep rotating until the robot sees the wall,then get the angel
			leftMotor.forward();
			rightMotor.backward();
			boolean isTurning=true;
	
			while(isTurning) {
				if(UltrasonicController.readUSDistance()<D) {
					leftMotor.stop(true);
					rightMotor.stop(false);
					isTurning=false;
				}
			}
		Sound.beep();
		thetaA=odometer.getThetaDegrees();
		thetaA=normalizeTheta(thetaA);
		
		//switch direction until it sees no wall
		while(UltrasonicController.readUSDistance()<D) {
			leftMotor.backward();
			rightMotor.forward();
		}
		
		//keep rotation until the robot sees the wall
		leftMotor.backward();
		rightMotor.forward();
		
		isTurning=true;
		while(isTurning) {
			if(UltrasonicController.readUSDistance()<D) {
				leftMotor.stop(true);
				rightMotor.stop(false);
				isTurning=false;
			}
		}
		Sound.beep();
		
		thetaB=odometer.getThetaDegrees();
		thetaB=normalizeTheta(thetaB);
		Sound.beep();
		
		if(thetaA>thetaB) {
			dT= 45-(int) (thetaA+thetaB)/2;
		}
		else if(thetaA<thetaB) {
			dT= 225-(int) (thetaA+thetaB)/2;
		} 
		double currentTheta= odometer.getThetaDegrees();

		double newtheta=currentTheta+dT;
		odometer.setTheta(newtheta);
		if (newtheta > 180) {
			Navigation.turnTo(360 - newtheta, false);
		} else {
			Navigation.turnTo(-newtheta, false);
		}
		odometer.setPosition(new double [] {0, 0, 0}, 
				new boolean [] {true, true, true});
		
		// Navigation.driveDistance(25, false);
		
		odometer.setPosition(new double [] {0, 0, 0}, 
				new boolean [] {true, true, true});
	}
	
	
	
	/**
	 * Method that constraint the values of the thetas to be in degree between the values of [0, 360)
	 * 
	 * @param theta which is the angle in degree
	 * @return theta, which is the angle in degree between [0,360)
	 */
	private static double normalizeTheta(double theta) {
		if (theta >= 360) {
			theta = theta - 360;
		}
		else if( theta < 0) {
			theta = theta +360;
		}
		return theta;
	}

}
