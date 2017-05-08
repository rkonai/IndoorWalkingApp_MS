package cmsc436.msproject.walkingTest.indoor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cmsc436.msproject.R;
/**
 * This activity displays the instructions for the Indoor Walking test
 */
public class WalkingIndoorInstructions extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_indoor_instructions);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void exitInstructions(View view) {
        finish();
    }
}
