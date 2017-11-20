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
	
	
	private SampleProvider colorSensorMiddle = Resources.getColorSensorMiddle().getMode("Red");
	

	private float[] colorDataMiddle = new float[colorSensorMiddle.sampleSize()];
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
		float colorLeft, colorRight, colorMiddle;
		while (true) {
			us.fetchSample(usData, 0); // acquire data
			colorSensorMiddle.fetchSample(colorDataMiddle, 0);
			distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
			colorMiddle = colorDataMiddle[0];
			usCont.processUSData(distance); // now take action depending on value
			colorCont.processColorData(colorMiddle);
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}
}
