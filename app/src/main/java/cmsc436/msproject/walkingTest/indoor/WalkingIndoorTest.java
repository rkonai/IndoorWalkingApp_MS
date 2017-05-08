package cmsc436.msproject.walkingTest.indoor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.SystemClock;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cmsc436.msproject.R;
import cmsc436.msproject.util.Utilities;
import cmsc436.msproject.walkingTest.util.StepDetector;
import cmsc436.msproject.walkingTest.util.StepListener;
import android.view.*;
import android.widget.*;
import android.widget.Toolbar;

/**
 * This activity performs the Indoor Walking test
 */
public class WalkingIndoorTest extends AppCompatActivity implements SensorEventListener,
        LocationListener, StepListener {
//    /**
//     * TextView to display the total distance the user has walked
//     */
//    private TextView distanceLabel;
//
//    /**
//     * TextView to display the total distance the user has walked
//     */
//    private TextView velocityLabel;


    /*private static final String CHECK_KEY = "CHECK_KEY";*/


    /**
     * TextView to display the number of steps the user has taken
     */
    private TextView numStepsLabel;

    /**
     * Android's timer widget
     */
    private Chronometer chronometer;


    /**
     * ToneGenerator to signal the user with beeps
     */
    private ToneGenerator toneGenerator;

    /**
     * The date and time the user started the test
     */
    private String testDate;

    /**
     * Time it took user to complete the test
     */
    private float testDuration;

    /**
     * Time when the test is started
     */
    private long testStartTime;

    /**
     * Time when the test has ended
     */
    private long testEndTime;

    /**
     * The previous device location
     */
    private Location prevLocation;

    /**
     * This variable keeps track of the total number of steps the user has taken
     */
    private int numSteps = 0;

    /**
     * The total distance the user walked in meters
     */
    private float distanceWalked;

    /**
     * The phone's previous velocity
     */
    private float prevVelocity;

    /**
     * The sum of all the velocity's taken
     */
    private float totalVelocity;

    /**
     * The number of velocity measures taken
     */
    private int velocityCount;

    /**
     * The user's average velocity
     */
    private double aveVelocity;


    /**
     * Current Distance travled
     */
    private float currDistance;

    /**
     * Distance we want the user to travel
     */
    private float maxDistance = 25;


    // The sensors
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor linAccelerometer;
    private Sensor pedometer;
    private StepDetector stepDetector;

//    /**
//     * LocationManager to get the user indoor location
//     */
//    LocationManager locationManager;

    /**
     * True if the phone contains a Step Detector Sensor, false otherwise
     */
    private boolean hasStepDetector;

    /**
     * The request code for using the network location
     */
    private static final int NETWORK_LOCATION_REQUEST_CODE = 0;

    private static final String LOG_TAG = WalkingIndoorTest.class.getSimpleName();
    private static final String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;
    private static final DateFormat dateFormat = new SimpleDateFormat("M/dd/yy h:mm aa", Locale.US);
    private static final DecimalFormat df = new DecimalFormat("#.#");
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private float calibratedSteps = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_indoor_test);

        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize activity layout views
//        distanceLabel = (TextView) findViewById(R.id.distanceLabel);
//        String distanceLabelText = "Distance walked: 0 m";
//        distanceLabel.setText(distanceLabelText);
//
//        velocityLabel = (TextView) findViewById(R.id.velocityLabel);
//        velocityLabel.setText("0 m/s");

        numStepsLabel = (TextView) findViewById(R.id.numStepsLabel);
        String numStepsLabelText = "0";
        numStepsLabel.setText(numStepsLabelText);


        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                updateVelocity();
            }
        });

        findViewById(R.id.complete_test_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTest();
            }
        });

        // Initialize the sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        pedometer = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);

        // Initialize the ToneGenerator
        toneGenerator = new ToneGenerator(AudioManager.STREAM_RING, 100);

        Bundle extras = this.getIntent().getExtras();

        // Initialize primitive variables
        numSteps = 0;
        distanceWalked = 0;
        testDuration = 0;
        prevVelocity = 0;
        totalVelocity = 0;
        velocityCount = 0;
        aveVelocity = 0;
        currDistance = 0;
        /*userCalDistancePerStep = readFloatFromSharePref(CHECK_KEY);
        System.out.println("USER CALC DIST PER STEPPPPP:::::::" + userCalDistancePerStep);*/

//        // Initialize LocationManager
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )
//                == PackageManager.PERMISSION_GRANTED) {
//            Log.d(LOG_TAG, "App has Network Location permission");
//            displayBeginTestDialogFragment();
//        }
//        else{
//            Log.d(LOG_TAG, "App does not have Network Location permission");
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, NETWORK_LOCATION_REQUEST_CODE);
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(LOG_TAG + ".onWindowFocusChanged()", "hasFocus = " + hasFocus);
        if (hasFocus) {
            testStartTime = System.currentTimeMillis();
            Calendar cal = Calendar.getInstance();
            testDate = dateFormat.format(cal.getTime());
            try {
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//                prevLocation = locationManager.getLastKnownLocation(LOCATION_PROVIDER);
//                Log.d(LOG_TAG + ".onWindowFocusChanged()", prevLocation.toString());
            }
            catch (SecurityException e){
                Log.d(LOG_TAG, "locationManager does not have permission for Network Provider");
                e.printStackTrace();
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            registerSensors();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        }
        else{
            chronometer.stop();
            unregisterSensors();
//            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        chronometer.stop();
        unregisterSensors();
        toneGenerator.release();
//        locationManager.removeUpdates(this);
    }

    /**
     * Display the DialogFragment to let the user begint he test
     */
    private void displayBeginTestDialogFragment(){
        String fragmentMessage = "Begin indoor walking test";
        Utilities.displayAlertDialogFragment(getFragmentManager(), "Indoor Walking Test", fragmentMessage);
    }

    /**
     * Register the sensors sensor in the SensorManager
     */
    private void registerSensors(){
        Log.d(LOG_TAG, "registerSensors()");
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, linAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        hasStepDetector = sensorManager.registerListener(this, pedometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (!hasStepDetector){
            Log.d(LOG_TAG, "Step detector is not supported, using accelerometer instead");
        }
    }

    /**
     * Unregister the sensors sensor from the SensorManager
     */
    private void unregisterSensors(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            updateSteps();
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            if (!hasStepDetector) {
                stepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
            }
        }
    }

    @Override
    public void step(long timeNs) {
        updateSteps();
    }

    private void updateVelocity(){
        float velocity = Math.abs(stepDetector.getGoodVelocityEstimate());
        if (velocity == prevVelocity){
            velocity = 0;
        }
        else {
            prevVelocity = velocity;
        }
        totalVelocity += velocity;
        velocityCount++;
//        String text = df.format(velocity) + " m/s";
//        velocityLabel.setText(text);
    }

    /**
     * Increment the number of steps the user has taken and display it
     */
    private void updateSteps(){
        numSteps++;
        String text = Integer.toString(numSteps);
        numStepsLabel.setText(text);
    }

    public void endTest()
    {
        testEndTime = System.currentTimeMillis();
        unregisterSensors();
        chronometer.stop();
        endTestBeep();
        completeTest();
    }

    /**
     * Output a beep to alert the user the calibration has ended
     */
    private void endTestBeep(){
        Vibrator vibrator = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 1000);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG + ".onLocationChanged()", location.toString());
        location.getAccuracy();

        if (prevLocation != null){
            distanceWalked += location.distanceTo(prevLocation);
//            String distanceLabelText = "Distance: " + df.format(distanceWalked) + " m";
//            distanceLabel.setText(distanceLabelText);
        }
        prevLocation = location;
    }

    /**
     * This method completes the Walking Indoor Test and sends the results to the WalkingIndoorResults activity
     */
    private void completeTest(){
        //eStop();
        testDuration = testEndTime - testStartTime;
        if (velocityCount > 0) {
            aveVelocity = totalVelocity / velocityCount;
        }

        distanceWalked = currDistance; //We now calc this
        aveVelocity =

        Log.d(LOG_TAG, "testDuration = " + testDuration);
        Log.d(LOG_TAG, "totalVelocity = " + totalVelocity);
        Log.d(LOG_TAG, "velocityCount = " + velocityCount);
        Log.d(LOG_TAG, "aveVelocity = " + aveVelocity);


        Intent intent = new Intent(this, WalkingIndoorResults.class);
        intent.putExtra(WalkingIndoorUtil.TEST_DATE_TAG, testDate);
        intent.putExtra(WalkingIndoorUtil.TIME_TAG, testDuration);
        //intent.putExtra(WalkingIndoorUtil.VELOCITY_TAG, aveVelocity);
        //intent.putExtra(WalkingIndoorUtil.DISTANCE_TAG, distanceWalked);
        Float retFloat = new Float(currDistance);
        String floatString = retFloat.toString();

        //intent.putExtra("DIST",  floatString);
        //intent.putExtra("TIME", testDuration);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        intent.putExtra("NumSteps", numSteps);
        intent.putExtra("Aide", (String) this.getIntent().getExtras().get("Aide"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(LOG_TAG, "onRequestPermissionsResult()");
        switch (requestCode) {
            case NETWORK_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the task you need to do.
                    Log.d(LOG_TAG, "Network location permission granted");
                    displayBeginTestDialogFragment();
                }
                else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Log.d(LOG_TAG, "Network location permission not granted");
                    this.finish();
                }
            }

            // other 'case' lines to check for other permissions this app might request
        }
    }


/*    private void writeFloatToSharePref(float f){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(CHECK_KEY,f);
        editor.apply();

    }*/

    private float readFloatFromSharePref(String key){
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        return sp.getFloat(key, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help_icon:
                Intent intent = new Intent(this, WalkingIndoorInstructions.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
