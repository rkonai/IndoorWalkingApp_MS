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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cmsc436.msproject.R;

public class VoiceRecognitionActivity extends AppCompatActivity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    private static String typeOfWalking;
    private static final String CHECK_KEY = "CHECK_KEY";
    private static final String CHECK_KEY2 = "CHECK_KEY2";
    private float userDistPerStepCalib;
   // float stepsPerSec = 0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Bundle extras = this.getIntent().getExtras();
        if(extras != null){
            userDistPerStepCalib = extras.getFloat("CHECK_KEY");
            writeFloatToSharePref(userDistPerStepCalib);
        }

        writeFloatToSharePref2(userDistPerStepCalib);

        checkVoiceRecognition();
    }


    public void startSpeach(final View view)
    {
        int id = view.getId();
        if (id == R.id.noHelp) typeOfWalking = "No Help";
        if (id == R.id.cane) typeOfWalking = "Cane";
        if (id == R.id.walker) typeOfWalking = "Walker";
        if (id == R.id.partner) typeOfWalking = "Partner";

        Intent intent = new Intent(this, StartingInstr.class);
        intent.putExtra("Aide", typeOfWalking);
        startActivity(intent);
        finish();
    }


    public void startSpeechPart2(View view)
    {
        speak();
    }

    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
    }

    public void exitWalkingTest(View view)
    {
        finish();
    }


    public void speak() {
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

    @Override
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
