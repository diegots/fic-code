package udc.es.meteoapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class MainActivity extends ActionBarActivity implements
        MainFragment.OnItemSelectedListener {

    String TAG = "MeteoApp";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity: onCreate");

        if (findViewById(R.id.place_detail_container) != null) {
            Log.d(TAG, "MainActivity: onCreate - two panes");
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            // TODO
        }
    }


    @Override
    public void onItemSelected(String locality_id) {
        Log.d(TAG, "MainActivity: onItemSelected");

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            // TODO
            Log.d(TAG, "MainActivity: onItemSelected - two pane");

        } else {
            Log.d(TAG, "MainActivity: onItemSelected - one pane - locality_id: " + locality_id);
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent placesIntent = new Intent(this, PlacesActivity.class);
            placesIntent.putExtra(PlacesFragment.ARG_LOCALITY_ID, locality_id);
            startActivity(placesIntent);
        }
    }
}