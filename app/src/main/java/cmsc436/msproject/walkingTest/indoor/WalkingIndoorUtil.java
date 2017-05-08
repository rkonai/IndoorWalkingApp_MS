package cmsc436.msproject.walkingTest.indoor;


public class WalkingIndoorUtil {
    /**
     * Intent tag to pass the test date
     */
    public static final String TEST_DATE_TAG = "TEST_DATE";

    /**
     * Intent tage to pass the total time it took user to complete the test
     */
    public static final String TIME_TAG = "TIME";

    /**
     * Intent tage to pass the user's average velocity in meter/sec
     */
    public static final String VELOCITY_TAG = "VELOCITY";

    /**
     * Intent tage to pass the user's average velocity in meter/sec
     */
    public static final String DISTANCE_TAG = "DISTANCE";

    /**
     * The number of steps the user has to take
     */
    public static final int MAX_STEPS = 25;

    /**
     * Name of the internal file in which to save the data to
     */
    public static final String STORAGE_FILE = "WALKING_INDOOR_TEST_DATA";

    /**
     * The metric to measure the user's velocity for the Indoor Walking test
     */
    public static final String VEL_METRIC = "feet/min";
}
