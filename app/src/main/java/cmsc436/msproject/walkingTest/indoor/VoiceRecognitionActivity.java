package cmsc436.msproject.walkingTest.indoor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cmsc436.msproject.R;

import edu.umd.cmsc436.frontendhelper.TrialMode;
import edu.umd.cmsc436.sheets.Sheets;

public class VoiceRecognitionActivity extends AppCompatActivity implements Sheets.Host {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private Sheets sheet;
    private static String typeOfWalking;
    private static final String CHECK_KEY = "CHECK_KEY";
    private static final String CHECK_KEY2 = "CHECK_KEY2";
    private float userDistPerStepCalib;
    private Button unableToWalk;
    public static final int LIB_ACCOUNT_NAME_REQUEST_CODE = 1001;
    public static final int LIB_AUTHORIZATION_REQUEST_CODE = 1002;
    public static final int LIB_PERMISSION_REQUEST_CODE = 1003;
    public static final int LIB_PLAY_SERVICES_REQUEST_CODE = 1004;


    Intent launchingIntent = WalkingIndoorMenu.startingIntent;
    String patientID = TrialMode.getPatientId(launchingIntent);


   // float stepsPerSec = 0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voice_recognition);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        unableToWalk = (Button) findViewById(R.id.unableToWalk);
        if(patientID == null){

            patientID = "defaultUser";
        }
        sheet = new Sheets(this, this, getString(R.string.app_name),
                "1YvI3CjS4ZlZQDYi5PaiA7WGGcoCsZfLoSFM0IdvdbDU" , "13_ig4ShrqxmNlcZum4Urjm8r5wb-GB03MD6my63mOEI");

        unableToWalk.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v){

            float[] trials = {-1, -1, -1, -1, -1};
            sheet.writeData(Sheets.TestType.INDOOR_WALKING, patientID, -1);
            sheet.writeTrials(Sheets.TestType.INDOOR_WALKING, patientID, trials);
            exitWalkingTest(v);
        }
    });
}

    /*SHEETS INTERFACE CODE*/
    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        this.sheet.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void startReadyButtonPage(final View view)
    {
        int id = view.getId();
        if (id == R.id.noHelp) typeOfWalking = "No Help";
        if (id == R.id.cane) typeOfWalking = "Cane";
        if (id == R.id.walker) typeOfWalking = "Walker";
        if (id == R.id.partner) typeOfWalking = "Partner";

        Intent intent = new Intent(this, StartingInstr.class);
        intent.putExtra("Aide", typeOfWalking);
        startActivity(intent);

    }


    @Override
    public void notifyFinished(Exception e) {
        if (e != null) {
            throw new RuntimeException(e);
        }
        Log.i(getClass().getSimpleName(), "Done");
    }

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

    public void exitWalkingTest(View view)
    {
        finish();
    }


  /*  public void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());


        // Given an hint to the recognizer about what the user is going to say
        //There are two form of language model available
        //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
        //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        //Start the Voice recognizer activity for the result.
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
*/
  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            //If Voice recognition is successful then it returns RESULT_OK
            if(resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    if (textMatchList.get(0).contains("start")) {
                        Intent intent = new Intent(this, WalkingIndoorTest.class);
                        intent.putExtra("Aide", typeOfWalking);
                        //Intent intent2 = new Intent(getApplicationContext(), VoiceRecognitionActivity.class);
                        intent.putExtra("CHECK_KEY2", userDistPerStepCalib);

                        startActivity(intent);
                        finish();

                    } else if (textMatchList.get(0).contains("stop")) {
                        Intent intent = new Intent(this, WalkingIndoorMenu.class);
                        startActivity(intent);
                        finish();
                    }

                }
                //Result code for various error.
            }else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                showToastMessage("Audio Error");
            }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                showToastMessage("Client Error");
            }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                showToastMessage("Network Error");
            }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                showToastMessage("No Match");
            }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
*/
    /**
     * Helper method to show the toast message
     **/
    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void writeFloatToSharePref(float f){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(CHECK_KEY,f);
        editor.apply();

    }

    private void writeFloatToSharePref2(float f){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences.edit();
        editor2.putFloat(CHECK_KEY2,f);
        editor2.apply();

    }
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
