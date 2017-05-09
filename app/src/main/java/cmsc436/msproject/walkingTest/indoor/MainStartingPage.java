package cmsc436.msproject.walkingTest.indoor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import cmsc436.msproject.R;

import edu.umd.cmsc436.frontendhelper.TrialMode;
import edu.umd.cmsc436.sheets.Sheets;

public class MainStartingPage extends AppCompatActivity implements Sheets.Host {

    private Sheets sheet;
    private static String typeOfWalking;
    private Button unableToWalk;
    public static final int LIB_ACCOUNT_NAME_REQUEST_CODE = 1001;
    public static final int LIB_AUTHORIZATION_REQUEST_CODE = 1002;
    public static final int LIB_PERMISSION_REQUEST_CODE = 1003;
    public static final int LIB_PLAY_SERVICES_REQUEST_CODE = 1004;


    Intent launchingIntent = WalkingIndoorMenu.startingIntent;
    String patientID = TrialMode.getPatientId(launchingIntent);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_starting_page);

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


    public void startReadyButtonPage(final View view){
        int id = view.getId();
        if (id == R.id.noHelp) typeOfWalking = "No Help";
        if (id == R.id.cane) typeOfWalking = "Cane";
        if (id == R.id.walker) typeOfWalking = "Walker";
        if (id == R.id.partner) typeOfWalking = "Partner";

        Intent intent = new Intent(this, ReadyButtonPage.class);
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
