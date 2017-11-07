package ca.mcgill.ecse211.beta;

public class ColorController {
	private static float colorVal;
	private static float[] prevVal = new float[2];
	
	public ColorController() {};
	
	public void processColorData(float val) {
		prevVal[0] = prevVal[1];
		prevVal[1] =  val;
		
		ColorController.colorVal = prevVal[1] - prevVal[0];
	};

	public static float readColorData() {
		return colorVal;
	};
}