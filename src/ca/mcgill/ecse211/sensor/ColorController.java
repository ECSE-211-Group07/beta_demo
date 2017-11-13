package ca.mcgill.ecse211.sensor;

import lejos.hardware.Sound;

/** ColorController class that interfaces with Poller class to process readings from color sensor
 * @author Adam Gobran, Ali Shobeiri
 *
 */
public class ColorController {
	private static float colorValLeft, colorValRight, colorValMiddle;
	private static float[] prevValLeft = new float[2];
	private static float[] prevValRight = new float[2];
	private static float[] prevValMiddle = new float[2];
	private static boolean leftLineDetected = false;
	private static boolean rightLineDetected = false;
	private static boolean middleLineDetected = false;
	private static boolean bothLinesDetected = false;
	
	
	public ColorController() {};
	
	/** Processes readings from color sensor
	 * @param valLeft
	 * @param valRight
	 */
	public void processColorData(float valLeft, float valRight, float valMiddle) {
		prevValLeft[0] = prevValLeft[1];
		prevValLeft[1] =  valLeft;
	
		ColorController.colorValLeft = prevValLeft[1] - prevValLeft[0];
		
		prevValRight[0] = prevValRight[1];
		prevValRight[1] =  valRight;
	
		ColorController.colorValRight = prevValRight[1] - prevValRight[0];
		
		prevValMiddle[0] = prevValMiddle[1];
		prevValMiddle[1] =  valMiddle;
	
		ColorController.colorValMiddle = prevValMiddle[1] - prevValMiddle[0];
		
		changeState();
	};

	/** Returns local colorValLeft variable representing current reading of left color sensor
	 * @return float
	 */
	public static float readColorDataLeft() {
		return colorValLeft;
	};
	
	/** Returns local colorValRight variable representing current reading of right color sensor
	 * @return float
	 */
	public static float readColorDataRight() {
		return colorValRight;
	};
	
	/** Changes local state variables according to whether lines have been detected
	 * 
	 */
	private static void changeState() {
		if (colorValLeft > 0.10) {
			leftLineDetected = true;
		} else {
			leftLineDetected = false;
		}
		
		if(colorValRight > 0.10) {
			rightLineDetected = true;
		} else {
			rightLineDetected = false;
		}
		
		if(colorValMiddle > 0.10) {
			middleLineDetected = true;
		} else {
			middleLineDetected = false;
		}
		
		if(leftLineDetected && rightLineDetected) {
			bothLinesDetected = true;
		}
	}
	
	/** Returns boolean indicating if left line has been detected
	 * @return boolean
	 */
	public static boolean leftLineDetected() {
		return leftLineDetected;
	}
	
	/** Returns boolean indicating if right line has been detected
	 * @return boolean
	 */
	public static boolean rightLineDetected() {
		return rightLineDetected;
	}
	
	public static boolean middleLineDetected() {
		return middleLineDetected;
	}
	
	
	/** Returns boolean indicating if both lines have been detected
	 * @return boolean
	 */
	public static boolean bothLineDetected() {
		return bothLinesDetected;
	}
	
	/** Returns boolean indicating if any line has been detected
	 * @return boolean
	 */
	public static boolean anyLineDetected() {
		return (rightLineDetected || leftLineDetected);
	}
}