package cmsc436.msproject.walkingTest.indoor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import cmsc436.msproject.R;

/**
 * This activity gets the intent that we use in the MainStartingPage
 */
public class WalkingIndoorMenu extends Activity {

    static Intent startingIntent;
    static boolean isTrialMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTrialMode = this.getIntent().getAction().equals("edu.umd.cmsc436.walk.indoors.action.PRACTICE");
        startingIntent = this.getIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, MainStartingPage.class);
        startActivity(intent);
        finish();
    }
}
