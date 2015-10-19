package udc.es.meteoapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get localities from resource file and fill the ListView
        final String[] locality = getResources().getStringArray(R.array.locality);
        ListView localityListView = (ListView) findViewById(R.id.localityListView);
        localityListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locality));
    }
}
