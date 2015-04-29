package es.udc.psi14.lab09trabazo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class SensActiv extends ActionBarActivity implements
        View.OnClickListener,
        AdapterView.OnItemClickListener,
        SensorEventListener {

    final static String TAG = "LCA_TAG";
    private final static String banner = "[SensActiv] ";

    TextView sens_activ;
    TextView tv_x;
    TextView tv_y;
    TextView tv_z;
    Button but_check;
    ListView list_view;

    SensorManager sensorManager;
    List<Sensor> sensorList;
    ArrayAdapter<String> sensorArrayAdapter;

    void initViews () {
        sens_activ = (TextView) findViewById(R.id.sens_activ);
        tv_x = (TextView) findViewById(R.id.tv_x);
        tv_y = (TextView) findViewById(R.id.tv_y);
        tv_z = (TextView) findViewById(R.id.tv_z);
        but_check = (Button) findViewById(R.id.but_check);
        list_view = (ListView) findViewById(R.id.list_view);

        but_check.setOnClickListener(this);
        list_view.setOnItemClickListener(this);
    }

    void initVariables () {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sens);

        initViews();
        initVariables();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sens, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        Iterator<Sensor> is = sensorList.iterator();

        while (is.hasNext())
            sensorArrayAdapter.add(is.next().getName());
        list_view.setAdapter(sensorArrayAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Sensor mySens = sensorList.get((int) id);
        sensorManager.unregisterListener(this);
        sensorManager.registerListener(this, mySens, SensorManager.SENSOR_DELAY_NORMAL);

        sens_activ.setText(mySens.getName().toString());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        sens_activ.setText(event.sensor.getName());
        tv_x.setText("" + event.values[0]);
        if (event.values.length >= 3) {
            tv_y.setText(""+event.values[1]);
            tv_z.setText(""+event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
