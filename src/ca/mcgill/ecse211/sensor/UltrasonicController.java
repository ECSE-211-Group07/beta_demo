package ca.mcgill.ecse211.sensor;

public class UltrasonicController {
	private static int distance;
	public static Object lock = new Object();
	
	public UltrasonicController() {}

	
	/** Processes distance from sensor and updates local variable distance
	 * @param distance
	 */
	public void processUSData(int distance) {
		UltrasonicController.distance = distance;
	};

	
	/** Returns local variable distance which represents current ultrasonic reading
	 * @return
	 */
	public static int readUSDistance() {
		synchronized(lock) {
			return distance;
		}
	};
}