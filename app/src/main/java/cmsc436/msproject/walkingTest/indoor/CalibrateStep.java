package cmsc436.msproject.walkingTest.indoor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cmsc436.msproject.R;

/*This class allows the user to measure a small distance, say 5 feet, and count how many steps
* they take in that distance. This allows a return value of distance traveled per step,
* which is used to be multiplied by number of steps during the test to get total distance.
* The test is stopped after 25feet have been walked.*/
public class CalibrateStep extends AppCompatActivity {
    EditText distanceWalkedText;
    EditText stepsTakenText;
    private float distanceWalked;
    private float stepsTaken;
    private float stepsPerFoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_step);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        distanceWalkedText = (EditText) findViewById(R.id.distanceWalkedValText);
        stepsTakenText = (EditText) findViewById(R.id.stepsTakenValText);
        stepsTaken = 0;
        distanceWalked = 0;

    }

    public void finishedPressed(View v){
        if (distanceWalkedText.getText().toString().equals("") || stepsTakenText.getText().toString().equals(""))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please enter distance walked and steps taken")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        Float temp = new Float(distanceWalkedText.getText().toString());
        distanceWalked = temp.floatValue();
        temp = new Float(stepsTakenText.getText().toString());
        stepsTaken = temp.floatValue();

        stepsPerFoot = distanceWalked/stepsTaken;
        Intent intent = new Intent(getApplicationContext(), VoiceRecognitionActivity.class);
        intent.putExtra("CHECK_KEY", stepsPerFoot);

        //Intent startingVoice = new Intent(getApplicationContext(), VoiceRecognitionActivity.class);
        startActivity(intent);
        finish();

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
