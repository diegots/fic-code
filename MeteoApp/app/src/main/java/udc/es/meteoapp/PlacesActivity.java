package udc.es.meteoapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PlacesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        TextView tv_locality_name = (TextView) findViewById(R.id.tv_locality_name);

        Intent intent = getIntent();
        String locality = intent.getExtras().getString("Place");
        tv_locality_name.setText(locality);

    }
}
