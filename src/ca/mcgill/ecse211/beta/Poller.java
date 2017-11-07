package ca.mcgill.ecse211.beta;

import ca.mcgill.ecse211.beta.UltrasonicController;
import lejos.robotics.SampleProvider;

public class Poller extends Thread {
	private SampleProvider us = Resources.getUltrasonicSensor();
	private SampleProvider usDistance = Resources.getUltrasonicSensor().getMode("Distance");
	private float[] usData = new float[usDistance.sampleSize()];
	private UltrasonicController usCont;
	
	
	private SampleProvider colorSensor = Resources.getColorSensor().getMode("Red");
	private float[] colorData = new float[colorSensor.sampleSize()];
	private ColorController colorCont;
	
	public Poller(UltrasonicController usCont, ColorController colorCont) {
		this.usCont = usCont;
		this.colorCont = colorCont;
	}
	
	public void run() {
		int distance;
		float color;
		while (true) {
			us.fetchSample(usData, 0); // acquire data
			colorSensor.fetchSample(colorData, 0);
			distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
			color = colorData[0];
			usCont.processUSData(distance); // now take action depending on value
			colorCont.processColorData(color);
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}
}
