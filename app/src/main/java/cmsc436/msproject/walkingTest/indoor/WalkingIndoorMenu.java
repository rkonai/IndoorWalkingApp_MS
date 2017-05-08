package cmsc436.msproject.walkingTest.indoor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;


import cmsc436.msproject.R;

/**
 * This activity is the entry point to the Indoor Walking Test and it displays the Indoor Walking Test menu
 */
public class WalkingIndoorMenu extends Activity {

    static Intent startingIntent;
    static boolean isTrialMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_indoor_menu);

        /*
            WE NEED TO CHANGE THIS BACK TO TRIAL MODDE AFTER THEY IMPLEMENT TRIAL MODE!!!!!!!!!!!!!
            AND THE PATIENT ID IN THE RESULTS CLASS
            !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         */
        //isTrialMode = this.getIntent().getAction().equals("edu.umd.cmsc436.walk.indoors.action.TRIAL");
        isTrialMode = this.getIntent().getAction().equals("edu.umd.cmsc436.walk.indoors.action.PRACTICE");
        startingIntent = this.getIntent();
    /*
        findViewById(R.id.instructionsBtn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), WalkingIndoorInstructions.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.viewProgressBtn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), WalkingIndoorProgress.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.takeTestBtn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), VoiceRecognitionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.calibrateStepLngth).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), CalibrateStep.class);
                startActivity(intent);
                finish();
            }
        });
    */
    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, VoiceRecognitionActivity.class);
        startActivity(intent);
        finish();

    }
}
