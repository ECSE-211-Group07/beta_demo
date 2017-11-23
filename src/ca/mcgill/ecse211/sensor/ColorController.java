package ca.mcgill.ecse211.sensor;

import lejos.hardware.Sound;

/**
 * ColorController class that interfaces with Poller class to process readings
 * from color sensor
 * 
 * @author Adam Gobran, Ali Shobeiri
 *
 */
public class ColorController {
	private static float colorValMiddle;
	private static float[] prevValMiddle = new float[2];
	private static boolean middleLineDetected = false;

	public ColorController() {
	};

	/**
	 * Processes readings from color sensor
	 * 
	 * @param valLeft
	 * @param valRight
	 */
	public void processColorData(float valMiddle) {

		prevValMiddle[0] = prevValMiddle[1];
		prevValMiddle[1] = valMiddle;

		ColorController.colorValMiddle = prevValMiddle[1] - prevValMiddle[0];

		if (colorValMiddle > 0.10) {
			middleLineDetected = true;
		} else {
			middleLineDetected = false;
		}
	};

	public static boolean middleLineDetected() {
		return middleLineDetected;
	}
	
	public static float getColorValMiddle() {
		return colorValMiddle;
	}
}