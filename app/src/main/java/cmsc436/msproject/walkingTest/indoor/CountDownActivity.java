package cmsc436.msproject.walkingTest.indoor;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import cmsc436.msproject.R;

public class CountDownActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

    }
    @Override
    protected void onResume() {
        super.onResume();

        new CountDownTimer(6000, 1000) {
            TextView mTextField = (TextView) findViewById(R.id.countdown_textview);

            public void onTick(long millisUntilFinished) {
                mTextField.setText(millisUntilFinished / 1000 + "s");
                vibrate();
            }

            public void onFinish() {
                startTest();
            }
        }.start();

    }

    /**
     * Output a beep to alert the user the calibration has ended
     */

    private void vibrate(){
        Vibrator vibrator = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(70);

    }
    private void startTestBeep(){
        Vibrator vibrator = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1200);
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_RING, 100);
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 1000);
    }

    public void startTest()
    {
        startTestBeep();
        Intent intent = new Intent(this, WalkingIndoorTest.class);
        intent.putExtra("Aide", (String) (this.getIntent().getStringExtra("Aide")));
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
