package cmsc436.msproject.walkingTest.indoor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import edu.umd.cmsc436.frontendhelper.TrialMode;
import edu.umd.cmsc436.sheets.Sheets;

import cmsc436.msproject.R;
import cmsc436.msproject.util.Utilities;

/**
 * This activity displays the user's results after the Indoor Walking test has completed
 */
public class WalkingIndoorResults extends AppCompatActivity implements Sheets.Host {
    private RelativeLayout rootView;
    private ScrollView scrollView;
    private TextView testResultsTextView;
    private TextView aideTextView;
    private TextView aideDataTextView;
    private TextView timeTextView;
    private TextView timeDataTextView;
    private TextView speedTextView;
    private TextView speedDataTextView;
    private Button mainMenuBtn;
    private TextView[][] tableRows;

    /**
     * Sheets object
     */
    private Sheets sheet;

    public static final int LIB_ACCOUNT_NAME_REQUEST_CODE = 1001;
    public static final int LIB_AUTHORIZATION_REQUEST_CODE = 1002;
    public static final int LIB_PERMISSION_REQUEST_CODE = 1003;
    public static final int LIB_PLAY_SERVICES_REQUEST_CODE = 1004;
    public static final int LIB_CONNECTION_REQUEST_CODE = 1005;

    /**
     * Date and time the test was taken
     */
    private String testDate;

    /**
     * Number of steps the user took
     */
    private int numSteps = 0;

    /**
     * How long it took the user to complete the test
     */
    private float testDuration;

    /**
     * User's velocity in meters/second
     */
    private double velocitySPS;

    /**
     * User's velocity in feet/minute
     */
    private double velocityFM;

    /**
     * The total distance the user walked
     */
    private float distance;

    /**
     * True if the test results have already been saved to the phone's internal storage, false otherwise
     */
    private boolean savedData;

    private static final String LOG_TAG = WalkingIndoorResults.class.getSimpleName();
    private static final String SAVED_DATA_TAG = "SAVED_DATA";
    private static final DecimalFormat df = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_indoor_results);

        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        String appTitle = getTitle() + " Results";
        setTitle(appTitle);

        // Initalize Activity layouts
        rootView = (RelativeLayout) findViewById(R.id.relativeLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        testResultsTextView = (TextView) findViewById(R.id.testResultsTextView);
        aideTextView = (TextView) findViewById(R.id.aideTextView);
        aideDataTextView = (TextView) findViewById(R.id.aideDataTextView);
        timeTextView = (TextView) findViewById(R.id.timeLabelTextView);
        timeDataTextView = (TextView) findViewById(R.id.timeDataTextView);
        speedTextView = (TextView) findViewById(R.id.speedLabelTextView);
        speedDataTextView = (TextView) findViewById(R.id.speedDataTextView);
        mainMenuBtn = (Button) findViewById(R.id.mainMenuBtn);
        mainMenuBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        tableRows = new TextView[][]{{aideTextView, aideDataTextView},
                {timeTextView, timeDataTextView},
                {speedTextView, speedDataTextView}};

        //init sheets
        sheet = new Sheets(this, this, getString(R.string.app_name),
                "1YvI3CjS4ZlZQDYi5PaiA7WGGcoCsZfLoSFM0IdvdbDU" , "13_ig4ShrqxmNlcZum4Urjm8r5wb-GB03MD6my63mOEI");
        Intent intent = getIntent();

        distance = 25f;
        testDuration = (float) intent.getExtras().get(WalkingIndoorUtil.TIME_TAG);


        if (!WalkingIndoorMenu.isTrialMode) {

            Intent launchingIntent = WalkingIndoorMenu.startingIntent;
            //String patientID = TrialMode.getPatientId(launchingIntent);
            String patientID = "t05p01";
            float trialNum = TrialMode.getTrialNum(launchingIntent);
            float trialOutOf = TrialMode.getTrialOutOf(launchingIntent);
            Sheets.TestType testType = TrialMode.getAppendage(launchingIntent);
            int difficulty = TrialMode.getDifficulty(launchingIntent);

            /*if (intent.getStringExtra(WalkingIndoorUtil.VELOCITY_TAG) != null) {

                testDate = intent.getStringExtra(WalkingIndoorUtil.TEST_DATE_TAG);
                numSteps = Integer.valueOf(intent.getStringExtra("NUM_STEPS"));
                testDuration = intent.getLongExtra(WalkingIndoorUtil.TIME_TAG, 0);

                //THIS IS FOR TESTING API CALLS
                String aide = (String) this.getIntent().getExtras().get("Aide");
                float aideType = 0;
                if (aide == null || aide.equals("No Help")) aideType = 0;
                else if (aide.equals("Cane")) aideType = 1;
                else if (aide.equals("Walker")) aideType = 2;
                else if (aide.equals("Partner")) aideType = 3;

                float[] trials = {numSteps, round(testDuration / 1000.0f, 2), -1f, -1f, aideType};
                sheet.writeData(Sheets.TestType.INDOOR_WALKING, patientID, -1f);
                sheet.writeTrials(Sheets.TestType.INDOOR_WALKING, patientID, trials);

            } else {*/

                // Get test results
                testDate = intent.getStringExtra(WalkingIndoorUtil.TEST_DATE_TAG);
                numSteps = (int) intent.getExtras().get("NumSteps");
                testDuration = (float) intent.getExtras().get(WalkingIndoorUtil.TIME_TAG);
                velocitySPS = 25.0 / (testDuration / 1000.0);
                distance = 25f;
                //First code is central sheets (maps only speed) second code is for personal sheets (maps float array of trial info)

                //THIS IS FOR TESTING API CALLS
                String aide = (String) this.getIntent().getExtras().get("Aide");
                float aideType = 0;
                if (aide == null || aide.equals("No Help")) aideType = 0;
                else if (aide.equals("Cane")) aideType = 1;
                else if (aide.equals("Walker")) aideType = 2;
                else if (aide.equals("Partner")) aideType = 3;

                float[] trials = {numSteps, round(testDuration / 1000.0f, 2), distance, round(velocitySPS, 2), aideType};
                sheet.writeData(Sheets.TestType.INDOOR_WALKING, patientID, round(velocitySPS, 2));
                sheet.writeTrials(Sheets.TestType.INDOOR_WALKING, patientID, trials);
            /*}*/
        }
        displayTestResults();

        // Get data from savedInstanceState
        if (savedInstanceState != null){
            savedData = savedInstanceState.getBoolean(SAVED_DATA_TAG);
        }

        // Check if the results have already been saved to the phone's internal storage
        if (!savedData) {
            saveData();
        }

        String fileData = Utilities.readFromInternalStorage(getApplicationContext(), WalkingIndoorUtil.STORAGE_FILE);
        Log.d(LOG_TAG, "fileData = \n" + fileData);
    }

    public float round(double value, int scale) {
        return (float) (Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(LOG_TAG, "onWindowFocusChanged() - hasFocus = " + hasFocus);
        if (hasFocus) {
            setScrollViewHeight();
            setTableRowHeight();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVED_DATA_TAG, savedData);
        super.onSaveInstanceState(outState);
    }

    /**
     * Display the user's test results
     */
    private void displayTestResults(){
        // Display number of steps per second
       // timeDataTextView.setText(this.getIntent().getExtras().get("TIME").toString());
        aideDataTextView.setText((String) this.getIntent().getExtras().get("Aide"));
        speedDataTextView.setText(round(25f / (testDuration/1000f), 2)
                + " feet per sec");

        // Display test time
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) testDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) testDuration);
        String timeLabelText;
        if (minutes == 0){
            double millisec = ((double)(testDuration % 1000)) / 1000;
            Log.d(LOG_TAG, "millisec = " + millisec);
            double time = ((double) seconds) + millisec;
            Log.d(LOG_TAG, "time = " + time);
            timeLabelText = new DecimalFormat("#.#").format(time) + " sec";
            timeDataTextView.setText(timeLabelText);
        }else{
            timeLabelText = String.format("%d:%02d", minutes, seconds);
        }
        timeDataTextView.setText(timeLabelText);

        // Display user's velocity
       // String speedLabelText = round(velocitySPS,2) + " Steps per Second";//df.format(velocityFM) + " " + WalkingIndoorUtil.VEL_METRIC;
       // speedDataTextView.setText(speedLabelText);
    }

    /**
     * Save the test date and the user's step/sec to the phone's internal storage
     */
    private void saveData(){
        StringBuilder data = new StringBuilder();
        data.append(testDate).append(",").append(df.format(velocitySPS)).append("\n");
        Log.d(LOG_TAG, "data = " + data.toString());
        Utilities.saveToInternalStorage(getApplicationContext(), WalkingIndoorUtil.STORAGE_FILE, data.toString());
        savedData = true;
    }

    /**
     * Dynamically set the height of the ScrollView so it fits after the TextView label and before the menu button
     */
    private void setScrollViewHeight(){
        int relLayoutHeight = rootView.getHeight();
        int labelHeight = testResultsTextView.getHeight();
        int margin = (int) getResources().getDimension(R.dimen.margin);
        int scrollViewHeight = relLayoutHeight - labelHeight - margin;

        RelativeLayout.LayoutParams scrollViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, scrollViewHeight);
        scrollViewParams.addRule(RelativeLayout.BELOW, R.id.testResultsTextView);
        scrollViewParams.setMargins(10, 0, 10, 0);
        scrollView.setLayoutParams(scrollViewParams);
    }

    /**
     * Dynamically set all the columns in a table row to have the same height
     */
    private void setTableRowHeight(){
        Log.d(LOG_TAG, Arrays.deepToString(tableRows));
        for (TextView[] tableRow: tableRows){
            // get the height of the label cell
            int labelCellHeight = tableRow[0].getHeight();

            // get the height of the data cell
            int dataCellHeight = tableRow[1].getHeight();

            // if the two cells are not the same height
            if (labelCellHeight != dataCellHeight){
                int height = Utilities.max(labelCellHeight, dataCellHeight);
                TableRow.LayoutParams labelParam = new TableRow.LayoutParams(0, height, 2.0f);
                tableRow[0].setLayoutParams(labelParam);

                TableRow.LayoutParams dataParam = new TableRow.LayoutParams(0, height, 3.0f);
                tableRow[1].setLayoutParams(dataParam);
            }
        }
    }

    //SHEETS INTERFACE


    @Override
    public int getRequestCode(Sheets.Action action) {
        switch (action) {
            case REQUEST_ACCOUNT_NAME:
                return LIB_ACCOUNT_NAME_REQUEST_CODE;
            case REQUEST_AUTHORIZATION:
                return LIB_AUTHORIZATION_REQUEST_CODE;
            case REQUEST_PERMISSIONS:
                return LIB_PERMISSION_REQUEST_CODE;
            case REQUEST_PLAY_SERVICES:
                return LIB_PLAY_SERVICES_REQUEST_CODE;
            default:
                return -1;
        }
    }

    @Override
    public void notifyFinished(Exception e) {
        if (e != null) {
            throw new RuntimeException(e);
        }
        Log.i(getClass().getSimpleName(), "Done");
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        this.sheet.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.sheet.onActivityResult(requestCode, resultCode, data);
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
