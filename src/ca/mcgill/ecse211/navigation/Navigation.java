package ca.mcgill.ecse211.navigation;


import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.ColorController;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;

/** Navigation uses simple trigonometry along with information obtained from the odometer to be able to 
 * allow the robot to travel to certain x and y coordinates, or turn a specified number of degrees
 * @author Marine Huynh, Sihui Shen
 *
 */
public class Navigation {
	
	private static Odometer odometer = Resources.getOdometer();
	private static EV3LargeRegulatedMotor leftMotor = Resources.getLeftMotor();
	private static EV3LargeRegulatedMotor rightMotor = Resources.getRightMotor();
	private static EV3LargeRegulatedMotor zipMotor = Resources.getZipMotor();
	private static final double RADIUS = Resources.getRadius();
	private static final double TRACK = Resources.getTrack();
	private static final double SENSOR_DISTANCE = 13.0;

	public static final int FORWARD_SPEED = 200, ROTATE_SPEED = 125, MOTOR_ACCELERATION = 50;

	

	/**
	 * Primary navigation point of the program, performs all navigation based functions
	 * @param odometer2 
	 * @param rightmotor 
	 * @param leftmotor 
	 */
	

	/**
	 * Adjusts the rotation of the robot and it's heading to travel to designated coordinate point
	 * 
	 * @param x x coordinate in coordinate grid robot needs to travel to
	 * @param y y coordinate in coordinate grid robot needs to travel to
	 */
	
	/*
	 * TODO Define travelTo to work using only horizontal and lateral directions
	 * Make sure that the point is within a certain error threshold, look at Odometer
	 * to see where it is at, and if need be call travelTo again until it is no longer within that
	 * threshold
	 */
	public static void travelTo(double x, double y) {
		x= x*30.48;
		y= y*30.48;
		
		double deltaX = x - odometer.getX();
		double deltaY = y - odometer.getY();
		
		
		// calculate the minimum angle
		double minAngle = Math.toDegrees(Math.atan2(deltaX, deltaY)) - odometer.getThetaDegrees() % 360;
		
		// Adjust the angle to make sure it takes the min angle
		if (minAngle < -180) {
			minAngle = 360 + minAngle;
		} else if (minAngle > 180) {
			minAngle = minAngle - 360;
		}
		
		// turn to the minimum angle
		turnTo(minAngle, false);
		
		// calculate the distance to next point
		double distance  = Math.hypot(deltaX, deltaY);
		
		// move to the next point
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(RADIUS,distance), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), false);
		

		leftMotor.stop(true);
		rightMotor.stop(true);
	}	

	
	public static void travelToCorrection(double x, double y) {
		double prevX = odometer.getX();
		travelToCorrect(prevX/30.48, y);
		LightLocalizer.doLocalization(nearestPoint(prevX), y);
	}
	
	public static void positionCorrection(double x, double y) {
		double currentHeading=odometer.getTheta();
		double newX;
		double newY;
		if (currentHeading < 100 && currentHeading > 80) {
			newX=nearestPoint(odometer.getX())*30.48+12;
			newY=odometer.getY();
		} else if (odometer.getTheta() < 10 || (odometer.getTheta() > 350 && odometer.getTheta() < 360)) {
		 newY=nearestPoint(odometer.getY())*30.48+12;
		 newX=odometer.getX();
		} else if (currentHeading < 190 && currentHeading >  170) {
			newY=nearestPoint(odometer.getY())*30.48-12;
			newX=odometer.getX();

		} else if (currentHeading < 280 && currentHeading > 260) {
			newX=nearestPoint(odometer.getX())*30.48-12;
			newY=odometer.getY();
		}
		
	}
	
	public static double nearestPoint(double value) {
		return Math.round((value)/30.48);
	}
	
	public static void travelToCorrect(double x, double y) {
		double error = error(x, y);
		if (error < 2) {
			return;
		}
		travelToFree(x, y);
		
		while (isNavigating()) {
			if (ColorController.leftLineDetected() && ColorController.rightLineDetected()) {
				Sound.beep();
			}
			else if (ColorController.leftLineDetected() && !ColorController.rightLineDetected()) {
				adjustRightMotor();
				break;
			} 
			else if (ColorController.rightLineDetected() && !ColorController.leftLineDetected()) {
				adjustLeftMotor();
				break;
			}
		}
		
//		System.out.println("X before: " + odometer.getX());
//		System.out.println("Y before: " + odometer.getY());
//		odometer.setTheta(getHeading());
//		System.out.println("X after: " + odometer.getX());
//		System.out.println("Y after: " + odometer.getY());
		travelToCorrect(odometer.getX()/30.48, y);
		
	}
	
	public static void adjustRightMotor() {
		double startTime = System.currentTimeMillis();
		double deltaTime;
		startSynchronization();
		leftMotor.stop();
		rightMotor.stop();
		endSynchronization();
		rightMotor.forward();
		while (!ColorController.rightLineDetected()) {
			deltaTime = System.currentTimeMillis() - startTime;
			if (deltaTime > 2000) {
				rightMotor.backward();
			}
			rightMotor.setSpeed(100);
		}
		leftMotor.stop();
		rightMotor.stop();
	}
	
	public static void adjustLeftMotor() {
		double startTime = System.currentTimeMillis();
		double deltaTime;
		startSynchronization();
		leftMotor.stop();
		rightMotor.stop();
		endSynchronization();
		leftMotor.forward();
		while (!ColorController.leftLineDetected()) {
			deltaTime = System.currentTimeMillis() - startTime;
			if (deltaTime > 2000) {
				leftMotor.backward();
			}
			leftMotor.setSpeed(100);
		}
		rightMotor.stop();
		leftMotor.stop();
	}
	
	public static double error(double x, double y) {
		return Math.sqrt(Math.pow(odometer.getX() - x*30.48, 2) + Math.pow(odometer.getY() - y*30.48, 2));
	}
	
	public static void correctPosition() {
		double currentHeading = getHeading();
		boolean travellingX = false;
		int multiplier = 0;
		if (currentHeading == 0.0) {
			travellingX = false;
			multiplier = 1;
		} else if (currentHeading == 180.0) {
			travellingX = false;
			multiplier = -1;
		} else if (currentHeading == 90.0) {
			travellingX = true;
			multiplier = 1;
		} else if (currentHeading == 270.0) {
			travellingX = true;
			multiplier = -1;
		} else {
			return;
		}
		
		double currentPosition;
		if (travellingX) {
			currentPosition = odometer.getX();
		} else {
			currentPosition = odometer.getY();
		}
		currentPosition = currentPosition/30.48;
		
		double error = Double.MAX_VALUE;
		double bestIndex = 0;
		for(double i = 1; i < 13; i++) {
			if (Math.abs(currentPosition - i) < error) {
				bestIndex = i;
				error = Math.abs(currentPosition - i);
			}
		}
		
		if (travellingX) {
			System.out.println("Corrected X to be: " + 30.48*bestIndex + multiplier*SENSOR_DISTANCE);
		} else {
			System.out.println("Corrected Y to be: " + 30.48*bestIndex + multiplier*SENSOR_DISTANCE);
		}
		
		
		odometer.setPosition(new double[] {bestIndex*30.48 + SENSOR_DISTANCE*multiplier, bestIndex*30.48 + SENSOR_DISTANCE*multiplier, currentHeading}, 
							new boolean[] {travellingX, !travellingX, true}); 
	}
	
	public static double getHeading() {
		double currentHeading = odometer.getThetaDegrees();
		
		if (currentHeading < 100 && currentHeading > 80) {
			return 90.0;
		} else if (odometer.getTheta() < 10 || (odometer.getTheta() > 350 && odometer.getTheta() < 360)) {
			return 0.0;
		} else if (currentHeading < 190 && currentHeading >  170) {
			return 180.0;
		} else if (currentHeading < 280 && currentHeading > 260) {
			return 270.0;
		} else {
			return currentHeading;
		}
	}
	
	
	
	
	public static void travelToFree(double x, double y) {
		x= x*30.48;
		y= y*30.48;
		
		
		double deltaX = x - odometer.getX();
		double deltaY = y - odometer.getY();
//		System.out.println("X: " + x);
//		System.out.println("Y: " + y);
//		System.out.println("Odometer X: " + odometer.getX());
//		System.out.println("Odometer Y: " + odometer.getY());
//		System.out.println("DeltaX: " + deltaX);
//		System.out.println("DeltaY: " + deltaY);
		
		// Calculate the degree you need to change to
		double minAngle = Math.toDegrees(Math.atan2(deltaX, deltaY)) - odometer.getThetaDegrees();
//		System.out.println("Min angle: " + minAngle);
	
		if (minAngle < -180) {
			minAngle = 360 + minAngle;
		} else if (minAngle > 180) {
			minAngle = minAngle - 360;
		}
		
		// turn to the minimum angle
		turnTo(minAngle, false);
		
		// calculate the distance to next point
		double distance  = Math.hypot(deltaX, deltaY);
		
		// move to the next point
		leftMotor.setSpeed(FORWARD_SPEED+5);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(RADIUS,distance), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), true);
		double startTime = System.currentTimeMillis(); 
		while (!(System.currentTimeMillis() - startTime > 2000));
	}
	
	/**
	 * Designed to rotate the robot a preset number of degrees
	 * 
	 * @param theta The angle at which the robot needs to turn too
	 * @param block Whether or not the execution of the turn should block threads
	 */
	public static void turnTo(double theta, boolean block) {
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		int angle = convertAngle(RADIUS, TRACK, theta);
		
		leftMotor.rotate(angle, true);
		rightMotor.rotate(-angle, block);

	}
	
	
	/**
	 * Designed to rotate robot to face a direction defined by theta
	 * 
	 * @param theta The final orientation desired
	 */
	public static void pointTo(double theta) {
		double minAngle = theta - odometer.getThetaDegrees()%360;
		if (minAngle < -180) {
			minAngle = 360 + minAngle;
		} else if (minAngle > 180) {
			minAngle = minAngle - 360;
		}
		
		turnTo(minAngle, false);
	}
	
	
	/**
	 * Used to stop both left and right motors in sync with each other
	 */
	public static void stopMotors() {
		leftMotor.stop();
		rightMotor.stop();
	}
	
	/**
	 * Allows the robot to drive a fixed number of cm either in reverse or forwards
	 * 
	 * @param distance the distance in cm that the robot will drive
	 * @param forward whether the robot will be going backwards or forwards
	 */
	public static void driveDistance(int distance, boolean forward) {
		if (forward) {
			leftMotor.rotate(convertDistance(RADIUS, distance), true);
			rightMotor.rotate(convertDistance(RADIUS, distance), false);
		} else {
			leftMotor.rotate(-convertDistance(RADIUS, distance), true);
			rightMotor.rotate(-convertDistance(RADIUS, distance), false);
		}

	}
	
	/**
	 * Used to synchronize leftMotor and rightMotor together
	 */
	public static void synchronizeMotors() {
		RegulatedMotor[] motors = new RegulatedMotor[] {leftMotor};
		rightMotor.synchronizeWith(motors);
	}
	
	/**
	 * Must be called before a set of synchronized subroutines
	 */
	public static void startSynchronization() {
		rightMotor.startSynchronization();
	}
	
	/**
	 * Called to end synchronization of motors and to signal an end of a synchronized subroutine
	 */
	public static void endSynchronization() {
		rightMotor.endSynchronization();
	}

	
	/**
	 * Used to set the motor speed and get the motor to travel in a constant direction,
	 * if values are negative the robot will travel backwards
	 * 
	 * @param leftM speed of the left motor
	 * @param rightM speed of the right motor
	 */
	public static void setSpeed(int leftM, int rightM) {
		leftMotor.setSpeed(leftM);
		rightMotor.setSpeed(rightM);
		
		if (leftM > 0) {
			leftMotor.forward();
		} else {
			leftMotor.backward();
		}
		
		if (rightM > 0) {
			rightMotor.forward();
		} else {
			rightMotor.backward();
		}
	}
	
	
	/**
	 * Used to set the motor acceleration
	 * 
	 * @param leftM acceleration of the left motor
	 * @param rightM acceleration of the right motor
	 */
	public static void setAcceleration(int acceleration) {
		leftMotor.setAcceleration(acceleration);
		rightMotor.setAcceleration(acceleration);
	}
	
	/**
	 * Drives the zipline motor a predefined distance
	 * 
	 * @param distance The distance which the zipline motor needs to drive
	 */
	public static void driveZiplineDistance(double distance) {
		zipMotor.rotate(convertDistance(RADIUS, distance), false);
	}
	
	
	
	/**
	 * Drives left and right motor a certain distance in cm as defined by distance
	 * 
	 * @param distance The distance in cm the left and right motor should move by
	 */
	public static void driveLength(double distance) {
		leftMotor.rotate((int) (360*convertDistance(RADIUS, distance)), true);
		rightMotor.rotate((int) (360*convertDistance(RADIUS, distance)), false);
	}
	
	/**
	 * A set of instructions that all the robot to traverse the zipline from the point Xo, Yo
	 */
	public static void driveZipline() {
		leftMotor.setAcceleration(60);
		rightMotor.setAcceleration(60);
		leftMotor.setSpeed(300);
		rightMotor.setSpeed(300);
		zipMotor.setSpeed(350);
		zipMotor.backward();

		driveLength(30.48 * 9);	
	}
	
	
	/**
	 * @param radius Wheel radius of the robot
	 * @param distance Angular distance needed to travel
	 * @return
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180*distance) / (Math.PI * radius));
	}

	/**
	 * @param radius Wheel radius of the robot
	 * @param width Track width of the robot
	 * @param angle Desired rotation angle
	 * @return
	 */
	private static int convertAngle(double radius, double width, double angle) {
		int output = convertDistance(radius, Math.PI * width * angle / 360.0);
		return output;
	}
	
	/**
	 * Indicates whether the robot is in the process of navigation
	 * 
	 * @return If the robot's left or right motor is moving it returns true
	 */
	public static boolean isNavigating() {
		return (leftMotor.isMoving() || rightMotor.isMoving());
	}
}

