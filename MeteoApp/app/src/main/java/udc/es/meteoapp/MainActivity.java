package udc.es.meteoapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get localities from resource file and fill the ListView
        final String[] locality = getResources().getStringArray(R.array.locality);
        ListView localityListView = (ListView) findViewById(R.id.localityListView);
        localityListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locality));

        AdapterView.OnItemClickListener oicl = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tv = (TextView) view;
                // Toast.makeText(getApplicationContext(), "item: " + tv.getText(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
                intent.putExtra("Place", tv.getText());
                startActivity(intent);

            }
        };
        localityListView.setOnItemClickListener(oicl);
    }



}
