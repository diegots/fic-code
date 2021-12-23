package es.udc.psi14.lab09trabazo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Iterator;
import java.util.List;

public class SensActiv extends ActionBarActivity implements
        View.OnClickListener,
        AdapterView.OnItemClickListener,
        SensorEventListener,
        CompoundButton.OnCheckedChangeListener {

    final static String TAG = "LCA_TAG";
    private final static String banner = "[SensActiv] ";

    TextView sens_activ;
    TextView tv_x;
    TextView tv_y;
    TextView tv_z;
    Button but_check;
    ListView list_view;
    ToggleButton tb_alarm;
    EditText et_time;
    EditText et_url;

    SensorManager sensorManager;
    List<Sensor> sensorList;
    ArrayAdapter<String> sensorArrayAdapter;
    AlarmManager alarmManager;
    PendingIntent alarmPendingIntent;

    void setAlarm () {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(et_url.getText().toString()));
        alarmPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + Integer.parseInt(et_time.getText().toString()),
            alarmPendingIntent);
    }

    void initViews () {
        sens_activ = (TextView) findViewById(R.id.sens_activ);
        tv_x = (TextView) findViewById(R.id.tv_x);
        tv_y = (TextView) findViewById(R.id.tv_y);
        tv_z = (TextView) findViewById(R.id.tv_z);
        but_check = (Button) findViewById(R.id.but_check);
        list_view = (ListView) findViewById(R.id.list_view);
        tb_alarm = (ToggleButton) findViewById(R.id.tb_alarm);
        et_time = (EditText) findViewById(R.id.et_time);
        et_url = (EditText) findViewById(R.id.et_url);

        but_check.setOnClickListener(this);
        list_view.setOnItemClickListener(this);
        tb_alarm.setOnCheckedChangeListener(this);
    }

    void initVariables () {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sens);

        initViews();
        initVariables();

        // Avoid showing keyboard on activity start.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Log.d(TAG, banner + "onCreate");
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

        Log.d(TAG, banner + "onClick");
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        // Assuming than app is going to Paused state because of alarm, so change toggle button
        // state.
        tb_alarm.setChecked(false);

        Log.d(TAG, banner + "onPause");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Sensor mySens = sensorList.get((int) id);
        sensorManager.unregisterListener(this);
        sensorManager.registerListener(this, mySens, SensorManager.SENSOR_DELAY_NORMAL);

        sens_activ.setText(mySens.getName().toString());

        Log.d(TAG, banner + "onItemClick");
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
        Log.d(TAG, banner + "onAccuracyChanged");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            tb_alarm.setChecked(true);
            setAlarm();
            Log.d(TAG, banner + "onCheckedChanged: toggle but to true & setting alarm");
        }
        else {
            tb_alarm.setChecked(false);
            alarmManager.cancel(alarmPendingIntent);
            Log.d(TAG, banner + "onCheckedChanged: toggle but to false");

        }
    }
}
