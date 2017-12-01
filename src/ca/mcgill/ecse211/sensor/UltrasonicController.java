package ca.mcgill.ecse211.sensor;

/** UltrasonicController class that interfaces with Poller class to process readings from ultrasonic sensor
 * @author Adam Gobran, Ali Shobeiri
 *
 */
public class UltrasonicController {
	private static int distance;
	private static int blockDist = 50;
	public static Object lock = new Object();
	
	public UltrasonicController() {}

	
	/** Processes distance from sensor and updates local variable distance
	 * @param distance
	 */
	public void processUSData(int distance) {
		UltrasonicController.distance = distance;
	};

	
	/** Returns local variable distance which represents current ultrasonic reading
	 * @return current ultrasonic reading
	 */
	public static int readUSDistance() {
		synchronized(lock) {
			return distance;
		}
	};
	
	/** Returns variable depending if ultrasonic reading is less than blockDist class variable
	 * @return boolean indicating if block has been detected
	 */
	public static boolean blockDetected() {
		return (readUSDistance() < blockDist);
	}
}