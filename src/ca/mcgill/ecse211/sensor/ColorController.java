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
	private static float colorValFrontR, colorValFrontG, colorValFrontB;

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
	public void processColorData(float valMiddle, float valFrontR, float valFrontG, float valFrontB) {

		prevValMiddle[0] = prevValMiddle[1];
		prevValMiddle[1] = valMiddle;

		ColorController.colorValMiddle = prevValMiddle[1] - prevValMiddle[0];

		ColorController.colorValFrontR = valFrontR;
		ColorController.colorValFrontG = valFrontG;
		ColorController.colorValFrontB = valFrontB;

		if (colorValMiddle > 0.10) {
			middleLineDetected = true;
		} else {
			middleLineDetected = false;
		}
	};

	/**
	 * Returns if middle color sensor has detected anything
	 * 
	 * @return boolean
	 */
	public static boolean middleLineDetected() {
		return middleLineDetected;
	}

	/**
	 * Returns color sensor reading of middle color sensor
	 * 
	 * @return float
	 */
	public static float getColorValMiddle() {
		return colorValMiddle;
	}

	/**
	 * Returns if block color detected matches desired block color
	 * 
	 * @return boolean
	 */
	public static boolean blockMatch(int blockColor) {
		// switch on blockColor, testing color sensor values
		//note the boundaries were determined experimentally through our tests (see Testing Document)
		switch (blockColor) {
		// red block case
		case 1:
			if (colorValFrontR <= 0.12 && colorValFrontR >= 0.07 && colorValFrontG <= 0.017 && colorValFrontG >= 0.01
					&& colorValFrontB <= 0.015 && colorValFrontB >= 0.009) {
				return true;
			} else {
				return false;
			}
		case 2:
			//blue block case
			if (colorValFrontR <= 0.02 && colorValFrontR >= 0.01 && colorValFrontG <= 0.05 && colorValFrontG >= 0.02
					&& colorValFrontB <= 0.07 && colorValFrontB >= 0.04) {
				return true;
			} else {
				return false;
			}
		case 3:
			//yellow block case
			if (colorValFrontR <= 0.2 && colorValFrontR >= 0.1 && colorValFrontG <= 0.18 && colorValFrontG >= 0.09
					&& colorValFrontB <= 0.025 && colorValFrontB >= 0.017) {
				return true;
			} else {
				return false;
			}

		case 4:
			//white case
			if (colorValFrontR <= 0.05 && colorValFrontR >= 0.03 && colorValFrontG <= 0.08 && colorValFrontG >= 0.05
					&& colorValFrontB <= 0.05 && colorValFrontB >= 0.03) {
				return true;
			} else {
				return false;
			}

		}
		return false;
	}
}