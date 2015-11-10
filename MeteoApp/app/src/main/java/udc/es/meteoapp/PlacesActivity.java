package udc.es.meteoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

public class PlacesActivity extends ActionBarActivity {

    String TAG = "MeteoApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_detail);
        Log.d(TAG, "PlacesActivity: onCreate");

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            Log.d(TAG, "PlacesActivity: onCreate: " + PlacesFragment.ARG_LOCALITY_ID);
            arguments.putString(PlacesFragment.ARG_LOCALITY_ID,
                    getIntent().getStringExtra(PlacesFragment.ARG_LOCALITY_ID));
            PlacesFragment fragment = new PlacesFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.place_detail_container,fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "PlacesActivity: onOptionsItemSelected - button up");
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }

        Log.d(TAG, "PlacesActivity: onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }
}



//package udc.es.meteoapp;
//
//import android.locality_name.Intent;
//import android.support.v7.app.ActionBarActivity;
//import android.os.Bundle;
//import android.widget.TextView;
//
//public class PlacesActivity extends ActionBarActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_places_detail);
//
//        TextView tv_locality_name = (TextView) findViewById(R.locality_id.tv_locality_name);
//
//        Intent intent = getIntent();
//        String locality = intent.getExtras().getString("Place");
//        tv_locality_name.setText(locality);
//
//    }
//}

