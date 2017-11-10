package ca.mcgill.ecse211.sensor;

public class ColorController {
	private static float colorValLeft, colorValRight;
	private static float[] prevValLeft = new float[2];
	private static float[] prevValRight = new float[2];
	private static boolean leftLineDetected = false;
	private static boolean rightLineDetected = false;
	private static boolean bothLinesDetected = false;
	
	
	public ColorController() {};
	
	public void processColorData(float valLeft, float valRight) {
		prevValLeft[0] = prevValLeft[1];
		prevValLeft[1] =  valLeft;
	
		ColorController.colorValLeft = prevValLeft[1] - prevValLeft[0];
		
		prevValRight[0] = prevValRight[1];
		prevValRight[1] =  valRight;
	
		ColorController.colorValRight = prevValRight[1] - prevValRight[0];
		
		changeState();
	};

	public static float readColorDataLeft() {
		return colorValLeft;
	};
	
	public static float readColorDataRight() {
		return colorValRight;
	};
	
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
		
		if(leftLineDetected && rightLineDetected) {
			bothLinesDetected = true;
		}
	}
	
	public static boolean leftLineDetected() {
		return leftLineDetected;
	}
	
	public static boolean rightLineDetected() {
		return rightLineDetected;
	}
	
	public static boolean bothLineDetected() {
		return bothLinesDetected;
	}
	
	public static boolean anyLineDetected() {
		return (rightLineDetected || leftLineDetected);
	}
}