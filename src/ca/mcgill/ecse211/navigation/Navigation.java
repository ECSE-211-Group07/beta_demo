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

	public static final int FORWARD_SPEED = 200, ROTATE_SPEED = 75, MOTOR_ACCELERATION = 50;

	private static boolean travellingX = true;

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
		x = x * 30.48;
		y = y * 30.48;
		
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
		rightMotor.setSpeed((float) (FORWARD_SPEED * (1 + Resources.getSpeedMult())));
		
		
		leftMotor.rotate(convertDistance(RADIUS, distance / (1 + Resources.getSpeedMult())), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), false);
	}	

	/** Travels individually to desired x and y coordinates, correcting theta as needed
	 * @param x
	 * @param y
	 */
	public static void travelToCorrection(double x, double y) {
		double currentY = nearestPoint(odometer.getY());
		travelTo(x, currentY);
		LightLocalizer.doLocalization(x, currentY);
		double currentX = nearestPoint(odometer.getX());
		travelTo(currentX, y);
		LightLocalizer.doLocalization(currentX, y);
	}
	
	/** Set to true if traveling in x 
	 * @param x
	 */
	public static void setTravelling(boolean x) {
		travellingX = x;
	}
 	
	public static double nearestPoint(double value) {
		return Math.round((value)/30.48);
	}
	
	/** Calculate how far we are from where we want to be
	 * @param x
	 * @param y
	 * @return error from desired point
	 */
	public static double error(double x, double y) {
		return Math.sqrt(Math.pow(odometer.getX() - x*30.48, 2) + Math.pow(odometer.getY() - y*30.48, 2));
	}
	
	public static double getHeading() {
		double currentHeading = odometer.getThetaDegrees();
		
		if (currentHeading < 100 && currentHeading > 80) {
			travellingX = true;
			return 90.0;
		} else if (currentHeading < 10 || (currentHeading > 350 && currentHeading < 360)) {
			travellingX = false;
			return 0.0;
		} else if (currentHeading < 190 && currentHeading >  170) {
			travellingX = false;
			return 180.0;
		} else if (currentHeading < 280 && currentHeading > 260) {
			travellingX = true;
			return 270.0;
		} else {
			return currentHeading;
		}
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
	public static void setSpeed(float leftM, float rightM) {
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
		
		/*
		 * while (colorSensor does not detect colors) {
		 * 		zipMotor.forward();
		 *
		 * }
		 * 
		 * drive until black line detected
		 * calculate what point robot is at
		 * localize about that point
		 * set odometer
		 * 
		 * 
		 */
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

