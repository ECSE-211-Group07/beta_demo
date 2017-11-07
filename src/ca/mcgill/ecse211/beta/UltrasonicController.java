package ca.mcgill.ecse211.beta;

public class UltrasonicController {
	private static int distance;
	public static Object lock = new Object();
	
	public UltrasonicController() {}

	public void processUSData(int distance) {
		UltrasonicController.distance = distance;
	};

	public static int readUSDistance() {
		synchronized(lock) {
			return distance;
		}
	};
}