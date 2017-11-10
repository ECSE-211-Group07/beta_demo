package ca.mcgill.ecse211.sensor;

public class ColorController {
	private static float colorVal;
	private static float[] prevVal = new float[2];
	
	public ColorController() {};
	
	/** Processes data from color sensor and updates local colorVal variable
	 * @param val
	 */
	public void processColorData(float val) {
		prevVal[0] = prevVal[1];
		prevVal[1] =  val;
		
		ColorController.colorVal = prevVal[1] - prevVal[0];
	};
	
	
	/** Returns local colorVal variable
	 * @return float
	 */
	public static float readColorData() {
		return colorVal;
	};
}