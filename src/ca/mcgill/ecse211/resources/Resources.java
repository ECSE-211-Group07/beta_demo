package ca.mcgill.ecse211.resources;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometryDisplay;
import ca.mcgill.ecse211.sensor.ColorController;
import ca.mcgill.ecse211.sensor.UltrasonicController;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


/** Resources allows us to keep all the interfaces to our external hardware components in one place
 * @author Ali Shobeiri
 *
 */
public class Resources {
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static EV3LargeRegulatedMotor zipMotor;
	private static EV3UltrasonicSensor ultrasonicSensor;
	private static UltrasonicController usCont;
	private static EV3ColorSensor lightSensor;
	private static ColorController colorCont;
	private static Odometer odometer;
	private static OdometryDisplay odometryDisplay;
<<<<<<< HEAD:src/ca/mcgill/ecse211/beta/Resources.java
	//private static final double TRACK = 10.2;
	private static final double TRACK = 15.6;
=======
	private static final double TRACK = 17.5;
>>>>>>> 79c4aef00776170a8f78d7ec44dc2ec6f17cdd82:src/ca/mcgill/ecse211/resources/Resources.java
	private static final double RADIUS = 2.093;

	
	/**
	 * Initializes a class used to hold any potential constants required by multiple classes
	 * values are retrieved using getter methods
	 * 
	 * @param leftMotorPort Port at which the left motor is attached
	 * @param rightMotorPort Port at which the right motor is attached
	 * @param zipMotorPort Port at which the zipline motor is attached
	 * @param lightSensorPort Port at which the lightsensor is attached
	 * @param ultrasonicSensorPort Port at which the ultrasonic sensor is attached
	 */
	public Resources(String leftMotorPort, String rightMotorPort, 
			String zipMotorPort, String lightSensorPort, String ultrasonicSensorPort) {
		leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort(leftMotorPort));
		rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort(rightMotorPort));
		zipMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort(zipMotorPort));
		lightSensor = new EV3ColorSensor(LocalEV3.get().getPort(lightSensorPort));
		ultrasonicSensor = new EV3UltrasonicSensor(LocalEV3.get().getPort(ultrasonicSensorPort));
		usCont = new UltrasonicController();
		colorCont = new ColorController();
		// this instance
		odometer = new Odometer();
	}
	
	/**
	 * Used to return the light sensor instance defined for the program
	 * 
	 * @return EV3ColorSensor
	 */
	public static EV3ColorSensor getColorSensor() {
		return lightSensor;
	}
	
	public static ColorController getColorController() {
		return colorCont;
	}
	
	/**
	 * Used to return the track value defining the wheel width of the robot
	 * 
	 * @return double
	 */
	public static double getTrack() {
		return TRACK;
	}
	
	/**
	 * Used to return the initialized odometer for the program
	 * 
	 * @return Odometer
	 */
	public static Odometer getOdometer() {
		return odometer;
	}
	
	
	/**
	 * Used to return the radius value of the wheels
	 * 
	 * @return double
	 */
	public static double getRadius() {
		return RADIUS;
	}
	
	/**
	 * Used to return the initialized instance for the leftMotor
	 * 
	 * @return EV3LargeRegulatedMotor
	 */
	public static EV3LargeRegulatedMotor getLeftMotor() {
		return leftMotor;
	}
	
	/**
	 * Used to return the initialized instance for the rightMotor
	 * 
	 * @return EV3LargeRegulatedMotor
	 */
	public static EV3LargeRegulatedMotor getRightMotor() {
		return rightMotor;
	}
	
	/**
	 * Used to return the initialized instance for the ziplineMotor
	 * 
	 * @return EV3LargeRegulatedMotor
	 */
	public static EV3LargeRegulatedMotor getZipMotor() {
		return zipMotor;
	}
	
	/**
	 * Used to return the initialized instance of the ultrasonic sensor
	 * 
	 * @return EV3UltrasonicSensor
	 */
	public static EV3UltrasonicSensor getUltrasonicSensor() {
		return ultrasonicSensor;
	}
	
	public static UltrasonicController getUltrasonicController() {
		return usCont;
	}
	
	/**
	 * Used to return both left and right motors in one method call
	 * 
	 * @return EV3LargeRegulatedMotor[] = {leftMotor, rightMotor};
	 */
	public static EV3LargeRegulatedMotor[] getBothMotors() {
		EV3LargeRegulatedMotor[] val = {leftMotor, rightMotor};
		return val;
	}
}
