package ca.mcgill.ecse211.sensor;

import ca.mcgill.ecse211.resources.Resources;
import ca.mcgill.ecse211.sensor.UltrasonicController;
import lejos.robotics.SampleProvider;

/** Class that handles consistently sampling sensors and interfacing with sensor controllers
 * @author Adam Gobran, Ali Shobeiri
 *
 */
public class Poller extends Thread {
	private SampleProvider us = Resources.getUltrasonicSensor();
	private SampleProvider usDistance = Resources.getUltrasonicSensor().getMode("Distance");
	private float[] usData = new float[usDistance.sampleSize()];
	private UltrasonicController usCont;
	
	
	private SampleProvider colorSensorLeft = Resources.getColorSensorLeft().getMode("Red");
	private SampleProvider colorSensorRight = Resources.getColorSensorRight().getMode("Red");
	private float[] colorDataLeft = new float[colorSensorLeft.sampleSize()];
	private float[] colorDataRight = new float[colorSensorRight.sampleSize()];
	private ColorController colorCont;
	
	
	/** Constructor for Poller object. The Poller class handles sampling all of our sensors and ensuring 
	 * they interface properly with the corresponding sensor controllers
	 * @param usCont
	 * @param colorCont
	 */
	public Poller(UltrasonicController usCont, ColorController colorCont) {
		this.usCont = usCont;
		this.colorCont = colorCont;
	}
	
	
	public void run() {
		int distance;
		float colorLeft, colorRight;
		while (true) {
			us.fetchSample(usData, 0); // acquire data
			colorSensorLeft.fetchSample(colorDataLeft, 0);
			colorSensorRight.fetchSample(colorDataRight, 0);
			distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
			colorLeft = colorDataLeft[0];
			colorRight = colorDataRight[0];
			usCont.processUSData(distance); // now take action depending on value
			colorCont.processColorData(colorLeft, colorRight);
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}
}
