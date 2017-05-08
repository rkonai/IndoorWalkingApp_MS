package cmsc436.msproject.walkingTest.indoor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import cmsc436.msproject.R;

/**
 * This activity displays the user's previous test results for the Indoor Walking test
 */
public class WalkingIndoorProgress extends AppCompatActivity {
    RelativeLayout rootView;
    ScrollView scrollView;
    TextView dateColTextView;
    TextView speedColTextView;
    Button clearBtn;

    /**
     * The TableLayout that contains the previous test results
     */
    private TableLayout tableLayout;

    /**
     * Width of the date column in the TableLayout
     */
    private int dateColWidth;

    /**
     * Width of the speed column in the TableLayout
     */
    private int speedColWidth;

    private boolean loadedData = false;

    private static final String LOG_TAG = WalkingIndoorProgress.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_indoor_progress);

        rootView = (RelativeLayout) findViewById(R.id.relativeLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        dateColTextView = (TextView) findViewById(R.id.dateCol);
        speedColTextView = (TextView) findViewById(R.id.speedCol);
        clearBtn = (Button) findViewById(R.id.clearDataBtn);

        // Initialize the tableLayout
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        // Initialize the width of table columns
        dateColWidth = findViewById(R.id.dateCol).getWidth();
        speedColWidth = findViewById(R.id.speedCol).getWidth();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(LOG_TAG, "onWindowFocusChanged() - hasFocus = " + hasFocus);
        if (hasFocus) {
            setFirstTableRowHeight();
            updateTable();
            setScrollViewHeight();
        }
    }

    /**
     * Clear the stored results
     */
    public void clearSavedResults(View view) {
        File dataFile = new File(this.getFilesDir(), WalkingIndoorUtil.STORAGE_FILE);
        if (dataFile.exists()){
            if(dataFile.delete()){
                Toast.makeText(this, "Deleted saved results", Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
                finish();
            }
            else{
                Toast.makeText(this, "Error deleting saved results", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Unable to deleted file " + dataFile.getAbsoluteFile());
            }
        }
    }

    /**
     * Dynamically set all heights of the columns in the first row of the table to be the same
     */
    private void setFirstTableRowHeight(){
        Log.d(LOG_TAG, "setFirstTableRowHeight()");
        // get the height of the test date cell
        int dateCellHeight = dateColTextView.getHeight();

        // get the height of the test speed cell
        int speedCellHeight = speedColTextView.getHeight();

        // if the two cells are not the same height
        if (dateCellHeight < speedCellHeight){
            TableRow.LayoutParams tableParam = new TableRow.LayoutParams(0, speedCellHeight, 1f);
            dateColTextView.setLayoutParams(tableParam);
        }
    }

    /**
     * Update the TableView with the user's test data results
     */
    private void updateTable(){
        if (loadedData){
            return;
        }
        File file = new File(this.getFilesDir(), WalkingIndoorUtil.STORAGE_FILE);
        if (file.exists()){
            FileInputStream inputStream;
            try {
                inputStream = openFileInput(WalkingIndoorUtil.STORAGE_FILE);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    int index = line.indexOf(",");
                    String date = line.substring(0, index);
                    String speed = line.substring(index + 1);
                    Log.d(LOG_TAG, "date = \"" + date + "\", speed = \"" + speed + "\"");

                    TableRow tr = new TableRow(this);
                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT));

                    addTableCol(tr, date, dateColWidth);
                    addTableCol(tr, speed, speedColWidth);
                    tableLayout.addView(tr);
                }
                bufferedReader.close();
                inputStream.close();
                loadedData = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add a column to a TableRow
     *
     * @param tr The TableRow to modify
     * @param data The column cell data
     * @param cellWidth The width of the column cell
     */
    private void addTableCol(TableRow tr, String data, int cellWidth) {
        TextView tableCell = new TextView(this);
        tableCell.setText(data);
        tableCell.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.textSize));
        tableCell.setGravity(Gravity.CENTER);
        tableCell.setBackgroundResource(R.drawable.cell_shape);

        TableRow.LayoutParams cellParam = new TableRow.LayoutParams(cellWidth,
                TableRow.LayoutParams.MATCH_PARENT);

        tr.addView(tableCell, cellParam);
    }

    /**
     * Dynamically set the height of the ScrollView so it fits after the TextView label and before the menu button
     */
    private void setScrollViewHeight(){
        int relLayoutHeight = rootView.getHeight();
        int buttonHeight = clearBtn.getHeight();
        int scrollViewHeight = relLayoutHeight - buttonHeight;

        RelativeLayout.LayoutParams scrollViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, scrollViewHeight);
        scrollViewParams.addRule(RelativeLayout.ABOVE, R.id.clearDataBtn);
        scrollViewParams.setMargins(10, 20, 10, 0);
        scrollView.setLayoutParams(scrollViewParams);
    }
}
